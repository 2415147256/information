package com.hd123.baas.sop.service.dao.task;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

public class PShopTaskGroup extends PStandardEntity {
  public static final String TABLE_NAME = "shop_task_group";
  public static final String TABLE_ALIAS = "_shop_task_group";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static String FINISH_INFO_TIME = "finished";
  public static String FINISH_APPID = "finishAppid";
  public static String FINISH_INFO_OPERATOR_NAMESPACE = "finishNS";
  public static String FINISH_INFO_OPERATOR_ID = "finishId";
  public static String FINISH_INFO_OPERATOR_FULL_NAME = "finishName";

  public static final String SHOP = "shop";
  public static final String SHOP_NAME = "shop_name";
  public static final String SHOP_CODE = "shop_code";

  public static final String TASK_GROUP = "task_group";
  public static final String GROUP_NAME = "group_name";

  public static final String TYPE = "type";
  public static final String STATE = "state";

  public static final String PLAN_TIME = "plan_time";
  public static final String REMIND_TIME = "remind_time";

  public static final String EARLIEST_FINISH_TIME = "earliest_finish_time";

}
