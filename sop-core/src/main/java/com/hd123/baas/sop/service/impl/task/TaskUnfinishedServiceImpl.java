package com.hd123.baas.sop.service.impl.task;

import com.hd123.baas.sop.service.api.task.TaskUnfinished;
import com.hd123.baas.sop.service.api.task.TaskUnfinishedService;
import com.hd123.baas.sop.service.dao.task.TaskUnfinishedDaoBof;
import com.hd123.rumba.commons.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author guyahui
 * @Since
 */
@Service
public class TaskUnfinishedServiceImpl implements TaskUnfinishedService {

  @Autowired
  private TaskUnfinishedDaoBof taskUnfinishedDao;

  @Override
  public List<TaskUnfinished> query(String tenant, String operatorId, Integer pageStart, Integer pageSize) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(operatorId, "operatorId");
    Assert.notNull(pageStart, "pageStart");
    Assert.notNull(pageSize, "pageSize");

    return taskUnfinishedDao.query(tenant, operatorId, pageStart, pageSize);
  }

  @Override
  public long count(String tenant, String operatorId, String type) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(operatorId, "operatorId");
    Assert.hasText(type,"planType");

    return taskUnfinishedDao.count(tenant, operatorId, type);
  }
}
