package com.hd123.baas.sop.evcall.exector.price;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since 查询试算单商品行上周平均订货数量消息
 */
@Getter
@Setter
public class AdjustmentLineWeekQtyMsg extends AbstractTenantEvCallMessage {
  // 试算单UUID
  private String owner;
}
