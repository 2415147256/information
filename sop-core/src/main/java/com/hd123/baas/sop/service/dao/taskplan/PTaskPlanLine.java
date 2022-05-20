package com.hd123.baas.sop.service.dao.taskplan;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author guyahui
 * @date 2021/5/6 21:22
 */
public class PTaskPlanLine extends PEntity {

  public static final String TABLE_NAME = "task_plan_line";
  public static final String TABLE_ALIAS = "_task_plan_line";

  public static final String TENANT = "tenant";

  public static final String OWNER = "owner";
  public static final String TASK_PLAN_ITEM_ID = "task_plan_item_id";
  public static final String SHOP = "shop";
  public static final String SHOP_CODE = "shop_code";
  public static final String SHOP_NAME = "shop_name";
  public static final String TASK_GROUP_ID = "task_group_id";
  public static final String TASK_GROUP_CODE = "task_group_code";
  public static final String TASK_GROUP_NAME = "task_group_name";
  public static final String ASSIGNEE_ID = "assignee_id";
  public static final String ASSIGNEE = "assignee";
  public static final String POSITION_NAME = "position_name";
  public static final String POSITION_CODE = "position_code";
}
