package com.hd123.baas.sop.service.api.task;

import java.util.List;

import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @Author maodapeng
 * @Since
 */
public interface ShopTaskSummaryService {
  /**
   * 计划列表 按计划时间分组
   * 
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<PlanSummary> queryPlanSummary(String tenant, QueryDefinition qd);

  /**
   * 计划详情
   * 
   * @param tenant
   * @param plan
   * @param periodCode
   * @return
   */
  PlanSummary getPlanSummary(String tenant, String plan, String periodCode);

  /**
   * 门店统计列表
   * 
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<ShopTaskSummary> query(String tenant, QueryDefinition qd);

  /**
   * 门店统计列表，根据登陆用户进行过滤
   *
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<ShopTaskSummary> queryByLoginId(String tenant, String loginId, QueryDefinition qd);

  /**
   * 获取门店统计
   * 
   * @param tenant
   * @param uuid
   * @return
   */
  ShopTaskSummary get(String tenant, String uuid);

  /**
   * 统计门店任务完成情况 1。统计分数 2. 更新排名 3. 判断是否门店是否完成任务
   * 
   * @param tenant
   */
  void summary(String tenant, String uuid) throws BaasException;

  /**
   * 查询已过期的未完成的计划列表
   */
  List<PlanSummary> listExpirePlan(String tenant);

  /**
   * 查询巡检任务汇总
   * 
   * @param tenant
   *          租户
   * @param qd
   *          查询条件
   * @return ShopTaskSummary
   */
  QueryResult<ShopTaskSummary> mobileQuery(String tenant, QueryDefinition qd);

  /**
   * 查看巡检任务汇总
   * 
   * @param tenant
   *          租户
   * @param shopSummaryId
   *          summaryId
   * @return ShopTaskSummary
   */
  ShopTaskSummary mobileGet(String tenant, String shopSummaryId);
}
