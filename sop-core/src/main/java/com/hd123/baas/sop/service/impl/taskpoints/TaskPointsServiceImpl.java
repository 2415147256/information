package com.hd123.baas.sop.service.impl.taskpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.taskpoints.TaskPoints;
import com.hd123.baas.sop.service.api.taskpoints.TaskPointsService;
import com.hd123.baas.sop.service.dao.taskpoints.TaskPointsDaoBof;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author liyan
 * @date 2021/6/3
 */
@Service
public class TaskPointsServiceImpl implements TaskPointsService {
  @Autowired
  TaskPointsDaoBof taskPointsDao;

  @Override
  public QueryResult<TaskPoints> query(String tenant, QueryDefinition qd) {
    return taskPointsDao.query(tenant, qd);
  }

  @Override
  @Tx
  public String saveNew(String tenant, TaskPoints taskPoints) throws BaasException {
    String occurredType = taskPoints.getOccurredType().name(), occurredUuid = taskPoints.getOccurredUuid();
    TaskPoints oldTaskPoints = taskPointsDao.getByUK(tenant, occurredType, occurredUuid, true);
    if (null != oldTaskPoints) {
      throw new BaasException("任务积分已经存在，不允许重复添加[{}: {}]", occurredType, occurredUuid);
    }
    taskPointsDao.insert(tenant, taskPoints);
    return taskPoints.getUuid();
  }
}
