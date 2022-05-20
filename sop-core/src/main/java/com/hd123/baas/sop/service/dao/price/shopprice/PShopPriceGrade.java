package com.hd123.baas.sop.service.dao.price.shopprice;

import com.hd123.baas.sop.service.dao.PThinEntity;

/**
 * @author zhengzewang on 2020/11/16.
 */
public class PShopPriceGrade extends PThinEntity {

  public static final String TABLE_NAME = "shop_price_grade";
  public static final String TABLE_ALIAS = "_shop_price_grade";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String SHOP = "shop";
  public static final String SOURCE = "source";
  public static final String SKU_GROUP = "sku_group";
  public static final String SKU_POSITION = "sku_position";
  public static final String PRICE_GRADE = "price_grade";
  public static final String SOURCE_CREATE_TIME = "source_create_time";

  public static String[] allColumns() {
    return toColumnArray(PThinEntity.allColumns(), TENANT, ORG_ID, SHOP, SOURCE, SKU_GROUP, SKU_POSITION, PRICE_GRADE, SOURCE_CREATE_TIME);
  }

}
