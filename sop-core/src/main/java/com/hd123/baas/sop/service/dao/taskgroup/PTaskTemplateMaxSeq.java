package com.hd123.baas.sop.service.dao.taskgroup;

import java.io.Serializable;

/**
 * @author guyahui
 * @date 2021/7/29 16:30
 */
public class PTaskTemplateMaxSeq implements Serializable {

  private static final long serialVersionUID = 4744661080373945712L;

  public static final String TABLE_NAME = "task_template";
  public static final String TABLE_ALIAS = "_task_template";

  public static final String OWNER = "owner";
  // 排序字段
  public static final String SEQ = "seq";
  // 租户
  public static final String TENANT = "tenant";

}
