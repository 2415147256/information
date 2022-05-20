/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobDetailInfo.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.entity;

import java.io.Serializable;

/**
 * @author huzexiong
 *
 */
public class PJobDetailInfo implements Serializable {

  private static final long serialVersionUID = -4379056123778911980L;

  private String group;
  private String name;
  private String jobClassName;
  private String dataMap;
  private String desciption;

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

  /** 作业类全限定名。 */
  public String getJobClassName() {
    return jobClassName;
  }

  public void setJobClassName(String jobClassName) {
    this.jobClassName = jobClassName;
  }

  /** 数据映射表，作为作业的输入参数。 */
  public String getDataMap() {
    return dataMap;
  }

  public void setDataMap(String dataMap) {
    this.dataMap = dataMap;
  }

  /** 描述文字。 */
  public String getDesciption() {
    return desciption;
  }

  public void setDesciption(String desciption) {
    this.desciption = desciption;
  }

}
