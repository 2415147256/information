package com.hd123.baas.sop.service.dao.price.shopprice;

import com.hd123.baas.sop.service.dao.PThinEntity;

/**
 * @author zhengzewang on 2020/11/19.
 */
public class PShopPriceManager extends PThinEntity {

  public static final String TABLE_NAME = "shop_price_manager";
  public static final String TABLE_ALIAS = "_shop_price_manager";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String SHOP = "shop";
  public static final String SHOP_CODE = "shop_code";
  public static final String SHOP_NAME = "shop_name";
  public static final String EFFECTIVE_DATE = "effective_date";
  public static final String EFFECTIVE_END_DATE = "effective_end_date";
  public static final String SKU_ID = "sku_id";
  public static final String SKU_GID = "sku_gid";
  public static final String SKU_QPC = "sku_QPC";
  public static final String SKU_CODE = "sku_code";
  public static final String SKU_NAME = "sku_name";
  public static final String IN_PRICE = "in_price";
  public static final String BASE_PRICE = "base_price";
  public static final String PROMOTION_SOURCE = "promotion_source";
  public static final String SHOP_PRICE = "shop_price";
  public static final String SALE_PRICE = "sale_price";
  public static final String CHANGED = "changed";

  public static String[] allColumns() {
    return toColumnArray(PThinEntity.allColumns(), TENANT,ORG_ID, SHOP, SHOP_CODE, SHOP_NAME, EFFECTIVE_DATE,
        EFFECTIVE_END_DATE, SKU_ID, SKU_GID, SKU_QPC, SKU_CODE, SKU_NAME, IN_PRICE, BASE_PRICE, PROMOTION_SOURCE,SHOP_PRICE, SALE_PRICE,
        CHANGED);
  }

}
