package com.hd123.baas.sop.service.dao.taskgroup;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

public class PTaskGroup extends PStandardEntity {

  public static final String TABLE_NAME = "task_group";
  public static final String TABLE_ALIAS = "_task_group";

  public static final String TENANT = "tenant";
  public static final String NAME = "name";
  public static final String REMIND_TIME = "remind_time";
  public static final String DESCRIPTION = "description";
  public static final String TYPE = "type";
  public static final String CODE = "code";
  public static final String STATE = "state";
  public static final String ORG_ID = "org_id";

}
