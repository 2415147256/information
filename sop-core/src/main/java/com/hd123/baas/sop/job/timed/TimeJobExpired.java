package com.hd123.baas.sop.job.timed;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author zhengzewang
 */
public class TimeJobExpired implements Job {

  // 查出过期任务，将过期任务放入消息队列
  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

  }
}
