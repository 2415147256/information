package com.hd123.baas.sop.service.api.task.usertask;

import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;

/**
 * @author W.J.H.7
 */
public interface UserTaskSummaryService {
  /**
   * 计划列表
   *
   */
  QueryResult<UserTaskSummary> query(String tenant, QueryDefinition qd);

  /**
   * 通过联合主键获取任务统计详情
   * 
   * @param tenant
   *          租户
   * @param plan
   *          计划ID
   * @param planPeriodCode
   *          计划周期
   * @param operatorId
   *          执行人
   * @return UserTaskSummary
   */
  UserTaskSummary getByUK(String tenant, String plan, String planPeriodCode, String operatorId);
}
