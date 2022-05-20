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
public enum RsTaskStatus {
  /** 已提交（未生效） */
  submitted,
  /** 已生效 */
  effected,
  /** 已暂停 */
  paused,
  /** 已取消 */
  canceled,
  /** 已结束 */
  ended;
}
