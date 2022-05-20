package com.hd123.baas.sop.service.api.price.shopprice.bean;

import java.math.BigDecimal;
import java.util.Date;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/13.
 * 
 *         门店价格池
 * 
 */
@Getter
@Setter
public class ShopPriceManager extends TenantEntity {
  private String orgId;
  private String shop;
  private String shopCode;
  private String shopName;

  private PriceSku sku;
  // 目标采购价
  private BigDecimal inPrice;
  // 基础到店价
  private BigDecimal basePrice;
  //促销信息来源
  private String promotionSource;
  // 实际到店价。如果没有促销价，则存null
  private BigDecimal shopPrice;
  // 售价
  private BigDecimal salePrice;
  // 当前生效日期。非生效开始时间。当对应的价格标识变化时，表示生效开始时间
  private Date effectiveDate;
  // 生效结束时间。仅对到店促销价生效
  private Date effectiveEndDate; // TODO
  // 相比于上一次，是否发生了变化。用最后三位从倒数第一位开始分别表示basePrice，salePrice，shopPrice是否发生变化
  private int changed;
  // 是否计算尾差（不落库）
  private Boolean calcTailDiff;

  @QueryEntity(ShopPriceManager.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = ShopPriceManager.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String SHOP = PREFIX + "shop";
    @QueryField
    public static final String EFFECTIVE_DATE = PREFIX + "effectiveDate";
    @QueryField
    public static final String SKU_ID = PREFIX + "sku.id";
    @QueryField
    public static final String CHANGED = PREFIX + "changed";
  }

}
