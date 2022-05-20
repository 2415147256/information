package com.hd123.baas.sop.service.dao.group;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

public class PSkuGroupCategoryAssoc extends PEntity {
  public static final String TABLE_NAME = "sku_group_category_assoc";
  public static final String TABLE_ALIAS = "_sku_group_category_assoc";

  public static final String TENANT = "tenant";

  public static final String ORG_ID = "org_id";

  public static final String UUID = "UUID";

  public static final String SKU_GROUP_ID = "sku_group_id";

  public static final String CATEGORY_CODE = "category_code";

  public static final String CATEGORY_NAME = "category_name";

  public static final String CATEGORY_ID = "category_id";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, ORG_ID, UUID, SKU_GROUP_ID, CATEGORY_CODE, CATEGORY_NAME,
        CATEGORY_ID);
  }
}
