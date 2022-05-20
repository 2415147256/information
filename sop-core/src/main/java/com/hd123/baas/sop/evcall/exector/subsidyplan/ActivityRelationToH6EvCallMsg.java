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
public class ActivityRelationToH6EvCallMsg extends AbstractTenantEvCallMessage {
  /** 计划id */
  private List<String> owners;
}
