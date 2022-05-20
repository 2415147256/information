package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.api.task.AssignableShopTaskSummary;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

@SchemaMeta
@MapToEntity(AssignableShopTaskSummary.class)
@Getter
@Setter
public class PAssignableShopTaskSummary {
  @TableName
  public static final String TABLE_NAME = "shop_task";
  public static final String TABLE_ALIAS = "_shop_task";

  public static String SHOP = "shop";
  public static String SHOP_CODE = "shop_code";
  public static String SHOP_NAME = "shop_name";
  public static String FINISHED = "finished";
  public static String TOTAL = "total";
  public static String POINT = "point";
  public static String SCORE = "score";
  public static String RATE = "rate";
  public static String RANK = "rank";
  public static String ROW_NUM = "rowNum";
  public static String PRE_RATE = "preRate";
}
