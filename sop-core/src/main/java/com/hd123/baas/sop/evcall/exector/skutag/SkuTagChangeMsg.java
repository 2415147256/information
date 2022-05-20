package com.hd123.baas.sop.evcall.exector.skutag;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
public class SkuTagChangeMsg extends AbstractTenantEvCallMessage {
  private String orgId;
  private String skuId;
}
