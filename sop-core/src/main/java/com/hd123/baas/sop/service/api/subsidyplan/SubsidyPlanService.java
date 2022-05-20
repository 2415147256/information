package com.hd123.baas.sop.service.api.subsidyplan;

import java.util.Date;
import java.util.List;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author liuhaoxin
 */
public interface SubsidyPlanService {

  /**
   * 保存补贴计划
   *
   * @param tenant
   *     租户
   * @param subsidyPlans
   *     补贴计划列表
   * @param operateInfo
   *     操作信息
   * @return 错误补贴集合
   */
  void saveNew(String tenant, List<SubsidyPlan> subsidyPlans, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 删除未生效补贴计划
   *
   * @param tenant
   *     租户
   * @param uuid
   *     补贴计划ID
   */
  void delete(String tenant, String uuid) throws BaasException;

  /**
   * 保存扣款记录
   *
   * @param tenant
   *     保存记录时间
   * @param deductionRecords
   *     扣款记录
   */
  void deductionSaveNew(String tenant, List<DeductionRecord> deductionRecords)
      throws Exception;

  /**
   * 保存调整记录
   *
   * @param tenant
   *     租户
   * @param newSubsidyPlan
   *     新补贴计划
   * @param oldSubsidyPlan
   *     旧补贴计划
   * @param operateInfo
   *     操作信息
   */
  void logSaveNew(String tenant, SubsidyPlan newSubsidyPlan, SubsidyPlan oldSubsidyPlan, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 编辑补贴计划
   *
   * @param tenant
   *     租户
   * @param subsidyPlan
   *     补贴计划列表
   * @param operateInfo
   *     操作信息
   * @return 错误补贴集合
   */
  void saveModify(String tenant, SubsidyPlan subsidyPlan, OperateInfo operateInfo) throws BaasException;


  /**
   * @param tenant
   *     租户
   * @param type
   *     活动类型
   * @param activityIds
   *     活动ID list
   */
  void relation(String tenant, ActivityType type, List<String> activityIds);

  /**
   * 终止
   *
   * @param tenant
   *     租户
   * @param planId
   *     补贴id
   * @param operation
   *     操作信息
   */
  void terminate(String tenant, String planId, TerminateType operation, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 补贴计划生效
   *
   * @param tenant
   *     租户
   * @param date
   *     生效范围内日期
   * @param state
   *     需要生效的生效类型
   */
  void updateEffectSubsidyPlanByDate(String tenant, Date date, String... state);

  /**
   * 补贴计划失效
   *
   * @param tenant
   *     租户
   * @param date
   *     生效范围内日期
   * @param state
   *     需要生效的生效类型
   */
  void updateExpireSubsidyPlanByDate(String tenant, Date date, String... state);

  /**
   * 查看详情
   *
   * @param tenant
   *     租户id
   * @param planId
   *     计划id
   */
  SubsidyPlan get(String tenant, String planId, String... fetchParts);

  /**
   * 查看调整日志
   *
   * @param tenant
   *     租户
   * @param qd
   *     自定义查询
   */
  QueryResult<SubsidyPlanLog> logQuery(String tenant, QueryDefinition qd);

  /**
   * 查询补贴计划信息
   *
   * @param tenant
   *     租户
   * @param qd
   *     自定义查询
   * @param fetchParts
   *     分片
   * @return 补贴计划信息
   */
  QueryResult<SubsidyPlan> query(String tenant, QueryDefinition qd, String... fetchParts);

  /**
   * 查询门店补贴计划列表
   *
   * @param tenant
   *     租户
   * @param shops
   *     门店列表
   * @param effectiveStartTime
   *     生效开始时间
   * @param effectiveEndTime
   *     生效结束时间
   * @return 补贴计划列表
   */
  void checkShopExistPlan(String tenant, String orgId, List<String> shops, Date effectiveStartTime,
      Date effectiveEndTime)
      throws BaasException;

  void checkShopExistPlan(String tenant, List<String> shops, Date effectiveStartTime, Date effectiveEndTime)
      throws BaasException;

  /**
   * 活动查询
   *
   * @param tenant
   *     租户
   * @param owner
   *     补贴计划id
   * @param subsidyActivityState
   *     计划活动类型
   * @param activityType
   *     活动类型
   * @return 关联活动分页列表
   */
  QueryResult<SubsidyPlanActivityAssoc> activityQuery(String tenant, String owner,
      SubsidyActivityState subsidyActivityState, List<ActivityType> activityType) throws BaasException;

  /**
   * 扣款记录-查询
   *
   * @param tenant
   *     租户信息
   * @param qd
   *     自定义查询
   * @return 扣款记录信息
   */
  QueryResult<DeductionRecord> deductionQuery(String tenant, QueryDefinition qd);

  /**
   * 当天生效的补贴计划
   */
  List<SubsidyPlan> listByEffectiveDateScope(String tenant, Date effectDate, String... state);

  /**
   * 补贴计划修改后时间异常关联的活动
   *
   * @param tenant
   *     租户
   * @param subsidyPlan
   *     补贴计划
   */
  List<ActivityRelationException> activityEditCheck(String tenant, SubsidyPlan subsidyPlan) throws BaasException;

  /**
   * 生效补贴计划
   *
   * @param tenant
   *     租户
   * @param uuid
   *     计划id
   * @param operateInfo
   *     操作信息
   */
  void effect(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 批量生效补贴计划
   *
   * @param tenant
   *     租户
   * @param uuids
   *     计划id集合
   * @param operateInfo
   *     操作信息
   */
  void effect(String tenant, List<String> uuids, OperateInfo operateInfo);
}
