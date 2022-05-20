package com.hd123.baas.sop.service.dao.group;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

public class PSkuGroupPositionGradeConfig extends PEntity {
  public static final String TABLE_NAME = "sku_group_position_grade_config";
  public static final String TABLE_ALIAS = "_sku_group_position_grade_config";

  public static final String TENANT = "tenant";

  public static final String ORG_ID = "org_id";

  public static final String UUID = "UUID";

  public static final String SKU_GROUP_ID = "sku_group_id";

  public static final String PRICE_POSITION_ID = "price_position_id";

  public static final String PRICE_GRADE_JSON = "price_grade_json";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, ORG_ID, UUID, SKU_GROUP_ID, PRICE_POSITION_ID, PRICE_GRADE_JSON);
  }
}
