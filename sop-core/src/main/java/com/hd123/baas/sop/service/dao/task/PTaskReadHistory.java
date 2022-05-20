package com.hd123.baas.sop.service.dao.task;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

public class PTaskReadHistory extends PStandardEntity {
  private static final long serialVersionUID = -5410210879360923408L;

  public static final String TABLE_NAME = "task_read_history";
  public static final String TABLE_ALIAS = "_task_read_history";

  public static final String TENANT = "tenant";
  public static final String PLAN = "plan";
  public static final String PLAN_PERIOD = "plan_period";
  public static final String OPERATOR_ID = "operator_id";
  public static final String TYPE = "type";

}
