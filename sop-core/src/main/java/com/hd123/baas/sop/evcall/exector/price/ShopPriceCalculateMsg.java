package com.hd123.baas.sop.evcall.exector.price;

import java.util.Date;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/20.
 */
@Getter
@Setter
public class ShopPriceCalculateMsg extends AbstractTenantEvCallMessage {

  // 门店
  private String shop;
  // 门店数
  private long shopCount = 0L;
  // 计算日期
  private Date executeDate;
  // 试算单id
  private String pk;
  // 任务id
  private String taskId;

}
