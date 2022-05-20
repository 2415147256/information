package com.hd123.baas.sop.service.api.taskpoints;

import java.math.BigDecimal;
import java.util.Date;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * 任务积分
 */
@Getter
@Setter
public class TaskPoints extends TenantStandardEntity {
  private static final long serialVersionUID = 707495707950288973L;
  /**
   * 发生类型
   */
  private TaskPointsOccurredType occurredType;
  /**
   * 发生对象ID
   */
  private String occurredUuid;
  /**
   * 发生对象说明
   */
  private String occurredDesc;
  /**
   * 发生时间
   */
  private Date occurredTime = new Date();
  /**
   * 积分
   */
  private BigDecimal points;
  /**
   * 人员ID
   */
  private String userId;
  /**
   * 人员名称
   */
  private String userName;

  @QueryEntity(TaskPoints.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = TaskPoints.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String OCCURRED_TYPE = PREFIX + "occurredType";
    @QueryField
    public static final String OCCURRED_UUID = PREFIX + "occurredUuid";
    @QueryField
    public static final String USER_ID = PREFIX + "userId";
    @QueryField
    public static final String OCCURRED_TIME = PREFIX + "occurredTime";
  }
}
