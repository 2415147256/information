package com.hd123.baas.sop.service.dao.advertorial;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

public class PAdvertorial extends PStandardEntity {
  public static final String TABLE_NAME = "advertorial";
  public static final String TABLE_ALIAS = "_advertorial";

  public static final String TENANT = "tenant";
  public static final String UUID = "uuid";
  public static final String TITLE = "title";
  public static final String CONTENT = "content";
  public static final String TH_URI = "th_uri";

  public static String[] allColumns() {
    return toColumnArray(PStandardEntity.allColumns(), TENANT, UUID, TITLE, CONTENT, TH_URI);
  }
}
