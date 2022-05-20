package com.hd123.baas.sop.evcall.exector.price.tempshop;

import com.hd123.baas.sop.evcall.AbstractEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class TempShopPriceAdjustmentStartMsg extends AbstractEvCallMessage {
  private String tenant;
  // 任务id
  private String taskId;
}
