package com.hd123.baas.sop.service.dao.h6task;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/23.
 */
public class PH6Task extends PStandardEntity {

  public static final String TABLE_NAME = "h6_task";
  public static final String TABLE_ALIAS = "_h6_task";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String FILE_URL = "file_url";
  public static final String FLOW_NO = "flow_no";
  public static final String EXECUTE_DATE = "execute_date";
  public static final String OCCURRED_TIME = "occurred_time";
  public static final String TYPE = "type";
  public static final String STATE = "state";

  public static final String ERR_MSG = "err_msg";

  public static String[] allColumns() {
    return toColumnArray(PStandardEntity.allColumns(),ORG_ID, TENANT, FILE_URL, EXECUTE_DATE, OCCURRED_TIME, TYPE, STATE,
        FLOW_NO, ERR_MSG);
  }

}
