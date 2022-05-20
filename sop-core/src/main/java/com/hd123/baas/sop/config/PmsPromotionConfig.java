package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */

@Getter
@Setter
@BcGroup(name = "PmsPromotionConfig")
public class PmsPromotionConfig {
  @BcKey(name = "普通折扣是否包括促销折扣 默认为true")
  private boolean discountContainPriceDiscount = true;
  @BcKey(name = "是否支持排除促销冲突功能 默认为false")
  private boolean supportExcludePrm = false;
  @BcKey(name = "是否支持排除促销商品功能 默认为true")
  private boolean supportExcludePromotionSku = true;
  @BcKey(name = "是否支持排除商品功能 默认为false")
  private boolean supportExcludeSku = false;
  @BcKey(name = "是否支持促销承担方 默认为true")
  private boolean supportFavorSharing = true;
  @BcKey(name = "是否支持规则冲突校验 默认为false")
  private boolean supportRuleConflict = false;
}
