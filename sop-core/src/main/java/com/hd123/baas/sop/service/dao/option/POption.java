package com.hd123.baas.sop.service.dao.option;

import com.hd123.baas.sop.service.api.option.Option;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.lang.Assert;

import java.util.HashMap;
import java.util.Map;

public class POption extends PStandardEntity {
  public static final String TABLE_NAME = "options";
  public static final String TABLE_ALIAS = "_options";


  public static final String TENANT = "tenant";
  public static final String TYPE = "type";
  public static final String OP_KEY = "op_key";
  public static final String OP_VALUE = "op_value";

  public static Map<String, Object> toFieldValues(Option entity) {
    Assert.assertArgumentNotNull(entity, "entity");
    Map<String, Object> fvm = new HashMap<String, Object>(PStandardEntity.toFieldValues(entity));
    putFieldValue(fvm, TENANT, entity.getTenant());
    putFieldValue(fvm, TYPE, entity.getType().name());
    putFieldValue(fvm, OP_VALUE, entity.getOpValue());
    putFieldValue(fvm, OP_KEY, entity.getOpKey());
    return fvm;
  }
  
}
