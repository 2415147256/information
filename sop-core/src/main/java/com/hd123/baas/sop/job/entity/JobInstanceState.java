/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobInstanceState.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.entity;

/**
 * 作业实例状态枚举。
 * 
 * @author huzexiong
 * @since 1.0
 *
 */
public enum JobInstanceState {
  /** 执行中。 */
  executing,
  /** 执行结束。 */
  over;

}
