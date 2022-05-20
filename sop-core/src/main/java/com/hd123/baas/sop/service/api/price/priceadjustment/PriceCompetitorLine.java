package com.hd123.baas.sop.service.api.price.priceadjustment;

import java.math.BigDecimal;

import com.hd123.baas.sop.service.api.TenantEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/10.
 * 
 *         竞品行
 * 
 */
@Getter
@Setter
public class PriceCompetitorLine extends TenantEntity {

  private String owner;

  private String skuId;
  private String skuCode;
  private BigDecimal qpc;
  private BigDecimal salePrice;
  // 竞争数量 RsH6SOPClient#listOrderQty
  private BigDecimal qty;
  /** 是否忽略 */
  private boolean ignore = false;

}
