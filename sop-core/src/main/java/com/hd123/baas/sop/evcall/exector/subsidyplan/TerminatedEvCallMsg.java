package com.hd123.baas.sop.evcall.exector.subsidyplan;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class TerminatedEvCallMsg extends AbstractTenantEvCallMessage {
  /** 活动id */
  private String planId;
}
