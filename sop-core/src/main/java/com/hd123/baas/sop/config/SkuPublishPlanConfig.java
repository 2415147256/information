package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import com.qianfan123.baas.config.api.field.ConfigEditor;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
@BcGroup(name = "上下架方案")
public class SkuPublishPlanConfig {
  private static final String PREFIX = "skuPublishPlan.";
  public static final String SKU_PUBLISH_PLAN_ENABLED = PREFIX + "enabled";

  @BcKey(name = "是否启动")
  private boolean enabled = false;
  @BcKey(name = "是否开启自动创建分公司的上下架方案，默认=false", editor = ConfigEditor.BOOL)
  private boolean enableSaveNew = false;
  @BcKey(name = "上下架方案的实现策略，默认=空实现", editor = ConfigEditor.TEXT)
  private String beanName;
}
