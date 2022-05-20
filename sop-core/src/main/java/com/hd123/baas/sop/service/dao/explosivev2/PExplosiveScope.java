package com.hd123.baas.sop.service.dao.explosivev2;

import com.hd123.baas.sop.service.api.explosivev2.ExplosiveScope;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shenmin
 */
@SchemaMeta
@MapToEntity(ExplosiveScope.class)
@Getter
@Setter
public class PExplosiveScope extends PEntity {
  public static final String TABLE_NAME = "explosive_v2_scope";
  public static final String TABLE_CAPTION = "爆品活动范围";

  public static final String TENANT = "tenant";
  public static final String OWNER = "owner";
  public static final String LINE_NO = "line_no";

  public static final String OPTION_TYPE = "option_type";
  public static final String OPTION_UUID = "option_uuid";
  public static final String OPTION_CODE = "option_code";
  public static final String OPTION_NAME = "option_name";
}
