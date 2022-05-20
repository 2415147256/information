package com.hd123.baas.sop.service.api.price.temppriceadjustment;

import java.math.BigDecimal;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class TempPriceAdjustmentLine extends TenantEntity {
  private String owner;
  private String shop;
  private String shopCode;
  private String shopName;
  private String skuId;
  private String skuGid;
  private BigDecimal skuQpc;
  private String skuCode;
  private String skuName;
  private BigDecimal salePrice;

  @QueryEntity(TempPriceAdjustmentLine.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = TempPriceAdjustmentLine.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String OWNER = PREFIX + "owner";
  }
}
