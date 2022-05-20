package com.hd123.baas.sop.service.api.subsidyplan;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
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

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Setter
@Getter
public class SubsidyPlan extends StandardEntity {
  public static final String FETCH_SUBSIDY_PLAN = "fetch_subsidy_plan";
  public static final String[] FETCH_ALL = new String[] {
      FETCH_SUBSIDY_PLAN };

  public static class Ext {
    public static final String MODE = "mode";
    public static final String DELAY_BEGIN_DAY = "delayBeginDay";
    public static final String DELAY_END_DAY = "delayEndDay";
    public static final String PLAN_TIME = "planTime";
    public static final String SHOP_OPEN_TIME = "shopOpenTime";
  }

  /** 租户 */
  private String tenant;
  /** 组织Id */
  private String orgId;
  /** 计划名称 */
  private String planName;

  /** 门店 */
  private String shop;
  /** 门店CODE */
  private String shopCode;
  /** 门店类型 */
  private String shopName;
  /** 门店类型 */
  private String shopType;
  /** 区域 */
  private String area;
  /** 区域code */
  private String areaCode;
  /** 区域名称 */
  private String areaName;

  /** 生效开始时间 */
  private Date effectiveStartTime;
  /** 生效结束时间 */
  private Date effectiveEndTime;
  /** 补贴金额 */
  private BigDecimal amount;
  /** 已用额度 */
  private BigDecimal usedAmount = BigDecimal.ZERO;
  /** 预扣额度 */
  private BigDecimal preQuota = BigDecimal.ZERO;
  /** 状态 */
  private String state;

  /** 状态 */
  private String ext;
  /** 扩展字段对象 */
  private ObjectNode expend;

  /** 开店时间*/
  private Date businessHour;

  @QueryEntity(SubsidyPlan.class)
  public static class Queries extends QueryFactors.StandardEntity {

    // 获取类中的字段名称
    public static final QueryFactorName PREFIX = QueryFactorName.prefix(SubsidyPlan.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String ORG_ID = PREFIX.nameOf("orgId");
    @QueryField
    public static final String PLAN_NAME = PREFIX.nameOf("planName");
    @QueryField
    public static final String EFFECTIVE_START_TIME = PREFIX.nameOf("effectiveStartTime");
    @QueryField
    public static final String EFFECTIVE_END_TIME = PREFIX.nameOf("effectiveEndTime");
    @QueryField
    public static final String SHOP = PREFIX.nameOf("shop");
    @QueryField
    public static final String SHOP_CODE = PREFIX.nameOf("shopCode");
    @QueryField
    public static final String SHOP_NAME = PREFIX.nameOf("shopName");
    @QueryField
    public static final String SHOP_TYPE = PREFIX.nameOf("shopType");
    @QueryField
    public static final String AREA = PREFIX.nameOf("area");
    @QueryField
    public static final String AREA_CODE = PREFIX.nameOf("areaCode");
    @QueryField
    public static final String STATE = PREFIX.nameOf("state");
    @QueryOperation
    public static final String SHOP_KEYWORD_LIKE = PREFIX.nameOf("shopKeyword like");
    @QueryOperation
    public static final String XCX_KEYWORD_LIKE = PREFIX.nameOf("xcxKeyword like");

  }

  @SchemaMeta
  @MapToEntity(SubsidyPlan.class)
  public class SubsidyPlanSchema extends Schemas.StandardEntity {

    @TableName
    public static final String TABLE_NAME = "subsidy_plan";

    public static final String TABLE_ALIAS = "_subsidy_plan";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    public static final String ORG_ID = "org_id";

    @ColumnName
    @MapToProperty(value = "planName")
    public static final String PLAN_NAME = "plan_name";
    @ColumnName
    @MapToProperty(value = "shop")
    public static final String SHOP = "shop";
    @ColumnName
    @MapToProperty(value = "shopCode")
    public static final String SHOP_CODE = "shop_code";
    @ColumnName
    @MapToProperty(value = "shopName")
    public static final String SHOP_NAME = "shop_name";
    @ColumnName
    @MapToProperty(value = "shopType")
    public static final String SHOP_TYPE = "shop_type";
    @ColumnName
    public static final String AREA = "area";
    @ColumnName
    @MapToProperty(value = "areaCode")
    public static final String AREA_CODE = "area_code";
    @ColumnName
    @MapToProperty(value = "areaName")
    public static final String AREA_NAME = "area_name";
    @ColumnName
    @MapToProperty(value = "effectiveStartTime")
    public static final String EFFECTIVE_START_TIME = "effective_start_time";
    @ColumnName
    @MapToProperty(value = "effectiveEndTime")
    public static final String EFFECTIVE_END_TIME = "effective_end_time";
    @ColumnName
    public static final String AMOUNT = "amount";
    @ColumnName
    @MapToProperty(value = "usedAmount")
    public static final String USED_AMOUNT = "used_amount";
    @ColumnName
    @MapToProperty(value = "preQuota")
    public static final String PRE_QUOTA = "pre_quota";
    @ColumnName
    public static final String STATE = "state";
    @ColumnName
    public static final String EXT = "ext";
  }
}
