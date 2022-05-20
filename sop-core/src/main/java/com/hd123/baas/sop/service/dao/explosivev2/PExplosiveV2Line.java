package com.hd123.baas.sop.service.dao.explosivev2;

import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Line;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shenmin
 */
@SchemaMeta
@MapToEntity(ExplosiveV2Line.class)
@Getter
@Setter
public class PExplosiveV2Line extends PEntity {
  public static final String TABLE_NAME = "explosive_v2_line";
  public static final String TABLE_CAPTION = "爆品活动计划行";

  public static final String TENANT = "tenant";
  public static final String OWNER = "owner";
  public static final String LINE_NO = "line_no";
  public static final String SKU_ID = "sku_id";
  public static final String SKU_CODE = "sku_code";
  public static final String SKU_GID = "sku_gid";
  public static final String SKU_NAME = "sku_name";
  public static final String SKU_QPC = "sku_qpc";
  public static final String SKU_UNIT = "sku_unit";
  public static final String IN_PRICE = "in_price";
  public static final String LIMIT_QTY = "limit_qty";
  public static final String MIN_QTY = "min_qty";
  public static final String USED_LIMIT = "used_limit";
  public static final String REMARK = "remark";

}
