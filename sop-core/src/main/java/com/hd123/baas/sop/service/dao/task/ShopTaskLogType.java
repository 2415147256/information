package com.hd123.baas.sop.service.dao.task;

/**
 * 反馈类型，区分log是REPLY或AUDIT
 */
public enum ShopTaskLogType {
  AUDIT, // 创建人评价
  REPLY, // 执行人反馈
}
