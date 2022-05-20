/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobScheduleHandler.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.tools;

import java.io.Serializable;
import java.util.Date;

/**
 * 作业调度句柄。用于在作业被提交调度器，等待调度器启动作业（即作业实例化）期间，找回作业实例。
 * 
 * @author huzexiong
 * @since 1.0
 * 
 */
public class JobScheduleHandler implements Serializable {

  private static final long serialVersionUID = 9008846258026285041L;

  public JobScheduleHandler() {
    // Do Nothing
  }

  public JobScheduleHandler(String name) {
    this.name = name;
  }

  public JobScheduleHandler(String name, String group) {
    this.group = group;
    this.name = name;
  }

  private String group;
  private String name;
  private Date scheduled = new Date();

  /** 作业组名。null值意味着使用默认组名。 */
  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  /** 作业名称。 */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /** 作业被调度器接受的时间。 */
  public Date getScheduled() {
    return scheduled;
  }

  public void setScheduled(Date scheduled) {
    this.scheduled = scheduled;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((group == null) ? 0 : group.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((scheduled == null) ? 0 : scheduled.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    JobScheduleHandler other = (JobScheduleHandler) obj;
    if (group == null) {
      if (other.group != null)
        return false;
    } else if (!group.equals(other.group))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (scheduled == null) {
      if (other.scheduled != null)
        return false;
    } else if (!scheduled.equals(other.scheduled))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(group);
    sb.append(',');
    sb.append(name);
    return sb.toString();
  }
}