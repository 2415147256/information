package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.api.task.ShopTaskSummary;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@SchemaMeta
@MapToEntity(ShopTaskSummary.class)
@Getter
@Setter
public class PShopTaskSummary extends PEntity {
  @TableName
  public static final String TABLE_NAME = "shop_task_summary";
  public static final String TABLE_ALIAS = "_shop_task_summary";

  public static final String TENANT = "tenant";

  public static final String PLAN = "plan";

  public static final String PLAN_CODE = "plan_code";

  public static final String PLAN_NAME = "plan_name";

  public static final String PLAN_PERIOD_CODE = "plan_period_code";

  public static final String PLAN_PERIOD = "plan_period";

  public static final String PLAN_START_TIME = "plan_start_time";

  public static final String PLAN_END_TIME = "plan_end_time";

  public static final String SHOP = "shop";

  public static final String SHOP_CODE = "shop_code";

  public static final String SHOP_NAME = "shop_name";

  public static final String STATE = "state";

  public static final String POINT = "point";

  public static final String SCORE = "score";

  public static final String FINISH_TIME = "finish_time";

  public static final String RANK = "rank";

}
