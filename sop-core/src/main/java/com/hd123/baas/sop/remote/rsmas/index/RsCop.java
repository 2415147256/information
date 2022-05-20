/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-commons-api
 * 文件名：	Cop.java
  * 模块说明：	
 * 修改历史：

 * 2019年10月12日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.index;

/**
 * 操作符
 * 
 * @author lsz
 */
public enum RsCop {
  /** 匹配 */
  match,
  /** 等于 */
  equals,
  /** 不等于 */
  notQquals,
  /** 在之中 */
  in,
  /** 不在之中 */
  notIn,
  /** 范围内（取边界值） */
  rangeWithEqual,
  /** 范围内（不取边界值） */
  rangeWithNotEqual,
  /** 起始于 */
  startWidth,
  /** 类似于 */
  like,
  /** 空 */
  isNull,
  /** 非空 */
  isNotNull
}
