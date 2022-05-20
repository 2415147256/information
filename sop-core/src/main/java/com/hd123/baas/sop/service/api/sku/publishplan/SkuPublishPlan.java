package com.hd123.baas.sop.service.api.sku.publishplan;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import com.hd123.rumba.commons.jdbc.annotation.ColumnName;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.MapToProperty;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.Schemas;
import com.hd123.rumba.commons.util.converter.Converter;
import com.hd123.rumba.commons.util.converter.ConverterBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 商品上下架方案(SkuPublishPlan)实体类
 *
 * @author liuhaoxin
 * @since 2021-11-24 16:06:30
 */
@Getter
@Setter
public class SkuPublishPlan extends TenantStandardEntity {

  public static final String FETCH_LINE = "fetch_line";
  public static final String FETCH_SCOPE = "fetch_scope";

  public static final String[] FETCH_ALL = new String[] {
      FETCH_LINE, FETCH_SCOPE };

  public static class Ext {
    public static final String LINE_COUNT = "line_count";
    public static final String SCOPE = "scope";
    public static final String SOURCE = "source";
    public static final String SOURCE_FLOW_NO = "source_flow_no";
  }

  public static Converter<SkuPublishPlan, SkuPublishPlan> converter = ConverterBuilder
      .newBuilder(SkuPublishPlan.class, SkuPublishPlan.class)
      .build();

  /** 组织ID */
  private String orgId;
  /** 单号 */
  private String flowNo;
  /** 名称 */
  private String name;
  /** 有效日期 */
  private Date effectiveDate;
  /** 有效结束日期 */
  private Date effectiveEndDate;
  /** 配货仓库ID */
  private String wrhId;
  /** 配货仓库代码 */
  private String wrhCode;
  /** 配货仓库名称 */
  private String wrhName;
  /** 状态 */
  private SkuPublishPlanState state = SkuPublishPlanState.INIT;
  /** 备注 */
  private String remark;
  /** 额外信息 */
  private ObjectNode ext;

  /** 商品上下架方案行 */
  private List<SkuPublishPlanLine> lines;
  /** 商品上下架范围 */
  private List<SkuPublishPlanScope> scopes;

  @QueryEntity(SkuPublishPlan.class)
  public static class Queries extends QueryFactors.StandardEntity {
    // 获取类中的字段名称
    public static final QueryFactorName PREFIX = QueryFactorName.prefix(SkuPublishPlan.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String STATE = PREFIX.nameOf("state");
    @QueryField
    public static final String EFFECTIVE_DATE = PREFIX.nameOf("effectiveDate");
    @QueryField
    public static final String ORG_ID = PREFIX.nameOf("orgId");
    @QueryField
    public static final String NAME = PREFIX.nameOf("name");

    @QueryOperation
    public static final String KEYWORD = PREFIX.nameOf("keyword");
    @QueryOperation
    public static final String SCOPE_ID = "scopeId";
  }

  @SchemaMeta
  @MapToEntity(SkuPublishPlan.class)
  public class Schema extends Schemas.StandardEntity {
    @TableName
    public static final String TABLE_NAME = "sku_publish_plan";

    public static final String TABLE_ALIAS = "_sku_publish_plan";

    @ColumnName
    @MapToProperty(value = "tenant")
    public static final String TENANT = "tenant";
    @ColumnName
    @MapToProperty(value = "orgId")
    public static final String ORG_ID = "org_id";
    @ColumnName
    @MapToProperty(value = "flowNo")
    public static final String FLOW_NO = "flow_no";
    @ColumnName
    @MapToProperty(value = "name")
    public static final String NAME = "name";
    @ColumnName
    @MapToProperty(value = "effectiveDate")
    public static final String EFFECTIVE_DATE = "effective_date";
    @ColumnName
    @MapToProperty(value = "effectiveEndDate")
    public static final String EFFECTIVE_END_DATE = "effective_end_date";
    @ColumnName
    @MapToProperty(value = "wrhId")
    public static final String WRH_ID = "wrh_id";
    @ColumnName
    @MapToProperty(value = "wrhCode")
    public static final String WRH_CODE = "wrh_code";
    @ColumnName
    @MapToProperty(value = "wrhName")
    public static final String WRH_NAME = "wrh_name";
    @ColumnName
    @MapToProperty(value = "state")
    public static final String STATE = "state";
    @ColumnName
    @MapToProperty(value = "remark")
    public static final String REMARK = "remark";
    @ColumnName
    @MapToProperty(value = "ext")
    public static final String EXT = "ext";
  }

}
