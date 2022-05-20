/*
 * 版权所有 (c) 2020. 上海海鼎信息工程股份有限公司，保留所有权利。
 */
package com.hd123.baas.sop.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 缓存服务
 *
 * @author BinLee
 * @since 1.7
 */
@Slf4j
@Service
public class RedisService {

  @Autowired
  private StringRedisTemplate redisTemplate;

  public String get(String cacheKey) {
    return redisTemplate.opsForValue().get(cacheKey);
  }

  public <T> T get(String cacheKey, Class<T> clazz) {
    String value = redisTemplate.opsForValue().get(cacheKey);
    return value == null ? null : JsonUtil.jsonToObject(value, clazz);
  }

  public void set(String key, Object value, long timeout, TimeUnit unit) {
    redisTemplate.opsForValue().set(key, JsonUtil.objectToJson(value), timeout, unit);
  }

  public String buildKey(String... items) {
    StringBuilder sb = new StringBuilder();
    for (String item : items) {
      if (sb.length() > 0) {
        sb.append(":");
      }
      sb.append(item);
    }
    return sb.toString();
  }

}