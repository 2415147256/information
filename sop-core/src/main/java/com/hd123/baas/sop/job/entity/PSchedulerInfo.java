/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	SchedulerInfo.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.entity;

import java.io.Serializable;

/**
 * 作业调用容器信息
 * 
 * @author huzexiong
 *
 */
public class PSchedulerInfo implements Serializable {

  private static final long serialVersionUID = -1057330185488797423L;

  private String name;
  private String instanceId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }
}
