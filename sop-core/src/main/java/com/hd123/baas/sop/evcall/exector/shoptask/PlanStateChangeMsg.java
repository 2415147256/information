package com.hd123.baas.sop.evcall.exector.shoptask;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import com.hd123.rumba.commons.biz.entity.OperateInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
public class PlanStateChangeMsg extends AbstractTenantEvCallMessage {
  private String plan;
  private String planPeriodCode;
  private PlanAction action;
  private OperateInfo operateInfo;
}
