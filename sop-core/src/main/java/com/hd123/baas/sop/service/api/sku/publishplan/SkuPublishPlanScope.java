package com.hd123.baas.sop.service.api.sku.publishplan;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.jdbc.annotation.ColumnName;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.MapToProperty;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

/**
 * 商品上下架方案范围(SkuPublishPlanScope)实体类
 *
 * @author liuhaoxin
 * @since 2021-11-25 11:51:07
 */
@Getter
@Setter
public class SkuPublishPlanScope extends TenantEntity {
  /** 方案ID */
  private String owner;
  /** 类型，门店=SHOP，组织=ORG */
  private String optionType;
  /** 类型对应值的UUID */
  private String optionUuid;
  /** 类型对应值的CODE */
  private String optionCode;
  /** 类型对应值的名称 */
  private String optionName;

  @QueryEntity(SkuPublishPlanScope.class)
  public static class Queries {
    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(SkuPublishPlanScope.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String OWNER = PREFIX.nameOf("owner");
  }

  @SchemaMeta
  @MapToEntity(SkuPublishPlanScope.class)
  public class Schema {
    @TableName
    public static final String TABLE_NAME = "sku_publish_plan_scope";

    public static final String TABLE_ALIAS = "_sku_publish_plan_scope";

    @ColumnName
    @MapToProperty(value = "uuid")
    public static final String UUID = "uuid";
    @ColumnName
    @MapToProperty(value = "tenant")
    public static final String TENANT = "tenant";
    @ColumnName
    @MapToProperty(value = "owner")
    public static final String OWNER = "owner";
    @ColumnName
    @MapToProperty(value = "optionType")
    public static final String OPTION_TYPE = "option_type";
    @ColumnName
    @MapToProperty(value = "optionUuid")
    public static final String OPTION_UUID = "option_uuid";
    @ColumnName
    @MapToProperty(value = "optionCode")
    public static final String OPTION_CODE = "option_code";
    @ColumnName
    @MapToProperty(value = "optionName")
    public static final String OPTION_NAME = "option_name";
  }
}
