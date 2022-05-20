package com.hd123.baas.sop.service.dao.taskplan;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

public class PTaskPlan extends PStandardEntity {
  public static final String TABLE_NAME = "task_plan";
  public static final String TABLE_ALIAS = "_task_plan";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String NAME = "name";
  public static final String REMIND_TIME = "remind_time";
  public static final String DESCRIPTION = "description";
  public static final String TASK_GROUP = "task_group";
  public static final String WORD_NEEDED = "word_needed";
  public static final String IMAGE_NEEDED = "image_needed";
  public static final String TEMPLATE_CLS = "template_cls";
  public static final String PLAN_TIME = "plan_time";
  public static final String STATE = "state";
  public static final String START_DATE = "start_date";
  public static final String END_DATE = "end_date";
  public static final String SORT = "sort";
  public static final String VALIDITY_DAYS = "validity_days";
  public static final String DAY_OF_WEEK = "day_of_Week";
  public static final String DAY_OF_MONTH = "day_of_month";
  public static final String DELAY_DAY = "delay_day";
  public static final String CODE = "code";
  public static final String CYCLE = "cycle";
  public static final String PUBLISH_DATE = "publish_date";
  public static final String TASK_GROUPS = "task_groups";
  public static final String SHOPS = "shops";
  public static final String GENERATE_MODE = "generate_mode";
  public static final String SHOP_MODE = "shop_mode";
  public static final String REMIND_DATE = "remind_date";
  public static final String TYPE = "type";
  public static final String PUBLISH_TASK_DATE = "publish_task_date";
  public static final String REMIND_DETAIL_TIME = "remind_detail_time";
  public static final String PUBLISH_TASK_DATE_COLLECT = "publish_task_date_collect";
  public static final String ASSIGN_TYPE = "assign_type";
  public static final String SOURCE = "source";
  public static final String CREATOR_SHOP = "creator_shop";
  public static final String CREATOR_SHOP_CODE = "creator_shop_code";
  public static final String CREATOR_SHOP_NAME = "creator_shop_name";
  public static final String ADVANCE_PUB_DAY = "advance_pub_day";
  public static final String ADVANCE_PUB_HOUR = "advance_pub_hour";
  public static final String ADVANCE_END_DAY = "advance_end_day";
  public static final String ADVANCE_END_HOUR = "advance_end_hour";

}
