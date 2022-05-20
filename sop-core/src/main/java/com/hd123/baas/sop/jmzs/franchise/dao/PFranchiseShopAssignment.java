package com.hd123.baas.sop.jmzs.franchise.dao;

import com.hd123.baas.sop.jmzs.franchise.api.FranchiseShopAssignment;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.lang.Assert;

import java.util.HashMap;
import java.util.Map;

public class PFranchiseShopAssignment extends PStandardEntity {

  public static final String TABLE_NAME = "franchise_shop_assignment";
  public static final String TABLE_ALIAS = "_franchise_shop_assignment";


  public static final String TENANT = "tenant";
  public static final String SHOP_CODE ="shopCode";
  public static final String SHOP_ID ="shopId";
  public static final String SHOP_NAME ="shopName";
  public static final String FRANCHISE_ID = "franchiseId";
  public static final String FRANCHISE_UUID = "franchiseUuid";
  public static final String FRANCHISE_CODE = "franchiseCode";
  public static final String FRANCHISE_NAME = "franchiseName";


  public static Map<String, Object> toFieldValues(FranchiseShopAssignment entity) {
    Assert.assertArgumentNotNull(entity, "entity");
    Map<String, Object> fvm = new HashMap<String, Object>(PStandardEntity.toFieldValues(entity));
    putFieldValue(fvm, SHOP_CODE, entity.getShopCode());
    putFieldValue(fvm, SHOP_ID, entity.getShopId());
    putFieldValue(fvm, SHOP_NAME, entity.getShopName());
    putFieldValue(fvm, FRANCHISE_CODE, entity.getFranchiseCode());
    putFieldValue(fvm, FRANCHISE_UUID, entity.getFranchiseUuid());
    putFieldValue(fvm, FRANCHISE_ID, entity.getFranchiseId());
    putFieldValue(fvm, FRANCHISE_NAME, entity.getFranchiseName());
    putFieldValue(fvm, TENANT, entity.getTenant());
    return fvm;
  }
}
