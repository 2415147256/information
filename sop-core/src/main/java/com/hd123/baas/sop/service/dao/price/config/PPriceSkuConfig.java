package com.hd123.baas.sop.service.dao.price.config;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/10.
 */
public class PPriceSkuConfig extends PStandardEntity {

  public static final String TABLE_NAME = "price_sku_config";
  public static final String TABLE_ALIAS = "_price_sku_config";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String SKU_ID = "sku_id";
  public static final String TOLERANCE_VALUE = "tolerance_value";
  public static final String KV = "kv";
  public static final String BV = "bv";
  public static final String INCREASE_RATE = "increase_rate";
  public static final String CALC_TAIL_DIFF = "calc_tail_diff";
  public static final String SKU_POSITION = "sku_position";
  public static final String HIGH_IN_PRICE="high_in_price";
  public static final String LOW_IN_PRICE="low_in_price";
  public static final String HIGH_BACK_GROSS_RATE="high_back_gross_rate";
  public static final String LOW_BACK_GROSS_RATE="low_back_gross_rate";
  public static final String HIGH_FRONT_GROSS_RATE="high_front_gross_rate";
  public static final String LOW_FRONT_GROSS_RATE="low_front_gross_rate";
  public static final String HIGH_MARKET_DIFF_RATE="high_market_diff_rate";
  public static final String LOW_MARKET_DIFF_RATE="low_market_diff_rate";
  public static final String HIGH_PRICE_FLOAT_RATE="high_price_float_rate";
  public static final String LOW_PRICE_FLOAT_RATE="low_price_float_rate";
  public static final String INCREASE_TYPE = "increase_type";
  public static final String INCREASE_RULES = "increase_rules";
  public static final String EXT = "ext";
  public static String[] allColumns() {
    return toColumnArray(PStandardEntity.allColumns(), TENANT, ORG_ID, SKU_ID, TOLERANCE_VALUE, KV, BV, INCREASE_RATE,
        CALC_TAIL_DIFF, SKU_POSITION,HIGH_IN_PRICE,LOW_IN_PRICE,HIGH_BACK_GROSS_RATE,LOW_BACK_GROSS_RATE, HIGH_FRONT_GROSS_RATE
            ,LOW_FRONT_GROSS_RATE,HIGH_MARKET_DIFF_RATE,LOW_MARKET_DIFF_RATE,HIGH_PRICE_FLOAT_RATE,LOW_PRICE_FLOAT_RATE,INCREASE_TYPE,INCREASE_RULES,EXT);
  }

}
