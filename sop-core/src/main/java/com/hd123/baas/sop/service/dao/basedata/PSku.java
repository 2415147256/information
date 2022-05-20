package com.hd123.baas.sop.service.dao.basedata;

import com.hd123.baas.sop.service.api.basedata.sku.DSku;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.lang.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lina
 */
public class PSku extends PStandardEntity {

  public static final String TABLE_NAME = "sku";
  public static final String TABLE_ALIAS = "库存商品";

  public static final String TENANT = "tenant";

  public static final String ORG_TYPE = "orgType";
  public static final String ORG_ID = "orgId";
  public static final String GOODS_GID = "goodsGid";
  public static final String ID = "id";
  public static final String CODE = "code";
  public static final String DELETED = "deleted";
  public static final String NAME = "name";
  public static final String QPC = "qpc";
  public static final String UNIT = "unit";
  public static final String PRICE = "price";
  public static final String CATEGORY_ID = "categoryId";
  public static final String PLU = "plu";
  public static final String REQUIRED = "required";
  public static final String INPUT_CODE= "inputCode";
  public static final String H6_GOODS_TYPE= "h6GoodsType";
  public static final String QPC_DESC= "qpcDesc";
  public static final String SKU_PY_CODE= "sku_py_code";
  public static final String SKU_DU= "sku_du";

  public static String[] columnArray() {
    return new String[] { NAME, CODE, ID, UNIT, QPC, PRICE, TENANT, CATEGORY_ID, GOODS_GID,DELETED,PLU,REQUIRED ,INPUT_CODE,H6_GOODS_TYPE,ORG_TYPE,ORG_ID,QPC_DESC,SKU_PY_CODE,SKU_DU};
  }

  public static Map<String, Object> toFieldValues(DSku entity) {
    Assert.assertArgumentNotNull(entity, "entity");
    Map<String, Object> fvm = new HashMap<String, Object>();
    fvm.putAll(PStandardEntity.toFieldValues(entity));
    putFieldValue(fvm, TENANT, entity.getTenant());
    putFieldValue(fvm, ID, entity.getId());
    putFieldValue(fvm, GOODS_GID, entity.getGoodsGid());
    putFieldValue(fvm, CATEGORY_ID, entity.getCategoryId());
    putFieldValue(fvm, NAME, entity.getName());
    putFieldValue(fvm, CODE, entity.getCode());
    putFieldValue(fvm, QPC, entity.getQpc());
    putFieldValue(fvm, UNIT, entity.getUnit());
    putFieldValue(fvm, PRICE, entity.getPrice());
    putFieldValue(fvm, DELETED, entity.getDeleted());
    putFieldValue(fvm, PLU, entity.getPlu());
    putFieldValue(fvm, REQUIRED, entity.getRequired());
    putFieldValue(fvm, INPUT_CODE, entity.getInputCode());
    putFieldValue(fvm, H6_GOODS_TYPE, entity.getH6GoodsType());
    putFieldValue(fvm, ORG_ID, entity.getOrgId());
    putFieldValue(fvm, ORG_TYPE, entity.getOrgType());
    putFieldValue(fvm, QPC_DESC, entity.getQpcDesc());
    putFieldValue(fvm, SKU_PY_CODE, entity.getPyCode());
    putFieldValue(fvm, SKU_DU, entity.getDu());
    return fvm;
  }
}
