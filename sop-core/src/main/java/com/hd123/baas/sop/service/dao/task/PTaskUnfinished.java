package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.api.task.TaskUnfinished;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author guyahi
 * @Since
 */
@SchemaMeta
@MapToEntity(TaskUnfinished.class)
@Getter
@Setter
public class PTaskUnfinished extends PEntity {
  private static final long serialVersionUID = 4907745669877238139L;

  @TableName
  public static final String TABLE_NAME = "shop_task";
  public static final String TABLE_ALIAS = "_shop_task";

  public static final String TENANT = "tenant";

  public static final String PLAN = "plan";

  public static final String PLAN_NAME = "plan_name";

  public static final String PLAN_PERIOD = "plan_period";

  public static final String PLAN_START_TIME = "plan_start_time";

  public static final String PLAN_END_TIME = "plan_end_time";

  public static final String PLAN_TYPE = "plan_type";

}
