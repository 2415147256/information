package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import lombok.Getter;
import lombok.Setter;

@SchemaMeta
@MapToEntity(ShopTask.class)
@Getter
@Setter
public class PShopTask extends PStandardEntity {
  @TableName
  public static final String TABLE_NAME = "shop_task";
  public static final String TABLE_ALIAS = "_shop_task";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String SHOP_TASK_GROUP = "shop_task_group";

  public static final String OWNER = "owner";

  public static final String SHOP = "shop";
  public static final String SHOP_NAME = "shop_name";
  public static final String SHOP_CODE = "shop_code";

  public static final String TASK_GROUP = "task_group";
  public static final String GROUP_NAME = "group_name";

  public static final String PLAN = "plan";
  public static final String PLAN_CODE = "plan_code";
  public static final String PLAN_NAME = "plan_name";
  public static final String PLAN_TYPE = "plan_type";
  public static final String PLAN_PERIOD = "plan_period";
  public static final String PLAN_PERIOD_CODE = "plan_period_code";

  public static final String STATE = "state";

  public static final String NAME = "name";

  public static final String SORT = "sort";

  public static final String PLAN_START_TIME = "plan_start_time";
  public static final String PLAN_END_TIME = "plan_end_time";

  public static final String PLAN_TIME = "plan_time";
  public static final String REMIND_TIME = "remind_time";
  public static final String DESCRIPTION = "description";

  public static final String WORD_NEEDED = "word_needed";
  public static final String IMAGE_NEEDED = "image_needed";
  public static final String TEMPLATE_CLS = "template_cls";

  public static final String FEEDBACK = "feedback";

  public static final String FINISH_INFO_TIME = "finished";
  public static final String FINISH_INFO_OPERATOR_NAMESPACE = "finishNS";
  public static final String FINISH_INFO_OPERATOR_ID = "finishId";
  public static final String FINISH_INFO_OPERATOR_FULL_NAME = "finishName";

  public static final String FINISH_APPID = "finish_appid";

  public static final String OPERATOR_ID = "operator_id";
  public static final String OPERATOR_NAME = "operator_name";
  public static final String OPERATOR_POSITION_CODE = "operator_position_code";
  public static final String OPERATOR_POSITION_NAME = "operator_position_name";

  public static final String SCORE = "score";

  public static final String POINT = "point";

  public static final String VIDEO_NEEDED = "video_needed";

  public static final String ATTACH_FILES = "attach_files";
  public static final String COMMENT = "comment";
  public static final String AUDIT = "audit";
  public static final String ASSIGN_TYPE = "assign_type";
  public static final String GRAB_ORDER_TIME = "GRAB_ORDER_TIME";
  public static final String STARTED = "started";
  public static final String CREATOR_SHOP = "creator_shop";
  public static final String CREATOR_SHOP_CODE = "creator_shop_code";
  public static final String CREATOR_SHOP_NAME = "creator_shop_name";
}
