package com.hd123.baas.sop.service.dao.application;

import com.hd123.spms.commons.jdbc.entity.PEntity;

public class PUserApplication extends PEntity {

  public static final String TABLE_NAME = "user_module";
  public static final String TABLE_ALIAS = "_user_module";

  public static final String TENANT = "tenant";
  public static final String UUID = "uuid";
  public static final String USER_ID = "user_id";
  public static final String MODULE = "module";
  public static final String SORT = "sort";
  public static final String CREATED = "created";
  public static final String CREATOR_NS = "creatorNS";
  public static final String CREATOR_ID = "creatorId";
  public static final String CREATOR_NAME = "creatorName";
}
