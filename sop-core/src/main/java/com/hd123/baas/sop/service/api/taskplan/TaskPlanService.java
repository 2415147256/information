package com.hd123.baas.sop.service.api.taskplan;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * @author zhengzewang on 2020/11/3.
 */
public interface TaskPlanService {
  /**
   * 新增任务计划
   */
  void saveNew(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException;

  /**
   * 修改任务计划
   */
  void saveModify(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException;

  /**
   * 获取指定任务计划
   */
  TaskPlan get(String tenant, String uuid);

  /**
   * 获取指定任务组下所有任务计划
   * 
   * @param groupId
   *          任务组id
   * @return
   */
  List<TaskPlan> list(String tenant, String groupId);

  /**
   * 设置任务组下所有任务计划状态为生效
   * 
   * @param tenant
   * @param groupId
   *          任务组id
   */
  // TODO youjiawei 修改
  void effectiveByGroupId(String tenant, String groupId);

  /**
   * 删除指定任务计划
   */
  void delete(String tenant, String group, String uuid) throws BaasException;

  /**
   * 设置任务失效
   */
  void makeOverdue(String tenant, String group, String uuid) throws BaasException;

  /**
   * 根据任务计划的id顺序设置任务计划的排序值
   */
  void setSort(String tenant, String groupId, List<String> uuids) throws BaasException;

  /**
   * 查询任务
   */
  QueryResult<TaskPlan> query(String tenant, QueryDefinition qd);
}
