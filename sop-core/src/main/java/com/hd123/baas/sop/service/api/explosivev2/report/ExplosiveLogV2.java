package com.hd123.baas.sop.service.api.explosivev2.report;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.entity.UCN;
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

import java.util.Date;
import java.util.List;

/**
 * 爆品活动日志(ExplosiveLogV2)实体类
 *
 * @author liuhaoxin
 * @since 2021-12-07 18:04:32
 */
@Getter
@Setter
public class ExplosiveLogV2 extends TenantEntity {

  public static final String FETCH_LINE = "fetch_line";

  /** 组织ID */
  private String orgId;
  /** 活动ID */
  private String explosiveId;
  /**活动名称*/
  /** 门店信息 */
  private UCN shop;
  /** 事务ID */
  private String tranId;
  /** 来源uuid */
  private String sourceId;
  /** 来源单号 */
  private String sourceFlowNo;
  /** 来源单据类型 */
  private ExplosiveLogV2Type sourceType;
  /** 来源单据动作 */
  private String sourceAction;
  /** 来源单据发生日期 */
  private Date sourceBusinessDate;
  /** 备注 */
  private String remark;
  /** 创建时间 */
  private Date created;

  /** 爆品活动日志行 */
  private List<ExplosiveLogV2Line> lines;

  @QueryEntity(ExplosiveLogV2.class)
  public static class Queries extends QueryFactors.Entity {
    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(ExplosiveLogV2.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
  }

  @SchemaMeta
  @MapToEntity(ExplosiveLogV2.class)
  public class Schema extends Schemas.Entity {
    @TableName
    public static final String TABLE_NAME = "explosive_log_v2";

    public static final String TABLE_ALIAS = "_explosive_log_v2";

    @ColumnName
    @MapToProperty(value = "tenant")
    public static final String TENANT = "tenant";
    @ColumnName
    @MapToProperty(value = "orgId")
    public static final String ORG_ID = "org_id";
    @ColumnName
    @MapToProperty(value = "explosiveId")
    public static final String EXPLOSIVE_ID = "explosive_id";
    @ColumnName
    @MapToProperty(value = "shopId")
    public static final String SHOP_ID = "shop_id";
    @ColumnName
    @MapToProperty(value = "shopCode")
    public static final String SHOP_CODE = "shop_code";
    @ColumnName
    @MapToProperty(value = "shopName")
    public static final String SHOP_NAME = "shop_name";
    @ColumnName
    @MapToProperty(value = "tranId")
    public static final String TRAN_ID = "tran_id";
    @ColumnName
    @MapToProperty(value = "sourceId")
    public static final String SOURCE_ID = "source_id";
    @ColumnName
    @MapToProperty(value = "sourceFlowNo")
    public static final String SOURCE_FLOW_NO = "source_flow_no";
    @ColumnName
    @MapToProperty(value = "sourceType")
    public static final String SOURCE_TYPE = "source_type";
    @ColumnName
    @MapToProperty(value = "sourceAction")
    public static final String SOURCE_ACTION = "source_action";
    @ColumnName
    @MapToProperty(value = "sourceBusinessDate")
    public static final String SOURCE_BUSINESS_DATE = "source_business_date";
    @ColumnName
    @MapToProperty(value = "remark")
    public static final String REMARK = "remark";
    @ColumnName
    @MapToProperty(value = "created")
    public static final String CREATED = "created";
  }

}
