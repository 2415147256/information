package com.hd123.baas.sop.jmzs.accountclassification.dao;

import com.hd123.baas.sop.jmzs.accountclassification.api.AccountClassification;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.lang.Assert;

import java.util.HashMap;
import java.util.Map;

public class PAccountClassification extends PStandardEntity {

  public static final String TABLE_NAME = "account_classification";
  public static final String TABLE_ALIAS = "_account_classification";


  public static final String ORG_ID="orgId";
  public static final String CODE="code";
  public static final String TENANT = "tenant";
  public static final String STATE = "state";
  public static final String TYPE = "type";
  public static final String NAME = "name";


  public static Map<String, Object> toFieldValues(AccountClassification entity) {
    Assert.assertArgumentNotNull(entity, "entity");
    Map<String, Object> fvm = new HashMap<String, Object>(PStandardEntity.toFieldValues(entity));
    putFieldValue(fvm, CODE, entity.getCode());
    putFieldValue(fvm, TENANT, entity.getTenant());
    putFieldValue(fvm, ORG_ID, entity.getOrgId());
    putFieldValue(fvm, STATE, entity.getState().name());
    putFieldValue(fvm, TYPE, entity.getType().name());
    putFieldValue(fvm, NAME, entity.getName());
    return fvm;
  }
}
