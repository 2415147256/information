package com.hd123.baas.sop.service.dao.price.config;

/**
 * @author zhengzewang on 2020/11/11.
 * 
 * 
 */
public class PPriceSku {

  public static final String TABLE_NAME = "sku";
  public static final String TABLE_ALIAS = "_sku";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "orgId";
  public static final String ID = "id";
  public static final String CATEGORY_ID = "categoryId";
  public static final String GOODS_GID = "goodsGid";
  public static final String CODE = "code";
  public static final String NAME = "name";
  public static final String QPC = "qpc";
  public static final String UNIT = "unit";
  public static final String DELETED = "deleted";
  // public static final String IS_SEGMENTED = "is_segmented";

  public static String[] allColumns() {
    return new String[] {
        TENANT, ORG_ID, ID, CODE, NAME, QPC, UNIT, GOODS_GID, CATEGORY_ID };
  }

}
