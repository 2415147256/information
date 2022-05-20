package com.hd123.baas.sop.service.impl.taskplan;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroup;
import com.hd123.baas.sop.service.api.taskplan.TaskPlan;
import com.hd123.baas.sop.service.dao.taskgroup.TaskGroupDaoBof;
import com.hd123.baas.sop.service.dao.taskplan.TaskPlanDaoBof;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.service.api.taskplan.TaskPlanService;

import java.util.*;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Service
public class TaskPlanServiceImpl implements TaskPlanService {

  @Autowired
  TaskPlanDaoBof dao;

  @Autowired
  TaskGroupDaoBof taskGroupDao;

  @Override
  @Tx
  public void saveNew(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskPlan, "任务");
    Assert.notNull(taskPlan.getTaskGroup(), "分组ID");
    Assert.notNull(taskPlan.getName(), "名称");
    taskPlan.setTenant(tenant);
    TaskGroup taskGroup = taskGroupDao.get(tenant, taskPlan.getTaskGroup());
    if (taskGroup == null) {
      throw new BaasException("任务组不存在");
    }

    TaskPlan taskPlanD = dao.getByGroupIdAndName(tenant, taskPlan.getTaskGroup(), taskPlan.getName());

    if (taskPlanD != null) {
      if (taskPlan.getTemplateCls() != null) {
        throw new BaasException("同一任务组下相同固定任务只能生成一个");
      }
      throw new BaasException("同一任务组下的任务名称不能相同，请重新填写");
    }
    if (taskPlan.getUuid() == null) {
      taskPlan.setUuid(UUID.randomUUID().toString());
    }
    if (taskPlan.getRemindTime() == null) {
      taskPlan.setRemindTime(taskGroup.getRemindTime());
    }
    TaskPlan maxSort = dao.maxSort(tenant, taskPlan.getTaskGroup());
    if (maxSort == null) {
      taskPlan.setSort(1);
    } else {
      taskPlan.setSort(maxSort.getSort() + 1);
    }

    int count = dao.insert(tenant, taskPlan, operateInfo);
    if (count != 1) {
      throw new BaasException("新增任务失败");
    }
  }

  @Override
  @Tx
  public void saveModify(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskPlan, "任务");
    Assert.notNull(taskPlan.getTaskGroup(), "分组ID");
    Assert.notNull(taskPlan.getName(), "名称");
    taskPlan.setTenant(tenant);
    verifyTaskPlan(tenant, taskPlan);
    int count = dao.update(tenant, taskPlan, operateInfo);
    if (count != 1) {
      throw new BaasException("编辑任务失败");
    }
  }

  @Override
  public TaskPlan get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    return dao.get(tenant, uuid);
  }

  @Override
  public List<TaskPlan> list(String tenant, String groupId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "任务组id");
    return dao.list(tenant, groupId);
  }

  @Override
  @Tx
  public void effectiveByGroupId(String tenant, String groupId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "任务组id");
    dao.setTaskPlanEnforced(tenant, groupId);
  }

  @Override
  @Tx
  public void delete(String tenant, String group, String uuid) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    TaskPlan taskPlan = this.get(tenant, uuid);
    if (taskPlan == null) {
      throw new BaasException("该任务不存在");
    }
    dao.delete(tenant, group, uuid);
  }

  @Override
  public void makeOverdue(String tenant, String group, String uuid) throws BaasException {
    Assert.notNull(tenant,"租户");
    Assert.notNull(group,"任务组");
    Assert.notNull(uuid,"uuid");
    TaskPlan taskPlan = this.get(tenant, uuid);
    if (taskPlan == null) {
      throw new BaasException("该任务不存在");
    }
    dao.overdue(tenant,group,uuid);
  }

  @Override
  @Tx
  public void setSort(String tenant, String groupId, List<String> uuids) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "任务组");
    Assert.notNull(uuids, "uuids");
    List<TaskPlan> list = this.list(tenant, groupId);
    if (CollectionUtils.isEmpty(list) || list.size() != uuids.size()) {
      throw new BaasException("任务异常,请联系开发");
    }
    for (int i = 0; i < uuids.size(); i++) {
      dao.setSort(tenant, uuids.get(i), i + 1);
    }
  }

  @Override
  public QueryResult<TaskPlan> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant,"租户");
    Assert.notNull(qd,"qd");
    return dao.query(tenant,qd);
  }

  /**
   * 校验任务
   */
  private void verifyTaskPlan(String tenant, TaskPlan taskPlan) throws BaasException {
    TaskPlan taskDao = dao.get(tenant, taskPlan.getUuid());
    if (taskDao == null) {
      throw new BaasException("任务不存在");
    }
    TaskGroup taskGroup = taskGroupDao.get(tenant, taskPlan.getTaskGroup());
    if(taskGroup == null){
      throw new BaasException("任务组不存在");
    }
    if (taskDao.getTemplateCls() != null) {
      throw new BaasException("固定任务不可修改");
    }
    TaskPlan taskPlanD = dao.getByGroupIdAndName(tenant, taskPlan.getTaskGroup(), taskPlan.getName());

    if (taskPlanD != null && !taskPlanD.getUuid().equals(taskPlan.getUuid())) {
      throw new BaasException("同一任务组下的任务名称不能相同，请重新填写");
    }
  }
}
