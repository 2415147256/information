package com.hd123.baas.sop.service.api.explosivev2;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * @author shenmin
 */
public interface ExplosiveV2Service {
  /**
   * 新增爆品活动
   *
   * @param tenant
   *     租户
   * @param explosiveV2
   *     爆品活动
   * @param operateInfo
   *     操作信息
   * @return 爆品活动uuid
   */
  String saveNew(String tenant, ExplosiveV2 explosiveV2, OperateInfo operateInfo) throws BaasException;

  /**
   * 修改爆品活动
   *
   * @param tenant
   *     租户
   * @param explosiveV2
   *     爆品活动
   * @param operateInfo
   *     操作信息
   * @return 爆品活动uuid
   */
  String saveModify(String tenant, ExplosiveV2 explosiveV2, OperateInfo operateInfo) throws BaasException;

  /**
   * 新增并启用爆品活动
   *
   * @param tenant
   *     租户
   * @param explosiveV2
   *     爆品活动
   * @param operateInfo
   *     操作信息
   * @return 爆品活动uuid
   */
  String saveNewAndSubmit(String tenant, ExplosiveV2 explosiveV2, OperateInfo operateInfo) throws BaasException;

  /**
   * 修改并启用爆品活动
   *
   * @param tenant
   *     租户
   * @param explosiveV2
   *     爆品活动
   * @param operateInfo
   *     操作信息
   * @return 爆品活动uuid
   */
  String saveModifyAndSubmit(String tenant, ExplosiveV2 explosiveV2, OperateInfo operateInfo) throws BaasException;

  /**
   * 提交爆品活动
   *
   * @param tenant
   *     租户
   * @param uuid
   *     爆品活动uuid
   * @param operateInfo
   *     操作信息
   */
  void submit(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 审核爆品活动
   *
   * @param tenant
   *     租户
   * @param uuid
   *     爆品活动uuid
   * @param operateInfo
   *     操作信息
   */
  void audit(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 驳回爆品活动
   *
   * @param tenant
   *     租户
   * @param uuid
   *     爆品活动uuid
   * @param operateInfo
   *     操作信息
   */
  void refuse(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 上架爆品活动
   *
   * @param tenant
   *     租户
   * @param uuid
   *     爆品活动uuid
   * @param operateInfo
   *     操作信息
   */
  void on(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 下架爆品活动
   *
   * @param tenant
   *     租户
   * @param uuid
   *     爆品活动uuid
   * @param operateInfo
   *     操作信息
   */
  void off(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 删除爆品活动
   *
   * @param tenant
   *     租户
   * @param uuid
   *     爆品uuid
   */
  void delete(String tenant, String uuid) throws BaasException;

  /**
   * 修改爆品活动行已订货量
   *
   * @param tenant
   *     租户
   * @param uuid
   *     爆品活动ID
   * @param lineLimitIncrInfos
   *     修改商品已订货量信息集合
   */
  void incrLineLimit(String tenant, String uuid, List<LineLimitIncrInfo> lineLimitIncrInfos) throws BaasException;

  /**
   * 修改爆品行限量信息
   */
  void saveLimitQty(String tenant, String uuid, List<ExplosiveV2Line> lines, List<String> explosiveSignIds, OperateInfo operateInfo) throws BaasException;

  /**
   * 条件查询爆品活动
   *
   * @param tenant
   *     租户
   * @param qd
   *     查询条件
   * @param fetchParts
   *     级联查询信息
   */
  QueryResult<ExplosiveV2> query(String tenant, QueryDefinition qd, String... fetchParts);

  /**
   * 获取爆品活动详情
   */
  ExplosiveV2 get(String tenant, String uuid, boolean forUpdate, String... fetchParts);

  /**
   * 获取爆品活动详情
   */
  List<ExplosiveV2> list(String tenant, List<String> uuids);

}
