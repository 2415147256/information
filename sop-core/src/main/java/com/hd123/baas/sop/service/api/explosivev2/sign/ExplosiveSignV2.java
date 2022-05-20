package com.hd123.baas.sop.service.api.explosivev2.sign;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
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
 * 爆品活动(ExplosiveSignV2)实体类
 *
 * @author liuhaoxin
 * @since 2021-12-02 19:10:44
 */
@Getter
@Setter
public class ExplosiveSignV2 extends TenantStandardEntity {

  public static final String FETCH_EXPLOSIVE = "fetch_explosive";
  public static final String FETCH_LINE = "fetch_line";

  public static final String[] FETCH_ALL = new String[] {
      FETCH_LINE, FETCH_EXPLOSIVE };

  public static class Ext {
    public static final String EXPLOSIVE = "explosive";
    public static final String EXPLOSIVE_NAME = "explosvie_name";
    public static final String EXPLOSIVE_START_TIME = "explosive_start_time";
    public static final String EXPLOSIVE_END_TIME = "explosive_end_time";
    public static final String EXPLOSIVE_SIGN_START_TIME = "explosive_sign_start_time";
    public static final String EXPLOSIVE_SIGN_END_TIME = "explosive_sign_end_time";
  }

  /** 组织ID */
  private String orgId;
  /** 活动ID */
  private String explosiveId;
  /** 门店信息 */
  private UCN shop;
  /** 状态 */
  private ExplosiveSignV2State state;
  /** 活动开始时间 */
  private Date startDate;
  /** 活动结束时间 */
  private Date endDate;
  /** 报名开始时间 */
  private Date signStartDate;
  /** 报名结束时间 */
  private Date signEndDate;
  /** 额外信息 */
  private ObjectNode ext;

  /** 报名行信息 */
  private List<ExplosiveSignV2Line> lines;

  /** 爆品信息 */
  private ExplosiveV2 explosive;


  @QueryEntity(ExplosiveSignV2.class)
  public static class Queries extends QueryFactors.StandardEntity {
    // 获取类中的字段名称
    public static final QueryFactorName PREFIX = QueryFactorName.prefix(ExplosiveSignV2.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String SHOP_ID = PREFIX.nameOf("shop.uuid");
    @QueryField
    public static final String STATE = PREFIX.nameOf("state");
    @QueryField
    public static final String ORG_ID = PREFIX.nameOf("orgId");
    @QueryField
    public static final String START_DATE = PREFIX.nameOf("startDate");
    @QueryField
    public static final String END_DATE = PREFIX.nameOf("endDate");
    @QueryField
    public static final String SIGN_START_DATE = PREFIX.nameOf("signStartDate");
    @QueryField
    public static final String SIGN_END_DATE = PREFIX.nameOf("signEndDate");

    @QueryField
    public static final String EXPLOSIVE_ID = PREFIX.nameOf("explosiveId");
  }

  @SchemaMeta
  @MapToEntity(ExplosiveSignV2.class)
  public class Schema extends Schemas.StandardEntity {
    @TableName
    public static final String TABLE_NAME = "explosive_sign_v2";

    public static final String TABLE_ALIAS = "_explosive_sign_v2";

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
    @MapToProperty(value = "shop.uuid")
    public static final String SHOP_ID = "shop_id";
    @ColumnName
    @MapToProperty(value = "shop.code")
    public static final String SHOP_CODE = "shop_code";
    @ColumnName
    @MapToProperty(value = "shop.name")
    public static final String SHOP_NAME = "shop_name";
    @ColumnName
    @MapToProperty(value = "state")
    public static final String STATE = "state";
    @ColumnName
    @MapToProperty(value = "startDate")
    public static final String START_DATE = "start_date";
    @ColumnName
    @MapToProperty(value = "endDate")
    public static final String END_DATE = "end_date";
    @ColumnName
    @MapToProperty(value = "signStartDate")
    public static final String SIGN_START_DATE = "sign_start_date";
    @ColumnName
    @MapToProperty(value = "signEndDate")
    public static final String SIGN_END_DATE = "sign_end_date";
    @ColumnName
    @MapToProperty(value = "ext")
    public static final String EXT = "ext";
  }

}
