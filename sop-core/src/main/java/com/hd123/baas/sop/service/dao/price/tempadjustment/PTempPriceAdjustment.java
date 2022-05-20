package com.hd123.baas.sop.service.dao.price.tempadjustment;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @Author maodapeng
 * @Since
 */
public class PTempPriceAdjustment extends PStandardEntity {

  public static final String TABLE_NAME = "temp_price_adjustment";
  public static final String TABLE_ALIAS = "_temp_price_adjustment";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String FLOW_NO = "flow_no";
  public static final String STATE = "state";
  public static final String EFFECTIVE_START_DATE = "effective_start_date";
  public static final String REASON = "reason";

  public static String[] allColumns() {
    return toColumnArray(PStandardEntity.allColumns(), TENANT, ORG_ID, FLOW_NO, STATE, EFFECTIVE_START_DATE, REASON);
  }

}
