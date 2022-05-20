package com.hd123.baas.sop.evcall.exector.pricescreen;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
public class PriceScreenTerminateMsg extends AbstractTenantEvCallMessage {
  private String tenant;
  private String uuid;
}
