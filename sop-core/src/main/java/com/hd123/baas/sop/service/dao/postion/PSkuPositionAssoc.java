package com.hd123.baas.sop.service.dao.postion;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

public class PSkuPositionAssoc extends PEntity {
  public static final String TABLE_NAME = "sku_position_assoc";
  public static final String TABLE_ALIAS = "_sku_position_assoc";

  public static final String TENANT = "tenant";
  public static final String UUID = "uuid";

  public static final String SKU_POSITION_ID = "sku_position_id";

  public static final String SKU_ID = "sku_id";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, UUID, SKU_POSITION_ID, SKU_ID);
  }

}
