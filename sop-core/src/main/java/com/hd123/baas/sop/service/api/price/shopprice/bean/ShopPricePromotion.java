package com.hd123.baas.sop.service.api.price.shopprice.bean;

import java.util.Date;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionLineType;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/19.
 * 
 *         当前门店促销价信息
 * 
 */
@Getter
@Setter
public class ShopPricePromotion extends TenantEntity {

  private String orgId;

  private String shop;
  // 生效结束时间
  private Date effectiveEndDate;

  private PriceSku sku;
  private PricePromotionLineType type;
  private String rule;
  // 来源单据
  private String source;
  /**
   * 来源单据的创建时间
   */
  private Date sourceLastModified;
  /**
   * 原单促销类型
   */
  private String pricePromotionType;


  @QueryEntity(ShopPricePromotion.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = ShopPricePromotion.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String SHOP = PREFIX + "shop";
    @QueryField
    public static final String EFFECTIVE_END_DATE = PREFIX + "effectiveEndDate";
    @QueryField
    public static final String SOURCE_LAST_MODIFIED = PREFIX + "sourceLastModified";
  }

}
