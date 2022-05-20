package com.hd123.baas.sop.evcall.exector.shoptask;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
public class ShopTaskSummaryMsg extends AbstractTenantEvCallMessage {
  private String tenant;
  private String uuid;
}
