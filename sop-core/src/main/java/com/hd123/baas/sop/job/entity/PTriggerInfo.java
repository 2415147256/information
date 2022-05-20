/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	TriggerInfo.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 触发器信息
 * 
 * @author huzexiong
 * @since 1.0
 */
public class PTriggerInfo implements Serializable {

  private static final long serialVersionUID = 2293608918981806438L;

  private String group;
  private String name;
  private String triggerClassName;
  private String cronExpression;
  private String dataMap;
  private Date startTime;
  private Date endTime;
  private String description;

  /** 组名。 */
  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  /** 名称。 */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /** 触发器类名。 */
  public String getTriggerClassName() {
    return triggerClassName;
  }

  public void setTriggerClassName(String triggerClassName) {
    this.triggerClassName = triggerClassName;
  }

  /** cron表达式。 */
  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  /** 数据映射表，作为作业的输入参数。 */
  public String getDataMap() {
    return dataMap;
  }

  public void setDataMap(String dataMap) {
    this.dataMap = dataMap;
  }

  /** 触发器启用时间。 */
  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  /** 触发器终止时间。 */
  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  /** 描述文字。 */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
