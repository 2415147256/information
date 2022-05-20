package com.hd123.baas.sop.service.dao.explosivev2;

import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import lombok.Getter;
import lombok.Setter;

@SchemaMeta
@MapToEntity(ExplosiveV2.class)
@Getter
@Setter
public class PExplosiveV2 extends PStandardEntity {
  public static final String TABLE_NAME = "explosive_v2";
  public static final String TABLE_CAPTION = "爆品计划";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String PLAN_ID = "plan_id";
  public static final String FLOW_NO = "flow_no";
  public static final String NAME = "name";
  public static final String STATE = "state";
  public static final String EXT = "ext";
  public static final String START_DATE = "start_date";
  public static final String END_DATE = "end_date";
  public static final String SIGN_START_DATE = "sign_start_date";
  public static final String SIGN_END_DATE = "sign_end_date";
}
