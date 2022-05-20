/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 *
 * 项目名：	mas-commons-api
 * 文件名：	TaskState.java
  * 模块说明：
 * 修改历史：

 * 2019年10月9日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.task;

/**
 *
 * @author lsz
 */
public enum ExecuteStatus {
  /** 执行中 */
  progressing,
  /**
   * 失败
   */
  fail,
  /**
   * 成功
   */
  success,
  /**
   * 已准备，等待执行
   */
  ready,
  /**
   * 已取消
   */
  canceled;
}
