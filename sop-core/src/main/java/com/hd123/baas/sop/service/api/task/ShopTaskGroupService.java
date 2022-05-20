package com.hd123.baas.sop.service.api.task;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.Date;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/4.
 */
public interface ShopTaskGroupService {
  /**
   * 查询门店任务组
   */
  QueryResult<ShopTaskGroup> query(String tenant, QueryDefinition qd);

  /**
   * 完成任务组
   */
  void finish(String tenant, String uuid, String appId, OperateInfo operateInfo) throws BaasException;

  /**
   * 门店日结任务检查
   * 
   * @param tenant
   * @param shopId
   * @return
   */
  DailyTaskFinishCheck finishCheck(String tenant, String shopId) throws BaasException;

  /**
   * 获取任务详情
   */
  ShopTaskGroup get(String tenant, String uuid);

  /**
   * 新增任务
   */
  void saveNew(String tenant, ShopTaskGroup shopTaskGroup);

  /**
   * 根据门店，任务组和计划时间查询门店任务组
   */
  ShopTaskGroup getByShopAndGroupIdAndPlanDate(String tenant, String shop, String groupId, Date planTime);

  /**
   * 检查该任务组下最近一个门店任务组任务是否全部完成，如果存在未完成，则设置为已过期，如全部完成，则完成该任务组 并返回最近门店任务组id
   */
  String checkLast(String tenant, String shop, String groupId);

  /**
   * 更新门店任务组的最后完成时间
   */
  void modifyEarliestFinishTime(String tenant, String uuid, Date earliestFinishTime);

  /**
   * 根据门店+日期+任务组状态 查询当前门店的任务组
   */
  List<ShopTaskGroup> listByShopAndPlanTimeAndState(String tenant, String shop, Date date, ShopTaskGroupState state);

}
