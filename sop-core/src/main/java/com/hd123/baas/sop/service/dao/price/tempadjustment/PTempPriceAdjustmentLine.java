package com.hd123.baas.sop.service.dao.price.tempadjustment;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @Author maodapeng
 * @Since
 */
public class PTempPriceAdjustmentLine extends PEntity {
  public static final String TABLE_NAME = "temp_price_adjustment_line";
  public static final String TABLE_ALIAS = "_temp_price_adjustment_line";

  public static final String TENANT = "tenant";
  public static final String OWNER = "owner";
  public static final String SHOP = "shop";
  public static final String SHOP_CODE = "shop_code";
  public static final String SHOP_NAME = "shop_name";
  public static final String SKU_ID = "sku_id";
  public static final String SKU_GID = "sku_gid";
  public static final String SKU_QPC = "sku_qpc";
  public static final String SKU_CODE = "sku_code";
  public static final String SKU_NAME = "sku_name";
  public static final String SALE_PRICE = "sale_price";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, OWNER, SHOP, SHOP_CODE, SHOP_NAME, SKU_ID, SKU_GID, SKU_QPC,
        SKU_CODE, SKU_NAME, SALE_PRICE);
  }
}
