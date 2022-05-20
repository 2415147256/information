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
@BcGroup(name = "门店任务")
public class ShopTaskConfig {
  private static final String PREFIX = "shopTask.";
  public static final String SHOP_TASK_ENABLED = PREFIX + "enabled";
  @BcKey(name = "是否启用")
  private boolean enabled = false;
}
