/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-commons-api
 * 文件名：	TaskState.java
  * 模块说明：	
 * 修改历史：

 * 2019年10月9日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.task;

/**
 * 
 * @author lsz
 */
public enum ReportType {

  /** 报告启动信息（有效字段：workerId） */
  start,
  /** 报告进度（有效字段：progress） */
  progress,
  /** 报告执行结果（有效字段：executeResult、failReason） */
  result,
  /** 报告参数添加或修改变更（有效字段：parameters） */
  updateParameterByAddOrModify,
  /** 报告参数覆盖变更（有效字段：parameters） */
  updateParameterByCover;

}
