/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobNotifier.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.tools;

import org.quartz.JobExecutionContext;

import com.hd123.baas.sop.job.entity.JobInstanceResult;
import com.hd123.baas.sop.job.exception.JobNotificationException;

/**
 * 指示类提供通知作业执行结果的功能。一种典型的实现是将作业执行结果以邮件方式发出通知。
 * 
 * @author huzexiong
 * @since 1.0
 * 
 */
public interface JobNotifier {

  /**
   * 发出通知。通常将在作业执行结束后被调用。
   * 
   * @param jobResult
   *          作业执行结果。禁止传入null。
   * @param caught
   *          捕获自作业执行过程中的异常对象，当jobResult为{@link JobInstanceResult#completed}
   *          时取值为null。
   * @param context
   *          作业执行上下文对象。禁止传入null。
   * @throws JobNotificationException
   * @throws IllegalArgumentException
   *           当参数executionContext或jobResult为null时抛出。
   */
  void nodify(JobInstanceResult jobResult, Exception caught, JobExecutionContext context)
      throws JobNotificationException, IllegalArgumentException;
}
