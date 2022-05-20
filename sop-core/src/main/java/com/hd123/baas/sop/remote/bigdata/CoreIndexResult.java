package com.hd123.baas.sop.remote.bigdata;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author shenmin
 */
@Getter
@Setter
public class CoreIndexResult {
  //成交订单量
  private String completeOrder;
  //客单价
  private String pct;
  //销售金额时段数据
  private List<SalamtResult> salamtList;
  //交易门店数
  private String saleStore;
  //总门店数
  private String totalStore;


}
