package com.hd123.baas.sop.utils;

import com.qianfan123.baas.common.BaasException;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisDistributedLocker {

  /**
   * 每个key的过期时间 {@link LockContent}
   */
  private Map<String, LockContent> lockerContents = new ConcurrentHashMap<>(512);

  @Autowired
  private StringRedisTemplate redisTemplate;

  public RedisDistributedLocker() {
    ScheduleTask task = new ScheduleTask(this, lockerContents);

    long delay = TimeUnit.SECONDS.toMillis(1);
    long period = TimeUnit.SECONDS.toMillis(1);
    // 定时执行
    new Timer("Lock-Renew-Task").schedule(task, delay, period);
  }

  public enum LockPolicy {
    wait, exception
  }

  public String lock(String lockKey, LockPolicy policy) throws BaasException {
    String lockValue = UUID.randomUUID().toString();
    lock(lockKey, lockValue, policy);
    return lockValue;
  }

  public void lock(String lockKey, String lockValue, LockPolicy policy) throws BaasException {
    log.info("开始执行加锁, lockKey ={}", lockKey);
    for (int i = 0; i < 50; i++) {  // 尝试加锁50 * 100ms = 5s
      // 判断是否已经有线程持有锁，减少redis的压力
      LockContent lockContent = lockerContents.get(lockKey);
      // 如果没有被锁，就获取锁
      if (lockContent == null) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 5, TimeUnit.SECONDS);
        if (success == Boolean.TRUE) {
          // 将锁放入map
          lockContent = new LockContent();
          lockContent.setLockKey(lockKey);
          lockContent.setLockValue(lockValue);
          lockContent.setThread(Thread.currentThread());
          lockerContents.put(lockKey, lockContent);
          log.info("加锁成功, lockKey ={}, requestId={}", lockKey, lockValue);
          return;
        }
      } else if (Thread.currentThread() == lockContent.getThread()
              && lockValue.equals(lockContent.getLockValue())) {
        // 重复获取锁，在线程池中由于线程复用，线程相等并不能确定是该线程的锁
        return;
      }

      if (policy == LockPolicy.exception) {
        throw new BaasException("资源已锁定，请稍后再试");
      }
      // 如果被锁或获取锁失败，则等待100毫秒
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        log.error("获取redis 锁失败, lockKey ={}" + lockKey, e);
        throw new BaasException("资源繁忙，请稍后再试");
      }
    }
    throw new BaasException("资源繁忙，请稍后再试");
  }

  /**
   * 解锁
   */
  public void unlock(String lockKey, String lockValue) throws BaasException {
    LockContent lockContent = lockerContents.get(lockKey);

    long consumeTime;
    if (lockContent == null) {
      return;
    } else if (lockValue.equals(lockContent.getLockValue()) == false) {
      throw new BaasException("资源异常，请稍后再试");
    }

    redisTemplate.delete(lockKey);
    lockerContents.remove(lockKey);
  }

  /**
   * 续约
   */
  public boolean renew(String lockKey, LockContent lockContent) {
    // 检测执行业务线程的状态
    Thread.State state = lockContent.getThread().getState();
    if (Thread.State.TERMINATED == state) {
      log.info("执行业务的线程已终止,不再续约 lockKey ={}, lockContent={}", lockKey, lockContent);
      return false;
    }

    Boolean success = redisTemplate.expire(lockKey, 5, TimeUnit.SECONDS);
    return success == Boolean.TRUE;
  }

  @Data
  public static class LockContent {
    private String lockKey;
    private String lockValue;
    private Thread thread;
  }

  public static class ScheduleTask extends TimerTask {

    private final RedisDistributedLocker redisDistributionLock;
    private final Map<String, LockContent> lockContentMap;

    public ScheduleTask(RedisDistributedLocker redisDistributionLock, Map<String, LockContent> lockContentMap) {
      this.redisDistributionLock = redisDistributionLock;
      this.lockContentMap = lockContentMap;
    }

    @SneakyThrows
    @Override
    public void run() {
      if (lockContentMap.isEmpty()) {
        return;
      }

      Set<Map.Entry<String, LockContent>> entries = lockContentMap.entrySet();
      for (Map.Entry<String, LockContent> entry : entries) {
        String lockKey = entry.getKey();
        LockContent lockContent = entry.getValue();
        // 减少线程池中任务数量
        boolean success = redisDistributionLock.renew(lockKey, lockContent);
        if (success == false) {// 续约失败，说明已经执行完 OR redis 出现问题
          redisDistributionLock.unlock(lockKey, lockContent.getLockValue());
        }
      }
    }
  }
}
