package com.hd123.baas.sop.service.dao.basedata;

import com.hd123.baas.sop.service.api.basedata.sku.DSkuTag;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import com.hd123.rumba.commons.lang.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lina
 */
public class PSkuTag extends PEntity {

  public static final String TABLE_NAME = "sku_tag";
  public static final String TABLE_ALIAS = "库存商品标签";

  public static final String TENANT = "tenant";

  public static final String GOODS_GID = "goodsGid";
  public static final String SKU_ID = "skuId";
  public static final String CODE = "code";
  public static final String NAME = "name";
  public static final String ORG_TYPE = "orgType";
  public static final String ORG_ID = "orgId";


  public static String[] columnArray() {
    return new String[] { NAME, CODE, TENANT, SKU_ID, GOODS_GID, ORG_ID, ORG_TYPE };
  }

  public static Map<String, Object> toFieldValues(DSkuTag entity) {
    Assert.assertArgumentNotNull(entity, "entity");
    Map<String, Object> fvm = new HashMap<String, Object>();
    fvm.putAll(PEntity.toFieldValues(entity));
    putFieldValue(fvm, TENANT, entity.getTenant());
    putFieldValue(fvm, GOODS_GID, entity.getGoodsGid());
    putFieldValue(fvm, SKU_ID, entity.getSkuId());
    putFieldValue(fvm, ORG_ID, entity.getOrgId());
    putFieldValue(fvm, ORG_TYPE, entity.getOrgType());
    putFieldValue(fvm, CODE, entity.getCode());
    putFieldValue(fvm, NAME, entity.getName());
    return fvm;
  }
}
