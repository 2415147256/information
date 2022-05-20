package com.hd123.baas.sop.service.api.subsidyplan;

/**
 * @author liuhaoxin on 2021/07/09
 */
public enum ActivityState {
  INIT, // 初始化
  CONFIRMED, // 未审核
  AUDITED, // 已审核
  CANCELED, // 作废
  PUBLISHED, // 生效中
  EXPIRED, // 过期
  TERMINATED // 终止
}
