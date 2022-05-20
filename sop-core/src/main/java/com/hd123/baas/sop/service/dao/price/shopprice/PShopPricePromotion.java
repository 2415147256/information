package com.hd123.baas.sop.service.dao.price.shopprice;

import com.hd123.baas.sop.service.dao.PThinEntity;

/**
 * @author zhengzewang on 2020/11/19.
 */
public class PShopPricePromotion extends PThinEntity {

  public static final String TABLE_NAME = "shop_price_promotion";
  public static final String TABLE_ALIAS = "_shop_price_promotion";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String SHOP = "shop";
  public static final String EFFECTIVE_END_DATE = "effective_end_date";
  public static final String SKU_ID = "sku_id";
  public static final String TYPE = "type";
  public static final String RULE = "rule";
  public static final String SOURCE = "source";
  public static final String SOURCE_LAST_MODIFIED = "source_last_modified";
  public static final String PRICE_PROMOTION_TYPE = "price_promotion_type";

  public static String[] allColumns() {
    return toColumnArray(PThinEntity.allColumns(), TENANT,ORG_ID, SHOP, EFFECTIVE_END_DATE, SKU_ID, TYPE, RULE, SOURCE,
        SOURCE_LAST_MODIFIED, PRICE_PROMOTION_TYPE);
  }

}
