package com.hd123.baas.sop.service.dao.price.formula;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @Author maodapeng
 * @Since
 */
public class PPriceSkuFormula extends PEntity {
  public static final String TABLE_NAME = "sku_formula";
  public static final String TABLE_ALIAS = "_sku_formula";
  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";

  public static final String SKU_ID = "sku_id";
  public static final String SKU_CODE = "sku_code";
  public static final String SKU_NAME = "sku_name";
  public static final String FORMULA = "formula";
  public static final String FORMULA_DESC = "formula_desc";

  public static final String DEPEND_ON_SKU_ID = "depend_on_sku_id";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, ORG_ID, SKU_ID, SKU_CODE, SKU_NAME, FORMULA, FORMULA_DESC,
        DEPEND_ON_SKU_ID);
  }
}
