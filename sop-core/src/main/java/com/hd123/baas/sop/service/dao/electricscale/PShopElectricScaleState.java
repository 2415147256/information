package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

public class PShopElectricScaleState extends PEntity {
  public static final String TABLE_NAME = "electronic_scale_state";
  public static final String TABLE_AlIAS = "_electronic_scale_state";

  public static final String TENANT = "tenant";
  public static final String TYPE = "type";
  public static final String ELECTRONIC_SCALE = "electronic_scale";
  public static final String STATE = "state";
  public static final String CREATE_TIME = "create_time";
  public static final String REMARK = "remark";
}
