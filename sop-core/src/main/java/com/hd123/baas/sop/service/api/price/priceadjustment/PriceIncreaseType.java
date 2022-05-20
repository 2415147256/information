package com.hd123.baas.sop.service.api.price.priceadjustment;

import lombok.Getter;

/**
 * @author zhengzewang on 2020/11/10.
 */
@Getter
public enum PriceIncreaseType {

  AMOUNT("金额加价"), // 金额加价
  FIX("固定价"), // 固定价
  RATE("比例加价"), // 比例加价
  EXPRESS("公式价"); // 公式价

  private String desc;

  private PriceIncreaseType(String desc) {
    this.desc = desc;
  };
}
