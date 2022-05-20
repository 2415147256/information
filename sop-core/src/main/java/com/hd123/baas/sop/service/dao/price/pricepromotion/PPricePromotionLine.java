package com.hd123.baas.sop.service.dao.price.pricepromotion;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/13.
 */
public class PPricePromotionLine extends PEntity {

  public static final String TABLE_NAME = "price_promotion_line";
  public static final String TABLE_ALIAS = "_price_promotion_line";

  public static final String TENANT = "tenant";
  public static final String OWNER = "owner";
  public static final String TYPE = "type";
  public static final String RULE = "rule";

  public static final String SKU_ID = "sku_id";
  public static final String SKU_CODE = "sku_code";
  public static final String SKU_NAME = "sku_name";
  public static final String SKU_QPC = "sku_qpc";
  public static final String SKU_UNIT = "sku_unit";
  public static final String SKU_IS_SEGMENTED = "sku_is_segmented";
  public static final String SKU_GROUP = "sku_group";
  public static final String SKU_GROUP_NAME = "sku_group_name";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, OWNER, TYPE, RULE, SKU_ID, SKU_CODE, SKU_NAME, SKU_QPC, SKU_UNIT,
        SKU_IS_SEGMENTED, SKU_GROUP, SKU_GROUP_NAME);
  }

}
