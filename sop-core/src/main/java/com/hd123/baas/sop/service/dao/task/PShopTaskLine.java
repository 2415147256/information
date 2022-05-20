package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.api.task.ShopTaskLine;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@SchemaMeta
@MapToEntity(ShopTaskLine.class)
@Getter
@Setter
public class PShopTaskLine {
  @TableName
  public static final String TABLE_NAME = "shop_task";
  public static final String TABLE_ALIAS = "_shop_task";

  public static String SHOP = "shop";
  public static String SHOP_CODE = "shop_code";
  public static String SHOP_NAME = "shop_name";
  public static String PLAN_CODE = "plan_code";
  public static String PLAN_NAME = "plan_name";
  public static String PLAN_PERIOD = "plan_period";
  public static String GROUP_NAME = "group_name";
  public static String OPERATOR_NAME = "operator_name";
  public static String OPERATOR_ID = "operator_id";
  public static String ITEM_NAME = "item_name";
  public static String NOTE = "note";
  public static String CUT_POINT = "cut_point";
  public static String SCORE = "score";
  public static String POINT = "point";
  public static String FEEDBACK = "feedback";
}
