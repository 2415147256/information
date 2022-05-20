package com.hd123.baas.sop.evcall.exector.subsidyplan;

import com.hd123.baas.sop.service.api.subsidyplan.ActivityType;
import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import com.hd123.rumba.commons.biz.entity.OperateInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class ActivityTerminatedEvCallMsg extends AbstractTenantEvCallMessage {
    /***/
  /** 活动类型 */
  private ActivityType activityType;
  /** 活动id */
  private String activityId;
  /** 操作人信息 */
  private OperateInfo operateInfo;
}
