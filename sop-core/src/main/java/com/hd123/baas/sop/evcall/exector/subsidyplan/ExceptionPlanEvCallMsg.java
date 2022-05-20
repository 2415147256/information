package com.hd123.baas.sop.evcall.exector.subsidyplan;

import java.util.List;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class ExceptionPlanEvCallMsg extends AbstractTenantEvCallMessage {
  /**
   * 补贴计划ID集合
   */
  private List<String> uuids;
}
