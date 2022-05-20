package com.hd123.baas.sop.service.api.task.usertask;

import java.math.BigDecimal;
import java.util.Date;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 * 
 */
@Setter
@Getter
public class UserTaskSummary {
  // ID
  private String uuid;
  // 计划ID
  private String tenant;
  private String plan;
  private String planCode;
  private String planName;

  private Date finishedDate;
  private Date planStartTime;
  private Date planEndTime;
  private String planPeriodCode;
  private String planPeriod;
  private String operatorId;

  private String state;

  private BigDecimal shopCount = BigDecimal.ZERO;
  private BigDecimal finishedShopCount = BigDecimal.ZERO;

  @QueryEntity(UserTaskSummary.class)
  public static class Queries extends QueryFactors.Entity {
    private static final String PREFIX = UserTaskSummary.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryField
    public static final String PLAN = PREFIX + "plan";
    @QueryField
    public static final String PLAN_CODE = PREFIX + "planCode";
    @QueryField
    public static final String PLAN_NAME = PREFIX + "planName";
    @QueryField
    public static final String PLAN_PERIOD_CODE = PREFIX + "planPeriodCode";
    @QueryField
    public static final String PLAN_END_TIME = "planEndTime";
    @QueryField
    public static final String OPERATOR_ID = "operatorId";
    @QueryOperation
    public static final String PLAN_KEYWORD_LIKE = PREFIX + "planKeyword like";
  }

}
