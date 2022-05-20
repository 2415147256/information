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
@BcGroup(name = "促销活动配置")
public class PromotionActivityConfig {
  private static final String PREFIX = "promotion.";

  @BcKey(name = "最晚审核时间 时分秒")
  private String latestAuditTime = "14:00:00";
}
