package com.hd123.baas.sop.job;

import javax.annotation.PostConstruct;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobStarter {
  private static final Logger LOG = LoggerFactory.getLogger(JobStarter.class);
  private Scheduler scheduler;
  private boolean allowStart;

  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public void setAllowStart(boolean allowStart) {
    this.allowStart = allowStart;
  }

  @PostConstruct
  public void schedule() {
    log.debug("调度开始");
    if (allowStart) {
      try {
        scheduler.start();
      } catch (SchedulerException e) {
        log.error("启动调度失败", e);
      }
    }
    log.debug("调度结束:" + (allowStart ? "启用Quartz服务，允许调度" : "启用Quartz服务,禁止调度"));
  }
}
