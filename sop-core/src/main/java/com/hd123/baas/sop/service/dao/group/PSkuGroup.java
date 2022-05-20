package com.hd123.baas.sop.service.dao.group;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

public class PSkuGroup extends PEntity {
  public static final String TABLE_NAME = "sku_group";
  public static final String TABLE_ALIAS = "_sku_group";

  public static final String TENANT = "tenant";

  public static final String ORG_ID = "org_id";

  public static final String UUID = "uuid";

  public static final String NAME = "name";

  public static final String TOLERANCE_VALUE = "tolerance_value";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, ORG_ID, UUID, NAME, TOLERANCE_VALUE);
  }
}
