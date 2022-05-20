package com.hd123.baas.sop.service.api.task;

import com.hd123.rumba.commons.biz.entity.OperateInfo;

import java.util.List;

/**
 * @Author guyahui
 * @Since
 */
public interface TaskReadHistoryService {

  /**
   * 根据计划列表、操作者ID查询已读列表
   *
   * @param tenant     租户
   * @param planList   计划列表
   * @param operatorId 操作者Id
   * @return 已读列表
   */
  List<TaskReadHistory> listByPlanAndOperatorId(String tenant, List<String> planList, String operatorId, String type);

  /**
   * 保存已读信息
   * @param tenant 租户
   * @param taskReadHistory 已读任务对象
   * @param operateInfo 操作者信息
   * @return
   */
  String saveNew(String tenant, TaskReadHistory taskReadHistory, OperateInfo operateInfo);

  /**
   * 根据UK条件删除已读信息
   * @param tenant 租户
   * @param plan 计划ID
   * @param planPeriod 计划批次
   * @param OperatorId 操作者ID
   * @param type 类型
   */
  void deleteByUk(String tenant , String plan , String planPeriod , String operatorId , String type);
}
