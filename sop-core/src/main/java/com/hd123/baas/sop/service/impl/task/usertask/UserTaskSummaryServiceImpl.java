package com.hd123.baas.sop.service.impl.task.usertask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.service.api.task.usertask.UserTaskSummary;
import com.hd123.baas.sop.service.api.task.usertask.UserTaskSummaryService;
import com.hd123.baas.sop.service.dao.task.usertask.UserTaskSummaryDaoBof;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;

/**
 * @author W.J.H.7
 */
@Service
public class UserTaskSummaryServiceImpl implements UserTaskSummaryService {
  @Autowired
  private UserTaskSummaryDaoBof userTaskSummaryDao;

  @Override
  public QueryResult<UserTaskSummary> query(String tenant, QueryDefinition qd) {
    QueryResult<UserTaskSummary> query = userTaskSummaryDao.query(tenant, qd);
    return query;
  }

  @Override
  public UserTaskSummary getByUK(String tenant, String plan, String planPeriodCode, String operatorId) {
    return userTaskSummaryDao.getByUK(tenant, plan, planPeriodCode, operatorId);
  }
}
