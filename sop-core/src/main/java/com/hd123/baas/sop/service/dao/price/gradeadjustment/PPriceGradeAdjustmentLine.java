package com.hd123.baas.sop.service.dao.price.gradeadjustment;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/12.
 */
public class PPriceGradeAdjustmentLine extends PEntity {

  public static final String TABLE_NAME = "price_grade_adjustment_line";
  public static final String TABLE_ALIAS = "_price_grade_adjustment_line";

  public static final String TENANT = "tenant";
  public static final String OWNER = "owner";

  public static final String SKU_GROUP = "sku_group";
  public static final String SKU_GROUP_NAME = "sku_group_name";
  public static final String SKU_POSITION = "sku_position";
  public static final String SKU_POSITION_NAME = "sku_position_name";
  public static final String PRICE_GRADE = "price_grade";
  public static final String PRICE_GRADE_NAME = "price_grade_name";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, OWNER, SKU_GROUP, SKU_GROUP_NAME, SKU_POSITION,
        SKU_POSITION_NAME, PRICE_GRADE, PRICE_GRADE_NAME);
  }

}
