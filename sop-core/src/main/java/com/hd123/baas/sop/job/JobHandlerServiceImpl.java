/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	ExcelHandlerServiceImpl.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.*;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.job.dao.JobInstanceDao;
import com.hd123.baas.sop.job.entity.PJobInstance;
import com.hd123.baas.sop.job.tools.JobScheduleHandler;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.util.SystemClock;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang
 *
 */
@Service
@Slf4j
public class JobHandlerServiceImpl implements JobHandlerService, ApplicationContextAware {

  private Scheduler scheduler;

  private ApplicationContext applicationContext;

  @Override
  public JobScheduleHandler startJob(String name, Class<? extends Job> job, long afterMinutes,
      Map<String, Object> dataMap) throws Exception {
    long currentTime = SystemClock.millis().now();
    log.info("生成时间截");
    currentTime = currentTime + afterMinutes * 60 * 1000;
    Trigger trigger = TriggerBuilder.newTrigger().startAt(new Date(currentTime)).build();
    return startJob(name, job, trigger, dataMap);
  }

  @Override
  public JobScheduleHandler startJob(String name, Class<? extends Job> job, Trigger trigger,
      Map<String, Object> dataMap) throws Exception {
    Assert.assertAttributeNotNull(getScheduler(), "scheduler");
    Assert.assertAttributeNotNull(job, "job");

    log.info("生成jobKey");
    JobScheduleHandler handler = new JobScheduleHandler(name, job.getName());
    JobKey jobKey = new JobKey(handler.getName(), handler.getGroup());

    getScheduler().getContext()
        .put(JobInstanceDao.class.getName(), this.applicationContext.getBean(JobInstanceDao.class));

    if (getScheduler().checkExists(jobKey)) {
      log.info("job运行中");
      boolean executing = false;
      for (JobExecutionContext context : scheduler.getCurrentlyExecutingJobs()) {
        if (jobKey.equals(context.getJobDetail().getKey())) {
          handler.setScheduled(context.getFireTime());
          executing = true;
          break;
        }
      }
      if (!executing) {
        log.info("触发JOB");
        getScheduler().getJobDetail(jobKey).getJobDataMap().putAll(dataMap);
        getScheduler().triggerJob(jobKey);
      }
    } else {
      log.info("创建JOB");
      JobDetail jobDetail = JobBuilder.newJob(job).withIdentity(jobKey).build();
      if (null != dataMap && !dataMap.isEmpty()) {
        jobDetail.getJobDataMap().putAll(dataMap);
      }
      log.info("提交JOB");
      getScheduler().scheduleJob(jobDetail, trigger);
    }
    log.info("完成");
    return handler;
  }

  @Override
  public JobScheduleHandler startJob(String name, Class<? extends Job> job, Map<String, Object> dataMap)
      throws Exception {
    dataMap.put(AbstractJob.TRACE_ID, MDC.get(AbstractJob.TRACE_ID));
    Trigger trigger = TriggerBuilder.newTrigger().startNow().build();
    return startJob(name, job, trigger, dataMap);
  }

  @Override
  public void runJob(String name, Class<? extends Job> job, Map<String, Object> dataMap) throws Exception {
    Assert.assertAttributeNotNull(getScheduler(), "scheduler");
    Assert.assertAttributeNotNull(job, "job");

    JobKey jobKey = new JobKey(name, job.getName());

    if (getScheduler().checkExists(jobKey)) {
      boolean executing = false;
      for (JobExecutionContext context : scheduler.getCurrentlyExecutingJobs()) {
        if (jobKey.equals(context.getJobDetail().getKey())) {
          executing = true;
          break;
        }
      }
      if (!executing) {
        getScheduler().getJobDetail(jobKey).getJobDataMap().putAll(dataMap);
        getScheduler().triggerJob(jobKey);
      }
    } else {
      JobDetail jobDetail = JobBuilder.newJob(job).withIdentity(jobKey).build();
      if (null != dataMap && !dataMap.isEmpty()) {
        jobDetail.getJobDataMap().putAll(dataMap);
      }
      Trigger trigger = TriggerBuilder.newTrigger().startNow().build();
      getScheduler().scheduleJob(jobDetail, trigger);
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public Scheduler getScheduler() {
    if (null == scheduler) {
      scheduler = this.applicationContext.getBean(Scheduler.class);
    }
    return scheduler;
  }

  @Override
  public void interrupt(String tenant, String shop, String jobName)
      throws UnableToInterruptJobException, BaasException {
    PJobInstance p = this.applicationContext.getBean(JobInstanceDao.class).getByName(tenant, jobName);
    if (p != null) {
      JobScheduleHandler handler = new JobScheduleHandler(jobName, p.getDetail().getJobClassName());
      JobKey jobKey = new JobKey(handler.getName(), handler.getGroup());
      getScheduler().interrupt(jobKey);
    } else {
      throw new BaasException("不存在这个任务");
    }
  }

  @Override
  public void removeJob(String jobName, Class<? extends Job> job) {
    try {
      JobKey jobKey = new JobKey(jobName, job.getName());
      // 中断任务
      getScheduler().interrupt(jobKey);
      // 删除任务
      getScheduler().deleteJob(jobKey);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean contain(String jobName, Class<? extends Job> job) throws SchedulerException {
    log.info("生成jobKey");
    JobScheduleHandler handler = new JobScheduleHandler(jobName, job.getName());
    JobKey jobKey = new JobKey(handler.getName(), handler.getGroup());
    return getScheduler().checkExists(jobKey);
  }

  @Override
  public List<? extends Trigger> getTriggersOfJob(String jobName, Class<? extends Job> job) throws SchedulerException {
    JobScheduleHandler handler = new JobScheduleHandler(jobName, job.getName());
    JobKey jobKey = new JobKey(handler.getName(), handler.getGroup());
    return getScheduler().getTriggersOfJob(jobKey);
  }

}
