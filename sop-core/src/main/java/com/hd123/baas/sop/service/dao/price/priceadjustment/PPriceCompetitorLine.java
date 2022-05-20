package com.hd123.baas.sop.service.dao.price.priceadjustment;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @Author: maodapeng
 * @Date: 2020/12/4 15:34
 */
public class PPriceCompetitorLine extends PEntity {
  public static final String TABLE_NAME = "price_competitor_line";
  public static final String TABLE_ALIAS = "_price_competitor_line";
  public static final String TENANT = "tenant";
  public static final String OWNER = "owner";
  public static final String SKU_ID = "sku_id";
  public static final String SKU_CODE = "sku_code";
  public static final String SALE_PRICE = "sale_price";
  public static final String QTY = "qty";
  public static final String IGNORE = "`ignore`";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, OWNER, SKU_ID, SKU_CODE, SALE_PRICE, QTY, IGNORE);
  }
}
