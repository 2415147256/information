package com.hd123.baas.sop.service.api.taskplan;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * @author guyahui
 * @date 2021/5/6 19:46
 */
public interface TaskPlanNewService {

  /**
   * 新增任务计划
   *
   * @param tenant      租户
   * @param taskPlan    请求体信息
   * @param operateInfo 操作者信息
   */
  String saveNew(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException;

  /**
   * 根据uuid获取单个任务计划
   *
   * @param tenant 租户
   * @param uuid   主键
   * @return
   */
  TaskPlan get(String tenant, String uuid);

  /**
   * 查询任务计划
   *
   * @param tenant 租户
   * @param qd     查询条件
   * @return
   */
  QueryResult<TaskPlan> query(String tenant, QueryDefinition qd);

  /**
   * 删除任务计划
   *
   * @param tenant 租户
   * @param uuid   uuid
   */
  void delete(String tenant, String uuid);

  /**
   * 根据owner获取列表
   *
   * @param tenant 租户
   * @param owner  任务计划ID
   * @return
   */
  List<TaskPlanLine> listTaskPlanLineByOwner(String tenant, String owner);

  /**
   * 根据owner 列表获取列表
   *
   * @param tenant 租户
   * @param owners 任务计划ID列表
   * @return
   */
  List<TaskPlanLine> listTaskPlanLineByOwner(String tenant, List<String> owners);

  /**
   * 保存更新
   *
   * @param tenant      租户
   * @param taskPlan    变更信息
   * @param operateInfo 操作者信息
   * @return
   */
  String saveModify(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException;

  /**
   * 分页查询明细
   *
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<TaskPlanLine> queryTaskPlanLine(String tenant, QueryDefinition qd);

  /**
   * 终止指定的任务计划
   *
   * @param tenant 租户
   * @param uuid   uuid
   */
  void terminate(String tenant, String uuid, OperateInfo operateInfo);

  /**
   * 更改任务计划状态
   *
   * @param tenant 租户
   * @param uuid   uuid
   * @param state  状态
   */
  void updateState(String tenant, String uuid, String state, OperateInfo operateInfo);

  /**
   * 更新发布时间
   *
   * @param tenant                 租户
   * @param uuid                   uuid
   * @param publishTaskDateCollect 任务发布时间集合
   */
  void updatePublishTaskDateCollect(String tenant, String uuid, String publishTaskDateCollect);

  /**
   * 获取taskPlan中的所有租户
   *
   * @return 数据列表
   */
  List<TaskPlan> listTenant();

  /**
   * 新增普通任务计划
   *
   * @param tenant      租户
   * @param taskPlan    请求体信息
   * @param operateInfo 操作者信息
   */
  String saveAssignableTaskPlan(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException;

  /**
   * 保存普通任务更新
   *
   * @param tenant      租户
   * @param taskPlan    变更信息
   * @param operateInfo 操作者信息
   * @return
   */
  String saveModifyAssignableTaskPlan(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException;

  /**
   * 由taskPlanId集合获取item集合
   *
   * @param tenant      租户
   * @param taskPlanIds 任务ID集合
   * @return 结果集
   */
  List<TaskPlanItem> listTaskPlanItemByTaskPlanIds(String tenant, List<String> taskPlanIds);

  /**
   * 删除普通任务
   *
   * @param tenant 租户
   * @param uuid   uuid
   */
  void deleteAssignableTaskPlan(String tenant, String uuid);

  /**
   * 发布指定任务
   *
   * @param tenant 租户
   * @param uuid   uuid
   */
  void publishAssignableTaskPlan(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 终止任务计划
   *
   * @param tenant 租户
   * @param uuid   uuid
   * @return 反馈信息
   */
  void terminateAssignableTaskPlan(String tenant, String uuid, OperateInfo operateInfo);

  /**
   * 修改并发布
   *
   * @param tenant      租户
   * @param taskPlan    对象
   * @param operateInfo 操作者
   * @return
   * @throws BaasException
   */
  String saveModifyAndPublish(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException;

  /**
   * 保存并发布
   *
   * @param tenant      租户
   * @param taskPlan    对象
   * @param operateInfo 操作者
   * @return
   * @throws BaasException
   */
  String saveNewAndPublish(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException;

  /**
   * 根据计划ID集合查询计划列表
   *
   * @param tenant  租户
   * @param planIds 计划ID列表
   * @return
   */
  List<TaskPlan> listByUuids(String tenant, List<String> planIds);
}
