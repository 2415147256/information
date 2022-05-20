package com.hd123.baas.sop.evcall.exector.price;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class TempPriceAdjustmentMsg extends AbstractTenantEvCallMessage {
  private String tenant;
  // 改价单ID
  private String pk;
}
