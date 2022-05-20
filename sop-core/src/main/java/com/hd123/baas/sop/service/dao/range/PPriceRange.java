package com.hd123.baas.sop.service.dao.range;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

public class PPriceRange extends PEntity {
  public static final String TABLE_NAME = "price_range";
  public static final String TABLE_ALIAS = "_price_range";

  public static final String TENANT = "tenant";

  public static final String ORG_ID = "org_id";

  public static final String UUID = "uuid";

  public static final String NAME = "name";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, ORG_ID, UUID, NAME);
  }
}
