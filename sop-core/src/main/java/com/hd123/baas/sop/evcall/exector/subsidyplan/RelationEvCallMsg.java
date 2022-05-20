package com.hd123.baas.sop.evcall.exector.subsidyplan;

import com.hd123.baas.sop.service.api.subsidyplan.ActivityType;
import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class RelationEvCallMsg extends AbstractTenantEvCallMessage {

  /**
   * 计划id
   */
  private String planId;
  /**
   * 活动类型
   */
  private ActivityType activityType;
  /**
   * 活动ID
   */
  private String activityId;
}
