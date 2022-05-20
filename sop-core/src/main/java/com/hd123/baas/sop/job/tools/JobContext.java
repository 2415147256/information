/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobContext.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.tools;

import java.text.MessageFormat;
import java.util.HashMap;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hd123.rumba.commons.lang.Assert;

/**
 * 作业执行环境上下文。其中所有提供的数据，以及基于这些数据的功能，都是针对当前正在执行的作业实例的。
 * <p>
 * 
 * @author huzexiong
 * @since 1.0
 * 
 */
public class JobContext {

  private static final Logger logger = LoggerFactory.getLogger(JobContext.class);
  private static final ThreadLocal<JobExecutionContext> localContext = new ThreadLocal<JobExecutionContext>();
  private static final ThreadLocal<Progresser> localProgresser = new ThreadLocal<Progresser>();

  private static final String DATA_MAP_KEY_OF_INTERRUPTED = "RB$Interrupted";

  /**
   * 初始化。
   * 
   * @param context
   *          禁止传入null。
   * @throws IllegalArgumentException
   */
  public static void initial(JobExecutionContext context) throws IllegalArgumentException {
    Assert.assertArgumentNotNull(context, "context");
    localContext.set(context);
    localProgresser.set(null);
    // 必须在localContext.set(context)之后。
    logger.debug(MessageFormat.format("initial job context: instance-id={0}", context.getFireInstanceId()));
  }

  /**
   * 释放。
   */
  public static void free() {
    logger.debug("Free job context.");
    localContext.set(null);
  }

  /**
   * 返回来自Quartz的作业执行期间上下文对象{@link JobExecutionContext}。
   * 
   * @return 返回null意味着当前并不处于作业运行环境中。
   */
  public static JobExecutionContext getContext() {
    return localContext.get();
  }

  /**
   * 取得当前作业实例唯一标识。
   * 
   * @return 返回null意味着当前并不处于作业运行环境中。
   */
  public static String getJobInstanceId() {
    if (getContext() == null) {
      return null;
    } else {
      return getContext().getFireInstanceId();
    }
  }

  /**
   * 从当前数据对象映射表中取得指定键对应的取值。
   * 
   * @param key
   * @return 返回null意味着当前并不处于作业运行环境中。
   */
  public static Object get(String key) {
    if (getContext() == null) {
      return null;
    } else {
      return getContext().get(key);
    }
  }

  /**
   * 在当前数据对象映射表中设置指定键对应的取值。<br>
   * 当不处于作业运行环境中时，操作将被忽略。
   * 
   * @param key
   * @param value
   */
  public static void put(String key, Object value) {
    if (getContext() != null) {
      getContext().put(key, value);
    }
  }

  /**
   * 取得当前作业数据对象映射表。
   * 
   * @return 返回null意味着当前并不处于作业运行环境中。
   */
  public static JobDataMap getMergedJobDataMap() {
    if (getContext() == null) {
      return null;
    } else {
      return getContext().getMergedJobDataMap();
    }
  }

  /**
   * 取得当前作业明细对象。
   * 
   * @return 返回null意味着当前并不处于作业运行环境中。
   */
  public static JobDetail getJobDetail() {
    if (getContext() == null) {
      return null;
    } else {
      return getContext().getJobDetail();
    }
  }

  /**
   * 返回是否已经被来自作业外部的请求中断。
   * 
   * @return 当不处于作业运行环境中时将始终返回false。
   */
  public static boolean isInterrupted() {
    if (Thread.interrupted()) {
      return true;
    }
    if (getContext() == null) {
      return false;
    } else {
      Boolean interrupted = (Boolean) getContext().get(DATA_MAP_KEY_OF_INTERRUPTED);
      return interrupted == null ? false : interrupted.booleanValue();
    }
  }

  /**
   * 请求中断。
   */
  public static void interrupt(JobExecutionContext context) throws IllegalArgumentException {
    Assert.assertArgumentNotNull(context, "context");
    context.put(DATA_MAP_KEY_OF_INTERRUPTED, Boolean.TRUE);
  }

  /**
   * 返回作业当前是否允许被中断。
   * 
   * @return 当不处于作业运行环境中时将始终返回false。
   */
  public static boolean isAllowInterrupt() {
    if (getContext() == null) {
      return false;
    }
    Boolean allowInterrupt = (Boolean) get(JobDatas.KEY_ALLOW_INTERRUPT);
    return allowInterrupt == null ? JobDatas.DEFAULT_ALLOW_INTERRUPT : allowInterrupt.booleanValue();
  }

  /**
   * 设置作业当前是否允许被中断。此方法可以用于通知作业外部，现在暂时不接受中断请求，从而使得监控界面获得更好的体验。<br>
   * 当不处于作业运行环境中时，操作将被忽略。
   * 
   * @param allowInterrupt
   */
  public static void setAllowInterrupt(boolean allowInterrupt) {
    put(JobDatas.KEY_ALLOW_INTERRUPT, Boolean.valueOf(allowInterrupt));
  }

  /**
   * 取得当前的执行进度写入器对象。<br>
   * 无论是否处于作业运行环境中，将始终能够获得有效的返回对象。
   */
  public static Progresser getProgresser() {
    Progresser progresser = localProgresser.get();
    if (progresser == null) {
      if (getContext() == null) {
        progresser = new Progresser(new HashMap<String, Object>());
      } else {
        progresser = new Progresser(getContext().getMergedJobDataMap());
      }
      localProgresser.set(progresser);
    }
    return progresser;
  }

  /**
   * 检查点。<br>
   * 作业开发者需要确保在作业执行期间，足够“频繁”地调用检查点，从而及时响应来自外部的中断请求。
   * 
   * @throws InterruptedException
   *           当发生来自作业外部的中断请求时抛出。
   */
  public static void cp() throws InterruptedException {
    logger.debug("checkpoint: " + isInterrupted());
    if (isInterrupted()) {
      throw new InterruptedException();
    }
  }
}
