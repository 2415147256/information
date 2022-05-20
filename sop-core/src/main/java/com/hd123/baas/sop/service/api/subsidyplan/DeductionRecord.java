package com.hd123.baas.sop.service.api.subsidyplan;

import java.math.BigDecimal;
import java.util.Date;

import com.hd123.rumba.commons.biz.entity.Entity;
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
@Getter
@Setter
public class DeductionRecord extends Entity {
  /** 租户 */
  private String tenant;

  /** 计划id */
  private String owner;
  /** 扣款金额 */
  private BigDecimal amount;
  /** 扣款类型 */
  private DeductionType type;

  /** 活动ID */
  private String activityId;
  /** 活动状态 */
  private ActivityType activityType;
  /** 活动名称 */
  private String activityName;
  /** 活动开始时间 */
  private Date activityStartTime;
  /** 活动结束时间 */
  private Date activityEndTime;
  /** 活动扣款时间 */
  private Date deductionTime;
  /** 活动创建人ID */
  private String activityCreator;
  /** 活动创建人名称 */
  private String activityCreatorName;
  /** 创建时间 */
  private Date created;

  /** H6扣款id */
  private String h6DeductionId;

  /** 扣款状态 SUCCESS 成功,FAIL 失败,PREPARE 准备 */
  private DeductionState state = DeductionState.PREPARE;
  /** 错误信息 */
  private String errorMsg;
  /** 扣款变更原因 */
  private String remark;
  @QueryEntity(DeductionRecord.class)
  public static class Queries extends QueryFactors.Entity {

    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(DeductionRecord.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String OWNER = PREFIX.nameOf("owner");
    @QueryField
    public static final String STATE = PREFIX.nameOf("state");
    @QueryField
    public static final String ACTIVITY_ID = PREFIX.nameOf("activityId");
    @QueryField
    public static final String DEDUCTION_TIME = PREFIX.nameOf("deductionTime");
    @QueryField
    public static final String CREATED = PREFIX.nameOf("created");
    @QueryOperation
    public static final String DETAIL_TYPE_EQUALS = PREFIX.nameOf("detailType equals");
  }

  @SchemaMeta
  @MapToEntity(DeductionRecord.class)
  public class DeductionRecordSchema extends Schemas.Entity {

    @TableName
    public static final String TABLE_NAME = "subsidy_plan_deduction_record";

    public static final String TABLE_ALIAS = "_subsidy_plan_deduction_record";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    public static final String OWNER = "owner";
    @ColumnName
    public static final String AMOUNT = "amount";
    @ColumnName
    public static final String TYPE = "type";
    @ColumnName
    public static final String STATE = "state";
    @ColumnName
    @MapToProperty(value = "errorMsg")
    public static final String ERROR_MSG = "error_msg";
    @ColumnName
    @MapToProperty(value = "remark")
    public static final String REMARK = "remark";
    @ColumnName
    @MapToProperty(value = "deductionTime")
    public static final String DEDUCTION_TIME = "deduction_time";
    @ColumnName
    @MapToProperty(value = "h6DeductionId")
    public static final String H6_DEDUCTION_ID = "h6_deduction_id";
    @ColumnName
    @MapToProperty(value = "activityId")
    public static final String ACTIVITY_ID = "activity_id";
    @ColumnName
    @MapToProperty(value = "activityType")
    public static final String ACTIVITY_TYPE = "activity_type";
    @ColumnName
    @MapToProperty(value = "activityName")
    public static final String ACTIVITY_NAME = "activity_name";
    @ColumnName
    @MapToProperty(value = "activityCreator")
    public static final String ACTIVITY_CREATOR = "activity_creator";
    @ColumnName
    @MapToProperty(value = "activityCreatorName")
    public static final String ACTIVITY_CREATOR_NAME = "activity_creator_name";
    @ColumnName
    @MapToProperty(value = "created")
    public static final String CREATED = "created";
  }
}
