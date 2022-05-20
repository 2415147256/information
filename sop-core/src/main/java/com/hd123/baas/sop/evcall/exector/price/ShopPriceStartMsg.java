package com.hd123.baas.sop.evcall.exector.price;

import java.util.Date;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class ShopPriceStartMsg extends AbstractTenantEvCallMessage {
  /** 指定待计算的日期 */
  private Date executeDate;
  // 任务id
  private String taskId;
}
