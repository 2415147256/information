/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobInstance.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author huzexiong
 *
 */
public class PJobInstance implements Serializable {

  public static final String TABLE_NAME = "RBQZ_JOB_INSTANCE";
  public static final String INSTANCE_ID = "instance_id";
  public static final String FSTATE = "fstate";
  public static final String SCHEDULER_NAME = "scheduler_name";
  public static final String SCHEDULER_INSTANCE_ID = "scheduler_instance_id";
  public static final String JOB_GROUP = "job_group";
  public static final String JOB_NAME = "job_name";
  public static final String JOB_CLASS_NAME = "job_class_name";
  public static final String JOB_DATA_MAP = "job_data_map";
  public static final String JOB_DESCRIPTION = "job_description";
  public static final String TRIGGER_GROUP = "trigger_group";
  public static final String TRIGGER_NAME = "trigger_name";
  public static final String TRIGGER_CLASS_NAME = "trigger_class_name";
  public static final String CRON_EXPRESSION = "cron_expression";
  public static final String TRIGGER_DATA_MAP = "trigger_data_map";
  public static final String TRIGGER_START_TIME = "trigger_start_time";
  public static final String TRIGGER_END_TIME = "trigger_end_time";
  public static final String TRIGGER_DESCRIPTION = "trigger_description";
  public static final String DATA_MAP = "data_map";
  public static final String STARTED_AT = "started_at";
  public static final String FINISHED_AT = "finished_at";
  public static final String FRESULT = "fresult";
  public static final String DESCRIPTION = "description";

  private static final long serialVersionUID = -7986689959097387615L;

  private String instanceId;
  private JobInstanceState state = JobInstanceState.executing;
  private PSchedulerInfo scheduler;
  private PJobDetailInfo detail;
  private PTriggerInfo trigger;
  private String dataMap;
  private Date startedAt;
  private Date finishedAt;
  private JobInstanceResult result;

  /** 作业实例唯一标识。 */
  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public JobInstanceState getState() {
    return state;
  }

  public void setState(JobInstanceState state) {
    this.state = state;
  }

  /** 作业调度器信息。 */
  public PSchedulerInfo getScheduler() {
    return scheduler;
  }

  public void setScheduler(PSchedulerInfo scheduler) {
    this.scheduler = scheduler;
  }

  /** 作业明细信息。 */
  public PJobDetailInfo getDetail() {
    return detail;
  }

  public void setDetail(PJobDetailInfo detail) {
    this.detail = detail;
  }

  /** 触发器信息。 */
  public PTriggerInfo getTrigger() {
    return trigger;
  }

  public void setTrigger(PTriggerInfo trigger) {
    this.trigger = trigger;
  }

  /** 数据映射表。 */
  public String getDataMap() {
    return dataMap;
  }

  public void setDataMap(String dataMap) {
    this.dataMap = dataMap;
  }

  /** 作业启动时刻。 */
  public Date getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(Date startedAt) {
    this.startedAt = startedAt;
  }

  /** 作业执行结束时刻。 */
  public Date getFinishedAt() {
    return finishedAt;
  }

  public void setFinishedAt(Date finishedAt) {
    this.finishedAt = finishedAt;
  }

  /** 作业执行结果。允许为null，意味着作业尚未执行结束。 */
  public JobInstanceResult getResult() {
    return result;
  }

  public void setResult(JobInstanceResult result) {
    this.result = result;
  }

}
