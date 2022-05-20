package com.hd123.baas.sop.service.dao.price.gradeadjustment;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/12.
 */
public class PPriceGradeAdjustment extends PStandardEntity {

  public static final String TABLE_NAME = "price_grade_adjustment";
  public static final String TABLE_ALIAS = "_price_grade_adjustment";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String FLOW_NO = "flow_no";
  public static final String STATE = "state";
  public static final String REASON = "reason";
  public static final String EFFECTIVE_START_DATE = "effective_start_date";

  public static String[] allColumns() {
    return toColumnArray(PStandardEntity.allColumns(), ORG_ID, TENANT, FLOW_NO, STATE, EFFECTIVE_START_DATE, REASON);
  }

}
