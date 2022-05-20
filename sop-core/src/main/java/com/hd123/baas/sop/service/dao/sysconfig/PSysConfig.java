package com.hd123.baas.sop.service.dao.sysconfig;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

public class PSysConfig extends PStandardEntity {

  public static final String TABLE_NAME = "config_item";
  public static final String TABLE_ALIAS = "_config_item";

  public static final String TENANT = "tenant";
  public static final String SPEC = "spec";
  public static final String CF_KYE = "cf_key";
  public static final String CF_VALUE = "cf_value";

}
