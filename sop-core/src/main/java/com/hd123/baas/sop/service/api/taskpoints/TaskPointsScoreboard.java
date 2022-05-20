package com.hd123.baas.sop.service.api.taskpoints;

import java.math.BigDecimal;

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
public class TaskPointsScoreboard extends TenantStandardEntity {
  /**
   * 人员ID
   */
  private String userId;
  /**
   * 人员名称
   */
  private String userName;
  /**
   * 积分
   */
  private BigDecimal points;
  /**
   * 积分
   */
  private Long rank;
  /**
   * 完成任务数
   */
  private Long finished = 0L;
  /**
   * 总任务数
   */
  private Long total = 0L;

  @QueryEntity(TaskPointsScoreboard.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = TaskPointsScoreboard.class.getName() + "::";
    @QueryField
    public static final String USER_ID = PREFIX + "userId";
  }
}
