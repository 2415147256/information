package com.hd123.baas.sop.service.dao.grade;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

public class PPriceGrade extends PEntity {
  public static final String TABLE_NAME = "price_grade";
  public static final String TABLE_ALIAS = "_price_grade";

  public static final String TENANT = "tenant";

  public static final String ORG_ID = "org_id";

  public static final String UUID = "UUID";

  public static final String NAME = "name";

  public static final String SEQ = "seq";

  public static final String DFT = "dft";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, ORG_ID, UUID, NAME, SEQ, DFT);
  }
}
