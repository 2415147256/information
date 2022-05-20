/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	ExcelHandlerService.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job;

import java.util.List;
import java.util.Map;

import org.quartz.*;

import com.hd123.baas.sop.job.tools.JobScheduleHandler;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang
 *
 */
public interface JobHandlerService {
  /**
   * 中断job并删除
   * 
   * @param jobName
   *          任务名称
   * @param job
   *          Job.class
   */
  void removeJob(String jobName, Class<? extends Job> job);

  /**
   * 判断是否含有job
   *
   * @param jobName
   *          任务名称
   * @param job
   *          Job.class
   */
  boolean contain(String jobName, Class<? extends Job> job) throws SchedulerException;

  /**
   * 返回trigger
   * 
   * @param jobName
   * @param job
   * @return
   */
  List<? extends Trigger> getTriggersOfJob(String jobName, Class<? extends Job> job) throws SchedulerException;

  /**
   * 启动系统中自己实现的AbstractJob作业。<br/>
   * 可指定几分钟后执行
   *
   * @return 返回作业调度句柄。
   */
  JobScheduleHandler startJob(String name, Class<? extends Job> job, long afterMinutes, Map<String, Object> dataMap)
      throws Exception;

  /**
   * 启动系统中自己实现的AbstractJob作业。<br/>
   * 可指定触发器执行
   *
   * @return 返回作业调度句柄。
   */
  JobScheduleHandler startJob(String name, Class<? extends Job> job, Trigger trigger, Map<String, Object> dataMap)
      throws Exception;

  /**
   * 启动系统中自己实现的AbstractJob作业。
   * 
   * @return 返回作业调度句柄。
   */
  JobScheduleHandler startJob(String name, Class<? extends Job> job, Map<String, Object> dataMap) throws Exception;

  /**
   * 启动原生的Job
   * 
   * @param name
   * @param job
   * @param dataMap
   * @throws Exception
   */
  void runJob(String name, Class<? extends Job> job, Map<String, Object> dataMap) throws Exception;

  Scheduler getScheduler();

  /**
   * 中断任务
   * 
   * @param jobName
   */
  void interrupt(String tenant, String shop, String jobName) throws UnableToInterruptJobException, BaasException;
}
