package com.hd123.baas.sop.service.api.sku.publishplan;

/**
 * 商品上下架状态
 *
 * @author liuhaoxin
 * @since 2021-11-24
 */
public enum SkuPublishPlanState {
  // 未上架
  INIT,
  // 已上架
  SUBMITTED,
  // 已过期
  EXPIRED,
  // 已下架
  CANCELED
}
