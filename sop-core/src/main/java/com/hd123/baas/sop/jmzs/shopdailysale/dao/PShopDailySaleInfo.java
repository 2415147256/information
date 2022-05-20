package com.hd123.baas.sop.jmzs.shopdailysale.dao;

import com.hd123.baas.sop.jmzs.shopdailysale.api.ShopDailySaleInfo;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.lang.Assert;

import java.util.HashMap;
import java.util.Map;

public class PShopDailySaleInfo  extends PStandardEntity {
  public static final String TABLE_NAME = "shop_daily_sale_info";
  public static final String TABLE_ALIAS = "_shop_daily_sale_info";


  public static final String ORG_ID="orgId";
  public static final String CODE="code";
  public static final String TENANT = "tenant";
  public static final String DAILY_SALE_DATE = "dailySaleDate";
  public static final String AMOUNT = "amount";
  public static final String SHOP_CODE = "shopCode";
  public static final String SHOP_ID = "shopId";
  public static final String SHOP_NAME = "shopName";
  public static final String HOLDER = "holder";
  public static final String HOLDER_ID = "holderId";
  public static final String HOLDER_CODE = "holderCode";
  public static final String HOLDER_NAME = "holderName";
  public static final String STATE = "state";
  public static final String LINE = "line";

  public static Map<String, Object> toFieldValues(ShopDailySaleInfo entity) {
    Assert.assertArgumentNotNull(entity, "entity");
    Map<String, Object> fvm = new HashMap<String, Object>(PStandardEntity.toFieldValues(entity));
    putFieldValue(fvm, CODE, entity.getCode());
    putFieldValue(fvm, TENANT, entity.getTenant());
    putFieldValue(fvm, ORG_ID, entity.getOrgId());
    putFieldValue(fvm, DAILY_SALE_DATE, entity.getDailySaleDate());
    putFieldValue(fvm, SHOP_CODE, entity.getShopCode());
    putFieldValue(fvm, SHOP_NAME, entity.getShopName());
    putFieldValue(fvm, SHOP_ID, entity.getShopId());
    putFieldValue(fvm, AMOUNT, entity.getAmount());
    putFieldValue(fvm, HOLDER, entity.getHolder().name());
    putFieldValue(fvm, HOLDER_NAME, entity.getHolderName());
    putFieldValue(fvm, HOLDER_ID, entity.getHolderId());
    putFieldValue(fvm, HOLDER_CODE, entity.getHolderCode());
    putFieldValue(fvm, STATE, entity.getState().name());
    putFieldValue(fvm, LINE, JsonUtil.objectToJson(entity.getLines()));
    return fvm;
  }
}
