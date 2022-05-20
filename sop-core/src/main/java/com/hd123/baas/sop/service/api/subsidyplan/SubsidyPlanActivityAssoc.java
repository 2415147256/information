package com.hd123.baas.sop.service.api.subsidyplan;

import java.util.Date;
import java.util.List;

import com.hd123.baas.sop.service.api.promotion.Promotion;
import com.hd123.baas.sop.service.api.promotion.condition.DateRangeCondition;
import com.hd123.baas.sop.service.api.promotion.condition.TimePeriodCondition;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.jdbc.annotation.ColumnName;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.MapToProperty;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import com.hd123.rumba.commons.jdbc.entity.Schemas;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class SubsidyPlanActivityAssoc extends PEntity {

  /** 租户信息 */
  private String tenant;

  /** 计划id */
  private String owner;

  /** 活动类型 */
  private ActivityType activityType;
  /** 活动ID */
  private String activityId;
  /** 活动状态 */
  private ActivityState activityState;
  /** 活动名称 */
  private String activityName;
  /** 活动开始时间 */
  private Date activityStartTime;
  /** 活动结束时间 */
  private Date activityEndTime;
  /** 活动创建时间 */
  private Date created;
  /** 活动创建人ID */
  private String creatorId;
  /** 活动创建人名称 */
  private String creatorName;
  /** 活动最后修改时间 */
  private Date lastModified;
  /** 活动最后修改人id */
  private String lastModifierId;
  /** 活动修改人名称 */
  private String lastModifierName;

  /** 促销周期 */
  private DateRangeCondition.DateRangeCycle timeCycle;
  /** 时段促销 */
  private TimePeriodCondition timePeriodCondition;
  /** 促销设置 */
  private List<Promotion> promotions;

  @QueryEntity(SubsidyPlanActivityAssoc.class)
  public static class Queries extends QueryFactors.Entity {

    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(SubsidyPlanActivityAssoc.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String OWNER = PREFIX.nameOf("owner");
    @QueryField
    public static final String ACTIVITY_TYPE = PREFIX.nameOf("activityType");
  }

  @SchemaMeta
  @MapToEntity(SubsidyPlanActivityAssoc.class)
  public class SubsidyPlanActivityAssocSchema extends Schemas.Entity {

    @TableName
    public static final String TABLE_NAME = "subsidy_plan_activity_assoc";

    public static final String TABLE_ALIAS = "_subsidy_plan_activity_assoc";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    public static final String OWNER = "owner";
    @ColumnName
    @MapToProperty(value = "activityType")
    public static final String ACTIVITY_TYPE = "activity_type";
    @ColumnName
    @MapToProperty(value = "activityId")
    public static final String ACTIVITY_ID = "activity_id";
    @ColumnName
    @MapToProperty(value = "activityStartTime")
    public static final String ACTIVITY_START_TIME = "activity_start_time";
    @ColumnName
    @MapToProperty(value = "activityEndTime")
    public static final String ACTIVITY_END_TIME = "activity_end_time";

  }
}
