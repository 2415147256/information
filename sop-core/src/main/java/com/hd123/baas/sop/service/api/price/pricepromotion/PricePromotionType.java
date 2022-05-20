package com.hd123.baas.sop.service.api.price.pricepromotion;

import lombok.Getter;

/**
 * 到店价促销类型
 */
@Getter
public enum PricePromotionType {
  FULL_DISCOUNT_PRMT("全场折扣促销", 0), SKU_LIMIT_PRMT("单品促销", 1);

  String value;
  //覆盖优先级
  int priority;

  PricePromotionType(String value, int priority) {
    this.value = value;
    this.priority = priority;
  }
}
