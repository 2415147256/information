package com.hd123.baas.sop.service.api.price.shopprice.bean;

import java.util.Date;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Getter
@Setter
public class ShopPricePromotionManager extends ShopPricePromotion {

  // 生效开始时间
  private Date effectiveStartDate;

  @QueryEntity(ShopPricePromotionManager.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = ShopPricePromotionManager.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String SHOP = PREFIX + "shop";
    @QueryField
    public static final String EFFECTIVE_START_DATE = PREFIX + "effectiveStartDate";
    @QueryField
    public static final String EFFECTIVE_END_DATE = PREFIX + "effectiveEndDate";
    @QueryField
    public static final String SKU_ID = PREFIX + "sku.id";
    @QueryField
    public static final String SOURCE_LAST_MODIFIED = PREFIX + "sourceLastModified";
  }

}
