package com.hd123.baas.sop.job.entity;

import com.hd123.rumba.commons.jdbc.entity.PEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang
 */
@Getter
@Setter
public class PTimedJob extends PEntity {

  public static final String CAPTION = "调度JOB";
  public static final String TABLE_ALIAS = "_timedJob";
  public static final String TABLE_NAME = "timedJob";

  public static final String TRAN_ID = "tranId";
  public static final String PARAMS = "params";
  public static final String INTERVAL = "interval";
  public static final String RUN_TIMES = "runTimes";
  public static final String EXPECTED_RUN_TIME = "expectedRunTime";
  public static final String CALLBACK_BEAN_NAME = "callbackBeanName";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TRAN_ID, PARAMS, INTERVAL, RUN_TIMES, EXPECTED_RUN_TIME,
        CALLBACK_BEAN_NAME);

  }
}
