package com.hd123.baas.sop.job.bean;

import java.util.UUID;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2019/9/4.
 */
@Slf4j
@DisallowConcurrentExecution
public class DemoJob implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    try {
      MDC.put("trace_id", UUID.randomUUID().toString());
      log.info("进入demo job");
    } catch (Throwable e) {
      log.error("demo job:", e);
    }
  }

}
