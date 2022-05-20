package com.hd123.baas.sop.service.api.sku.publishplan;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * 商品上下架服务
 *
 * @author liuhaoxin
 * @since 2021-11-24
 */
public interface SkuPublishPlanService {
  /**
   * 保存
   * 
   * @param tenant
   *          租户
   * @param skuPublishPlan
   *          商品上下架计划
   * @param operateInfo
   *          操作时间
   * @return String 唯一值
   */
  String saveNew(String tenant, SkuPublishPlan skuPublishPlan, OperateInfo operateInfo) throws BaasException;

  /**
   * 更新
   * 
   * @param tenant
   *          租户
   * @param skuPublishPlan
   *          商品上下架
   * @param operateInfo
   *          操作时间
   * @return String 唯一值
   * @throws BaasException
   *           更新失败异常
   */
  String saveModify(String tenant, SkuPublishPlan skuPublishPlan, OperateInfo operateInfo) throws BaasException;

  /**
   * 上架
   *
   * @param tenant
   *          租户
   * @param uuid
   *          商品上下架id
   * @param operateInfo
   *          操作时间
   * @throws BaasException
   *           商品上架异常
   */
  void on(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 下架
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          用户id
   * @param operateInfo
   *          操作时间
   * @throws BaasException
   *           商品下架异常
   */
  void off(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 下架
   *
   * @param tenant
   *     租户
   * @param uuid
   *     用户id
   * @param operateInfo
   *     操作时间
   * @throws BaasException
   *     商品下架异常
   */
  void off(String tenant, String uuid, boolean push, OperateInfo operateInfo) throws BaasException;

  /**
   * 保存并上架
   *
   * @param tenant
   *          租户
   * @param uuid
   *          商品上下架计划id
   * @param skuPublishPlan
   *          商品上下架计划
   * @param operateInfo
   *          操作时间
   * @throws BaasException
   *           保存并上架失败异常
   */
  void saveAndOn(String tenant, String uuid, SkuPublishPlan skuPublishPlan, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 刷新商品行信息
   *
   * @param tenant
   *          租户
   * @param uuid
   *          商品上下架计划id
   */
  void refreshLines(String tenant, String uuid) throws BaasException;

  /**
   * 删除
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          商品上下架计划id
   * @throws BaasException
   *           删除异常
   */
  void remove(String tenant, String uuid) throws BaasException;

  /**
   * 查询详情
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          商品上下架计划id
   * @param fetchParts
   *          分片
   * @return SkuPublishPlan 上下架详情信息
   */
  SkuPublishPlan get(String tenant, String uuid, String... fetchParts);

  /**
   * 查询
   * 
   * @param tenant
   *          租户
   * @param qd
   *          自定义查询
   * @param fetchParts
   * @return QueryResult<SkuPublishPlan> 自定义查询结果
   */
  QueryResult<SkuPublishPlan> query(String tenant, QueryDefinition qd, String... fetchParts);

  /**
   * 查询
   *
   * @param tenant
   *          租户
   * @param qd
   *          自定义查询
   * @return QueryResult<SkuPublishPlanLine> 上下架商品行信息
   */
  QueryResult<SkuPublishPlanLine> queryLines(String tenant, QueryDefinition qd);

  /**
   * 查询上下架通过状态
   *
   * @param tenant
   *          租户
   * @param state
   *          上下架状态
   * @return List<SkuPublishPlan> 上下架列表
   */
  List<SkuPublishPlan> listByStates(String tenant, SkuPublishPlanState... state);

  /**
   * 自动下架
   *
   * @param tenant
   *          租户
   * @param uuids
   *          上下架方案集合
   * @param sysOperateInfo
   *          系统更新时间
   * @throws BaasException
   *           下架异常
   */
  void expire(String tenant, List<String> uuids, OperateInfo sysOperateInfo) throws BaasException;

  /**
   * 上架组织/门店检测
   *
   * @param tenant
   *     租户
   * @param orgId
   *     组织id
   * @param scopes
   *     组织/门店范围
   * @return 冲突组织信息
   */
  List<SkuPublishPlanScope> checkScopes(String tenant, String orgId, List<SkuPublishPlanScope> scopes) throws BaasException;

  /**
   * 上架商品检测
   *
   * @param tenant
   *     租户
   * @param lines
   *     商品行信息
   * @return 冲突商品行信息
   */
  List<SkuPublishPlanLine> checkLines(String tenant, List<SkuPublishPlanLine> lines);
}
