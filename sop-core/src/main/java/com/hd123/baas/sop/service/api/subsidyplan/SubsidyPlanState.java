package com.hd123.baas.sop.service.api.subsidyplan;

/**
 * @author liuhaoxin
 */
public enum SubsidyPlanState {
  INIT, // 初始状态
  PUBLISHED, // 已发布
  TERMINATED, // 已终止
  EXPIRED, // 已经过期
  EXCEPTION// 异常
}
