package com.hd123.baas.sop.evcall.exector.subsidyplan;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import com.hd123.baas.sop.remote.dingtalk.DingTalkLinkMsg;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class SubsidyPlanMsg extends AbstractTenantEvCallMessage {
  private String shop;
  private String orgId;
  private DingTalkLinkMsg dingTalkLinkMsg;
}
