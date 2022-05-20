package com.hd123.baas.sop.service.dao.price.priceadjustment;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/11.
 */
public class PPriceAdjustment extends PStandardEntity {

  public static final String TABLE_NAME = "price_adjustment";
  public static final String TABLE_ALIAS = "_price_adjustment";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String FLOW_NO = "flow_no";
  public static final String EFFECTIVE_START_DATE = "effective_start_date";
  public static final String STATE = "state";
  public static final String REASON = "reason";

  public static String[] allColumns() {
    return toColumnArray(PStandardEntity.allColumns(), ORG_ID, TENANT, FLOW_NO, EFFECTIVE_START_DATE, STATE, REASON);
  }

}
