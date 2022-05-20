package com.hd123.baas.sop.remote.rsmkhpms.entity;

public enum BasePricePromotionState {
  INIT, // 初始化
  SUBMITTED, // 未审核
  AUDITED, // 已审核
  CANCELED, // 作废
  EFFECT, // 生效中
  EXPIRED, // 已过期
  TERMINATED// 终止
}