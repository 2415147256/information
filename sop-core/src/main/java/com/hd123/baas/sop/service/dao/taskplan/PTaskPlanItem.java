package com.hd123.baas.sop.service.dao.taskplan;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author guyahui
 * @date 2021/5/24 13:41
 */
public class PTaskPlanItem extends PEntity {
  private static final long serialVersionUID = 6666930687206673446L;

  public static final String TABLE_NAME = "task_plan_item";
  public static final String TABLE_ALIAS = "_task_plan_item";

  public static final String TENANT = "tenant";

  public static final String OWNER = "owner";
  // 附件列表
  public static final String ATTACH_FILES = "attach_files";
  // 内容
  public static final String COMMENT = "comment";
  // 分数
  public static final String POINT = "point";
  // 是否审核
  public static final String AUDIT = "audit";
  // 执行人列表
  public static final String ASSIGNEES = "assignees";
  // 门店列表
  public static final String SHOPS = "shops";

}
