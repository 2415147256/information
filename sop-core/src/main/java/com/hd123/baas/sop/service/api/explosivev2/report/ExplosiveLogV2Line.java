package com.hd123.baas.sop.service.api.explosivev2.report;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.jdbc.annotation.ColumnName;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.MapToProperty;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.Schemas;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 爆品活动日志行(ExplosiveLogV2Line)实体类
 *
 * @author liuhaoxin
 * @since 2021-12-07 18:12:46
 */
@Getter
@Setter
public class ExplosiveLogV2Line extends TenantEntity {
  /** 活动日志ID */
  private String owner;
  /** 商品ID */
  private String skuId;
  /** 商品代码 */
  private String skuCode;
  /** 商品GID */
  private String skuGid;
  /** 商品名称 */
  private String skuName;
  /** 商品规格 */
  private BigDecimal skuQpc;
  /** 商品单位 */
  private String skuUnit;
  /** 报名数 */
  private BigDecimal qty;

  @QueryEntity(ExplosiveLogV2Line.class)
  public static class Queries extends QueryFactors.Entity {
    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(ExplosiveLogV2Line.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
  }

  @SchemaMeta
  @MapToEntity(ExplosiveLogV2Line.class)
  public class Schema extends Schemas.Entity {
    @TableName
    public static final String TABLE_NAME = "explosive_log_v2_line";

    public static final String TABLE_ALIAS = "_explosive_log_v2_line";

    @ColumnName
    @MapToProperty(value = "tenant")
    public static final String TENANT = "tenant";
    @ColumnName
    @MapToProperty(value = "owner")
    public static final String OWNER = "owner";
    @ColumnName
    @MapToProperty(value = "skuId")
    public static final String SKU_ID = "sku_id";
    @ColumnName
    @MapToProperty(value = "skuCode")
    public static final String SKU_CODE = "sku_code";
    @ColumnName
    @MapToProperty(value = "skuGid")
    public static final String SKU_GID = "sku_gid";
    @ColumnName
    @MapToProperty(value = "skuName")
    public static final String SKU_NAME = "sku_name";
    @ColumnName
    @MapToProperty(value = "skuQpc")
    public static final String SKU_QPC = "sku_qpc";
    @ColumnName
    @MapToProperty(value = "skuUnit")
    public static final String SKU_UNIT = "sku_unit";
    @ColumnName
    @MapToProperty(value = "qty")
    public static final String QTY = "qty";
  }

}
