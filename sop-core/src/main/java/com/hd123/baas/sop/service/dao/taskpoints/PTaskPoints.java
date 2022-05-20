package com.hd123.baas.sop.service.dao.taskpoints;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author liyan
 * @date 2021/6/3
 */
public class PTaskPoints extends PEntity {
  public static final String TABLE_NAME = "task_points";
  public static final String TABLE_ALIAS = "_task_points";

  public static final String TENANT = "tenant";
  public static final String OCCURRED_TYPE = "occurred_type";
  public static final String OCCURRED_UUID = "occurred_uuid";
  public static final String OCCURRED_DESC = "occurred_desc";
  public static final String OCCURRED_TIME = "occurred_time";
  public static final String POINTS = "points";
  public static final String USER_ID = "user_id";
  public static final String USER_NAME = "user_name";
}
