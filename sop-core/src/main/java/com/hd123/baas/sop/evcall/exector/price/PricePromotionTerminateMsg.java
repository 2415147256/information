package com.hd123.baas.sop.evcall.exector.price;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
public class PricePromotionTerminateMsg extends AbstractTenantEvCallMessage {
  private String uuid;
}
