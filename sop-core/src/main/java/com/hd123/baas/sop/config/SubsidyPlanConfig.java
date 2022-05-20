package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
@BcGroup(name = "补贴计划")
public class SubsidyPlanConfig {
  private static final String PREFIX = "subsidyPlan.";
  public static final String SUBSIDY_PLAN_ENABLED = PREFIX + "enabled";

  @BcKey(name = "是否启动")
  private boolean enabled = false;
}
