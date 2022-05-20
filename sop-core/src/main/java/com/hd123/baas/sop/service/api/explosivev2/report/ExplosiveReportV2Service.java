package com.hd123.baas.sop.service.api.explosivev2.report;

import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Line;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * 爆品活动日志服务
 *
 * @author liuhaoxin
 * @date 2021-12-2
 */
public interface ExplosiveReportV2Service {

  /**
   * 保存爆品活动每日报表
   *
   * @param tenant
   *     租户
   * @param dailyReports
   *     爆品活动每日报表集合
   * @param operateInfo
   *     操作时间
   */
  void batchSave(String tenant, List<ExplosiveSignV2DailyReport> dailyReports, OperateInfo operateInfo);

  /**
   * 获取日期接口
   *
   * @param tenant
   *     租户
   * @param logId
   *     爆品活动日志
   * @param fetchParts
   *     分片
   * @return ExplosiveLogV2 爆品活动日志
   */
  ExplosiveLogV2 getLog(String tenant, String logId, String... fetchParts);

  /**
   * 查询爆品统计信息
   *
   * @param tenant
   *     租户
   * @param qd
   *     统计条件
   * @return QueryResult<ExplosiveSignV2DailyReport> 统计查询结果
   */
  QueryResult<ExplosiveSignV2DailyReport> query(String tenant, QueryDefinition qd);

  /**
   * 查询爆品商品统计
   *
   * @param tenant
   *     租户
   * @param orgId
   *     组织id
   * @param qd
   *     租户
   * @return QueryResult<ExplosiveSignV2DailyReport> 统计查询结果
   */
  QueryResult<ExplosiveReportSummary> queryGroupBySku(String tenant, String orgId, QueryDefinition qd) throws BaasException;

  /**
   * 门店单据同步
   *
   * @param tenant
   *     租户
   * @param explosiveLogV2
   *     爆品活动日志
   * @param operateInfo
   *     操作时间
   */
  void saveNewLog(String tenant, ExplosiveLogV2 explosiveLogV2, OperateInfo operateInfo);

  /**
   * 查询门店汇总信息
   *
   * @param tenant
   *     租户
   * @param orgId
   *     组织ID
   * @param qd
   *     自定义查询条件
   * @return 返回结果
   */
  QueryResult<ExplosiveReportSummary> queryGroupByShopAndSkuId(String tenant, String orgId, QueryDefinition qd) throws BaasException;

  /**
   * 查询所有门店数据
   *
   * @param tenant
   *     租户
   * @param qd
   *     自定义查询条件
   * @return QueryResult<ExplosiveReportSummary> 统计结果
   */
  QueryResult<ExplosiveReportSummary> queryGroupByDate(String tenant, QueryDefinition qd);

  /**
   * 批量新增爆品活动每日报表
   *
   * @param tenant
   *     租户
   * @param dailyReports
   *     爆品活动每日报表集合
   * @param operateInfo
   *     操作时间
   */
  void batchInsert(String tenant, List<ExplosiveSignV2DailyReport> dailyReports, OperateInfo operateInfo);

  /**
   * 批量更新爆品活动每日报表
   *
   * @param tenant
   *     租户
   * @param dailyReports
   *     爆品活动每日报表集合
   * @param operateInfo
   *     操作时间
   */
  void batchUpdate(String tenant, List<ExplosiveSignV2DailyReport> dailyReports, OperateInfo operateInfo);

  /**
   * 更新活动日志
   *
   * @param tenant
   *     租户
   * @param explosiveLog
   *     活动日志
   * @param operateInfo
   *     操作时间
   */
  void updateLog(String tenant, ExplosiveLogV2 explosiveLog, OperateInfo operateInfo);

  /**
   * 修改报表商品限量数据
   *
   * @param tenant
   *     租户
   * @param explosiveV2
   *     爆品数据
   * @param lines
   *     修改商品行数据
   * @param operateInfo
   *     操作时间
   */
  void updateLimitQty(String tenant, ExplosiveV2 explosiveV2, List<ExplosiveV2Line> lines, OperateInfo operateInfo);
}
