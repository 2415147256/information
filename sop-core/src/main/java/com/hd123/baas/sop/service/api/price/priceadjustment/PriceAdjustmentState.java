package com.hd123.baas.sop.service.api.price.priceadjustment;

/**
 * @author zhengzewang on 2020/11/10.
 */
public enum PriceAdjustmentState {

  INIT, // 未保存，草稿

  CONFIRMED, AUDITED, CANCELED, PUBLISHED, EXPIRED

}
