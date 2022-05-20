package com.hd123.baas.sop.service.api.price.pricepromotion;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/13.
 */
@Getter
@Setter
public class PricePromotionLine extends TenantEntity {

  private String owner;
  private PriceSku sku;
  private PricePromotionLineType type;
  private String rule;
  /**
   * 自定义类别
   */
  private String skuGroup;

  /**
   * 自定义类别名称
   */
  private String skuGroupName;

  @QueryEntity(PricePromotionLine.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = PricePromotionLine.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String OWNER = PREFIX + "owner";

  }

}
