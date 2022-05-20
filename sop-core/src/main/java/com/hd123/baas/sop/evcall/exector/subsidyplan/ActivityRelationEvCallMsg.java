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
public class ActivityRelationEvCallMsg extends AbstractTenantEvCallMessage {
  /**
   * 活动类型
   */
  private ActivityType activityType;
  /**
   * 活动ID
   */
  private String activityId;
}
