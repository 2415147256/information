package com.hd123.baas.sop.evcall.exector.goodsprm;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BcGroup(name = "促销价数据下发H6")
public class GoodsPrmPriceGeneralConfig {
  private static final String PREFIX = "h6-GoodsPrmPrice.";

  @BcKey(name = "日期跨度")
  private int spanDays = 2;
}
