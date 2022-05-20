package com.hd123.baas.sop.service.dao.task.usertask;

import com.hd123.baas.sop.service.api.task.usertask.UserTaskSummary;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@SchemaMeta
@MapToEntity(UserTaskSummary.class)
@Getter
@Setter
public class PUserTaskSummary extends PEntity {
  private static final long serialVersionUID = 2945083565016815583L;
  @TableName
  public static final String TABLE_NAME = "v_user_task_summary";
  public static final String TABLE_ALIAS = "v_user_task_summary";

  public static final String TENANT = "tenant";

  public static final String PLAN = "plan";
  public static final String PLAN_CODE = "plan_code";
  public static final String PLAN_NAME = "plan_name";

  public static final String PLAN_PERIOD = "plan_period";
  public static final String PLAN_PERIOD_CODE = "plan_period_code";

  public static final String PLAN_START_TIME = "plan_start_time";
  public static final String PLAN_END_TIME = "plan_end_time";

  public static final String STATE = "state";

  public static final String SHOP_COUNT = "shop_count";
  public static final String FINISHED_SHOP_COUNT = "finished_shop_count";
  public static final String OPERATOR_ID = "operator_id";
  public static final String FINISHED_DATE = "finished_date";
}
