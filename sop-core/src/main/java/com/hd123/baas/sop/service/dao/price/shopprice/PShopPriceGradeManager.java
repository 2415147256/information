package com.hd123.baas.sop.service.dao.price.shopprice;

/**
 * @author zhengzewang on 2020/11/19.
 */
public class PShopPriceGradeManager extends PShopPriceGrade {

  public static final String TABLE_NAME = "shop_price_grade_manager";
  public static final String TABLE_ALIAS = "_shop_price_grade_manager";

  public static final String EFFECTIVE_START_DATE = "effective_start_date";

  public static String[] allColumns() {
    return toColumnArray(PShopPriceGrade.allColumns(), EFFECTIVE_START_DATE);
  }

}
