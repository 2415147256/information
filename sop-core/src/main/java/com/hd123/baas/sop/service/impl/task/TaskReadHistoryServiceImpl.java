package com.hd123.baas.sop.service.impl.task;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.task.TaskReadHistory;
import com.hd123.baas.sop.service.api.task.TaskReadHistoryService;
import com.hd123.baas.sop.service.dao.task.TaskReadHistoryDaoBof;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author guyahui
 * @Since
 */
@Service
public class TaskReadHistoryServiceImpl implements TaskReadHistoryService {

  @Autowired
  private TaskReadHistoryDaoBof taskReadHistoryDao;

  @Override
  public List<TaskReadHistory> listByPlanAndOperatorId(String tenant, List<String> planList, String operatorId, String type) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(planList, "planList");
    Assert.hasText(operatorId, "operatorId");
    Assert.hasText(type, "type");

    return taskReadHistoryDao.listByPlanAndOperatorId(tenant, planList, operatorId, type);
  }

  @Override
  @Tx
  public String saveNew(String tenant, TaskReadHistory taskReadHistory, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(taskReadHistory, "taskReadHistory");
    String uuid = taskReadHistory.getUuid();
    if (StringUtils.isEmpty(uuid)) {
      uuid = IdGenUtils.buildRdUuid();
      taskReadHistory.setUuid(uuid);
    }
    taskReadHistoryDao.saveNew(tenant, taskReadHistory, operateInfo);
    return uuid;
  }

  @Override
  @Tx
  public void deleteByUk(String tenant, String plan, String planPeriod, String operatorId, String type) {
    Assert.hasText(tenant, "租户");
    Assert.hasText(plan, "plan");
    Assert.hasText(planPeriod, "planPeriod");
    Assert.hasText(operatorId, "operatorId");
    Assert.hasText(type, "type");

    taskReadHistoryDao.deleteByUk(tenant, plan, planPeriod, operatorId, type);
  }
}
