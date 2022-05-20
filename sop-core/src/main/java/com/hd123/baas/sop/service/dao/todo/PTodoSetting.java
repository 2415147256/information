package com.hd123.baas.sop.service.dao.todo;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

public class PTodoSetting extends PStandardEntity {
  public static final String TABLE_NAME = "sop_todo_setting";
  public static final String TABLE_ALIAS = "_setting";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String ID = "id";
  public static final String SETTING_NAME = "setting_name";
  public static final String STATE = "state";

  public static final String TYPE = "type";
  public static final String DESCRIPTION = "description";
  public static final String LEVEL = "level";
  public static final String START = "start";
  public static final String END = "end";
  public static final String EXT ="ext";
  public static final String SCENE_ID = "scene_id";
  public static final String TAG = "tag";
  public static final String CATEGORY ="category";
  public static final String POSITION_TYPE="position_type";
}
