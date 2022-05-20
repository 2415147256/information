/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobInstanceResult.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.entity;

/**
 * 作业实例执行结果枚举。
 * 
 * @author huzexiong
 * @since 1.0
 *
 */
public enum JobInstanceResult {
  /** 执行完成。 */
  completed,
  /** 内部原因导致执行被中止，例如执行过程中发生错误。 */
  aborted,
  /** 被外部请求要求终止。 */
  interrupted,
  /** 技术原因未能捕获。 */
  noCaught;
}
