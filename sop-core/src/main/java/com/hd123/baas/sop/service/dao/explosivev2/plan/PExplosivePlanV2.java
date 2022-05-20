package com.hd123.baas.sop.service.dao.explosivev2.plan;

import com.hd123.baas.sop.service.api.explosivev2.plan.ExplosivePlanV2;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import lombok.Getter;
import lombok.Setter;

@SchemaMeta
@MapToEntity(ExplosivePlanV2.class)
@Getter
@Setter
public class PExplosivePlanV2 extends PStandardEntity {
  @TableName
  public static final String TABLE_NAME = "explosive_plan_v2";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String FLOW_NO = "flow_no";
  public static final String NAME = "name";
  public static final String STATE = "state";
  public static final String EXT = "ext";
  public static final String START_DATE = "start_date";
  public static final String END_DATE = "end_date";
  public static final String SIGN_START_DATE = "sign_start_date";
  public static final String SIGN_END_DATE = "sign_end_date";
}
