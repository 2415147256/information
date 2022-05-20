package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * 价格屏配置
 * 
 * @author liuhaoxin
 */
@Getter
@Setter
@BcGroup(name = "价格屏")
public class PriceScreenConfig {
  private static final String PREFIX = "priceScreen.";
  public static final String PRICE_SCREEN_ENABLED = PREFIX + "enabled";

  @BcKey(name = "是否启动")
  private boolean enabled = false;
}
