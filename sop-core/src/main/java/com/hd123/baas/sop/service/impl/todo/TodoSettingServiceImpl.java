package com.hd123.baas.sop.service.impl.todo;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.todo.TodoScene;
import com.hd123.baas.sop.service.api.todo.TodoSceneService;
import com.hd123.baas.sop.service.api.todo.TodoSetting;
import com.hd123.baas.sop.service.api.todo.TodoSettingService;
import com.hd123.baas.sop.service.dao.todo.TodoSettingDao;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mibo
 */
@Service
public class TodoSettingServiceImpl implements TodoSettingService {


  @Autowired
  private TodoSettingDao todoSettingDao;
  @Autowired
  private TodoSceneService todoSceneService;

  @Override
  @Tx
  public String saveNew(String tenant, String orgId, TodoSetting todoSetting, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notNull(todoSetting, "todoSetting");
    //判断场景是否存在，并设置场景状态为已引用
    checkAndUpdate(tenant, orgId, todoSetting.getSceneId());
    todoSetting.setUuid(IdGenUtils.buildRdUuid());
    todoSetting.setId(IdGenUtils.buildIidAsString());
    todoSetting.setOrgId(orgId);
    todoSetting.setCreateInfo(operateInfo);
    todoSetting.setLastModifyInfo(operateInfo);
    todoSettingDao.insert(tenant, todoSetting);
    return todoSetting.getUuid();
  }

  @Override
  @Tx
  public void saveModify(String tenant, String orgId, TodoSetting todoSetting, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notNull(todoSetting, "todoSetting");
    Assert.hasText(todoSetting.getUuid(), "uuid");
    //判断场景是否存在，并设置场景状态为已引用
    checkAndUpdate(tenant, orgId, todoSetting.getSceneId());
    TodoSetting history = todoSettingDao.get(tenant, todoSetting.getUuid(), true);
    if (history == null) {
      throw new BaasException("数据不存在，无法修改");
    }
    todoSetting.setOrgId(orgId);
    todoSetting.setLastModifyInfo(operateInfo);
    todoSetting.setCreateInfo(history.getCreateInfo());
    todoSettingDao.update(tenant, todoSetting);
  }

  @Override
  public TodoSetting get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    return todoSettingDao.get(tenant, uuid, false);
  }

  @Override
  public QueryResult<TodoSetting> query(String tenant, String orgId, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notNull(qd, "qd");
    qd.addByField(TodoSetting.Queries.ORG_ID, Cop.EQUALS, orgId);
    return todoSettingDao.query(tenant, qd);
  }

  @Override
  public List<TodoSetting> listBySceneId(String tenant, String orgId, String sceneId) {
    return this.todoSettingDao.listBySceneId(tenant, orgId, sceneId);
  }

  private void checkAndUpdate(String tenant, String orgId, String sceneId) throws BaasException {
    TodoScene sceneHistory = todoSceneService.get(tenant, sceneId);
    if (sceneHistory == null) {
      throw new BaasException("场景不存在");
    }
    if (sceneHistory.getIsUsed() != true) {
      sceneHistory.setIsUsed(true);
      todoSceneService.saveModify(tenant, orgId, sceneHistory, sceneHistory.getLastModifyInfo());
    }
  }
}
