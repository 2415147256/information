package com.hd123.baas.sop.evcall.exector.price;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Getter
@Setter
public class ShopPriceFinishedMsg extends AbstractTenantEvCallMessage {
  // 门店
  private String shop;
  // 任务id
  private String taskId;
}
