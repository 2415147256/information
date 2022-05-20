package com.hd123.baas.sop.service.api.taskgroup;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TaskGroupService {
  /**
   * 新增任务组
   *
   * @param tenant      租户
   * @param taskGroup   任务组对象
   * @param operateInfo 操作人
   * @throws BaasException 业务异常
   */
  void saveNew(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) throws BaasException;

  /**
   * 修改任务组
   *
   * @param tenant      租户
   * @param taskGroup   任务组对象
   * @param operateInfo 操作人
   * @throws BaasException 业务异常
   */
  int saveModify(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) throws BaasException;

  /**
   * 删除任务组，并删除任务组下所有任务计划
   */
  int delete(String tenant, String uuid) throws BaasException;

  /**
   * 获取任务组
   */
  TaskGroup get(String tenant, String uuid);

  /**
   * 查询任务组
   */
  QueryResult<TaskGroup> query(String tenant, QueryDefinition qd);

  /**
   * 批量门店绑定指定任务组
   *
   * @throws BaasException 业务异常
   */
  Boolean relateShops(String tenant, String orgId, String uuid, List<String> shopIds) throws BaasException;

  /**
   * 获取绑定指定任务组的门店id集
   */
  List<String> getRelateShops(String tenant, String uuid);

  /**
   * 获取绑定指定任务组的门店id集
   */
  Set<String> getRelateShops(String tenant, TaskGroupType type);

  /**
   * 获取巡检主题列表
   *
   * @param tenant       租户
   * @param queryResults 查询结果
   */
  List<TaskGroupData> packingBTaskGroupData(String tenant, TaskGroup... queryResults);

  /**
   * 发布任务组
   *
   * @param tenant      租户
   * @param uuid        uuid主键
   * @param operateInfo 操作人信息
   */
  void updateState(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 新建任务组
   *
   * @param tenant       租户
   * @param newTaskGroup 请求体
   * @param operateInfo  操作人信息
   * @return 主题ID
   */
  String saveNewTaskGroup(String tenant, TaskGroup newTaskGroup, OperateInfo operateInfo) throws BaasException;

  /**
   * 编辑任务组名称
   *
   * @param tenant      租户
   * @param taskGroup   需要修改的任务组信息
   * @param operateInfo 操作者信息
   */
  void updateTaskGroupName(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) throws BaasException;

  /**
   * 根据id删除任务组
   *
   * @param tenant 租户
   * @param uuid   主键
   */
  void deleteByUuid(String tenant, String uuid);

  /**
   * 根据任务组id查询对应的模版列表
   *
   * @param tenant 租户
   * @param owner  任务组id
   * @return 模板列表
   */
  List<TaskTemplate> queryByOwner(String tenant, String owner);

  /**
   * 根据uuid获取巡检明细
   *
   * @param tenant 租户
   * @param uuid   主键
   * @return 模板明细
   */
  TaskTemplate getTaskTemplate(String tenant, String uuid);

  /**
   * 保存新的taskTemplate模版内容
   *
   * @param tenant       租户
   * @param taskTemplate 模版内容
   * @param operateInfo  操作人信息
   * @return 模板内容的ID
   */
  String saveNewTaskTemplate(String tenant, TaskTemplate taskTemplate, OperateInfo operateInfo) throws BaasException;

  /**
   * taskTemplate内容编辑保存
   *
   * @param tenant       租户
   * @param taskTemplate 修改内容
   * @param operateInfo  操作人信息
   * @return 更新内容的ID
   */
  String saveModifyTaskTemplate(String tenant, TaskTemplate taskTemplate, OperateInfo operateInfo) throws BaasException;

  /**
   * 根据uuid删除任务模版
   *
   * @param tenant 租户
   * @param uuid   任务id
   */
  void deleteTaskTemplateByUuid(String tenant, String uuid);

  /**
   * 根据code信息查询出计划组列表
   *
   * @param tenant 租户
   * @param codes  codes
   * @return 主题列表
   */
  List<TaskGroup> listByCodes(String tenant, List<String> codes);

  /**
   * 查询所有主题列表
   *
   * @param tenant               租户
   * @param importTaskGroupNames 需要导入的主题列表
   * @return 主题列表
   */
  List<TaskGroup> listByTypeAndName(String tenant, String type, List<String> importTaskGroupNames);

  /**
   * 查询所有的任务列表
   *
   * @param tenant 租户
   * @return 任务内容列表
   */
  List<TaskTemplate> listTemplateByOwners(String tenant, List<String> owners);

  /**
   * 批量添加分组
   *
   * @param tenant              租户
   * @param insertTaskGroupList 任务分组列表
   */
  void saveBatch(String tenant, List<TaskGroup> insertTaskGroupList, OperateInfo operateInfo) throws BaasException;

  /**
   * 批量新增任务
   *
   * @param tenant                 租户
   * @param insertTaskTemplateList 任务列表
   * @param operateInfo            操作人信息
   */
  void saveTaskTemplateBatch(String tenant, Collection<TaskTemplate> insertTaskTemplateList, OperateInfo operateInfo);

  /**
   * 批量修改任务
   *
   * @param tenant                 租户
   * @param updateTaskTemplateList 任务列表
   * @param operateInfo            操作人信息
   */
  void updateTaskTemplateBatch(String tenant, Collection<TaskTemplate> updateTaskTemplateList, OperateInfo operateInfo);

  /**
   * 根据前端请求调整排序内容
   *
   * @param tenant       租户
   * @param templateList 模板内容排序列表
   */
  void adjustSeq(String tenant, List<TaskTemplate> templateList, OperateInfo operateInfo);

  /**
   * 根据ownerList获取对应的分组最大排序值
   *
   * @param tenant          租户
   * @param taskGroupIdList taskGroupIdList
   * @return 分组最大值列表
   */
  List<TaskTemplateMaxSeq> getTaskTemplateMaxSeqByOwnerList(String tenant, List<String> taskGroupIdList);
}
