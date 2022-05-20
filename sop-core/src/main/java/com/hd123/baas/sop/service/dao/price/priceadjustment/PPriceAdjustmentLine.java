package com.hd123.baas.sop.service.dao.price.priceadjustment;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/11.
 */
public class PPriceAdjustmentLine extends PEntity {

  public static final String TABLE_NAME = "price_adjustment_line";
  public static final String TABLE_ALIAS = "_price_adjustment_line";

  public static final String TENANT = "tenant";
  public static final String OWNER = "owner";

  public static final String SKU_ID = "sku_id";
  public static final String SKU_GID = "sku_gid";
  public static final String SKU_CODE = "sku_code";
  public static final String SKU_NAME = "sku_name";
  public static final String SKU_QPC = "sku_qpc";
  public static final String SKU_UNIT = "sku_unit";
  public static final String SKU_DEFINE = "sku_define";
  public static final String RAW = "raw";

  public static final String SKU_IN_PRICE = "sku_in_price";
  public static final String SKU_INIT_IN_PRICE = "sku_init_in_price";
  public static final String SKU_BASE_PRICE = "sku_base_price";

  public static final String SKU_TOLERANCE_VALUE = "sku_tolerance_value";
  public static final String SKU_KV = "sku_kv";
  public static final String SKU_BV = "sku_bv";
  public static final String SKU_INCREASE_RATE = "sku_increase_rate";

  public static final String SKU_GROUP = "sku_group";
  public static final String SKU_GROUP_NAME = "sku_group_name";
  public static final String SKU_GROUP_TOLERANCE_VALUE = "sku_group_tolerance_value";

  public static final String SKU_POSITION = "sku_position";
  public static final String SKU_POSITION_NAME = "sku_position_name";
  public static final String SKU_POSITION_INCREASE_RATES = "sku_position_increase_rates";

  public static final String PRICE_RANGE_INCREASE_RATES = "price_range_increase_rates";

  public static final String SKU_GRADE_INCREASE_RATES = "sku_grade_increase_rates";

  public static final String INCREASE_TYPE = "increase_type";
  public static final String INCREASE_RULES = "increase_rules";
  public static final String PRICE_GRADES = "price_grades";

  public static final String REMARK = "remark";

  public static final String CALC_TAIL_DIFF = "calc_tail_diff";

  public static final String HIGH_IN_PRICE = "high_in_price";
  public static final String LOW_IN_PRICE = "low_in_price";
  public static final String HIGH_BACK_GROSS_RATE = "high_back_gross_rate";
  public static final String LOW_BACK_GROSS_RATE = "low_back_gross_rate";
  public static final String HIGH_FRONT_GROSS_RATE = "high_front_gross_rate";
  public static final String LOW_FRONT_GROSS_RATE = "low_front_gross_rate";
  public static final String HIGH_MARKET_DIFF_RATE = "high_market_diff_rate";
  public static final String LOW_MARKET_DIFF_RATE = "low_market_diff_rate";
  public static final String HIGH_PRICE_FLOAT_RATE = "high_price_float_rate";
  public static final String LOW_PRICE_FLOAT_RATE = "low_price_float_rate";

  public static final String PRE_SKU_IN_PRICE = "pre_sku_in_price";
  public static final String PRE_SKU_INCREASE_RATE = "pre_sku_increase_rate";
  public static final String PRE_PRICE_RANGE_INCREASE_RATES = "pre_price_range_increase_rates";
  public static final String PRE_PRICE_GRADES = "pre_price_grades";

  public static final String EXT = "ext";

  public static final String AVE_WEEK_QTY = "ave_week_qty";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, OWNER, SKU_ID, SKU_GID, SKU_CODE, SKU_NAME, SKU_QPC, SKU_UNIT,
        SKU_DEFINE, SKU_IN_PRICE, SKU_INIT_IN_PRICE, SKU_BASE_PRICE, SKU_TOLERANCE_VALUE, SKU_KV, SKU_BV,
        SKU_INCREASE_RATE, SKU_GROUP, SKU_GROUP_NAME, SKU_GROUP_TOLERANCE_VALUE, SKU_POSITION, SKU_POSITION_NAME,
        SKU_POSITION_INCREASE_RATES, PRICE_RANGE_INCREASE_RATES, SKU_GRADE_INCREASE_RATES, INCREASE_TYPE,
        INCREASE_RULES, PRICE_GRADES, RAW, REMARK, CALC_TAIL_DIFF, HIGH_IN_PRICE, LOW_IN_PRICE, HIGH_BACK_GROSS_RATE,
        LOW_BACK_GROSS_RATE, HIGH_FRONT_GROSS_RATE, LOW_FRONT_GROSS_RATE, HIGH_MARKET_DIFF_RATE, LOW_MARKET_DIFF_RATE,
        HIGH_PRICE_FLOAT_RATE, LOW_PRICE_FLOAT_RATE, PRE_SKU_IN_PRICE, PRE_SKU_INCREASE_RATE,
        PRE_PRICE_RANGE_INCREASE_RATES, PRE_PRICE_GRADES,EXT,AVE_WEEK_QTY);
  }

}
