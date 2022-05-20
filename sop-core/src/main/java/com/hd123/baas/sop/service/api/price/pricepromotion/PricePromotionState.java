package com.hd123.baas.sop.service.api.price.pricepromotion;

/**
 * @author zhengzewang on 2020/11/13.
 */
public enum PricePromotionState {
  INIT,

  CONFIRMED, AUDITED, CANCELED, PUBLISHED, EXPIRED,
  /** 终止  */
  TERMINATE
}
