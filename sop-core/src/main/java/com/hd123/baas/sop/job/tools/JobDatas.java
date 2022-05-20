/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobDatas.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.tools;

/**
 * 提供若干使用简单类型的作业映射表数据。
 * 
 * @author huzexiong
 * @since 1.0
 *
 */
public class JobDatas {

  /** 作业映射表键，取值表示作业实例当前是否处于允许中断的状态，类型为Boolean。 */
  public static final String KEY_ALLOW_INTERRUPT = "RB$AllowInterrupt";
  /** 作业映射表，键为{@link #KEY_ALLOW_INTERRUPT}对应的默认取值。 */
  public static final boolean DEFAULT_ALLOW_INTERRUPT = true;
}
