package com.hd123.baas.sop.service.api.subsidyplan;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class PlanTime {
  /**
   * 模式 delay/assign
   */
  private String mode;
  /**
   * 延时开始日
   */
  private Integer delayBeginDay;
  /**
   * 延时结束日
   */
  private Integer delayEndDay;
}
