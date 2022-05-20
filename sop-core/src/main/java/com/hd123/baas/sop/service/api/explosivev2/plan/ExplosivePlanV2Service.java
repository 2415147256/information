package com.hd123.baas.sop.service.api.explosivev2.plan;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author shenmin
 */
public interface ExplosivePlanV2Service {
  /**
   * 新增爆品计划
   *
   * @param tenant          租户
   * @param explosivePlanV2 爆品计划
   * @param operateInfo     操作信息
   * @return 爆品计划uuid
   */
  String saveNew(String tenant, ExplosivePlanV2 explosivePlanV2, OperateInfo operateInfo) throws BaasException;

  /**
   * 保存爆品计划
   *
   * @param tenant          租户
   * @param explosivePlanV2 爆品计划
   * @param operateInfo     操作信息
   * @return 爆品计划uuid
   * @throws BaasException
   */
  String saveModify(String tenant, ExplosivePlanV2 explosivePlanV2, OperateInfo operateInfo) throws BaasException;

  /**
   * 新增并启用爆品计划
   *
   * @param tenant          租户
   * @param explosivePlanV2 爆品计划
   * @param operateInfo     操作信息
   * @throws BaasException
   * @return 爆品计划uuid
   */
  String saveNewAndEnable(String tenant, ExplosivePlanV2 explosivePlanV2, OperateInfo operateInfo) throws BaasException;

  /**
   * 保存并启用爆品计划
   *
   * @param tenant          租户
   * @param explosivePlanV2 爆品计划
   * @param operateInfo     操作信息
   * @throws BaasException
   * @return 爆品计划uuid
   */
  String saveModifyAndEnable(String tenant, ExplosivePlanV2 explosivePlanV2, OperateInfo operateInfo) throws BaasException;

  /**
   * 获取爆品计划
   *
   * @param tenant     租户
   * @param uuid       爆品计划
   * @param fetchParts 级联查询信息
   * @return 爆品计划
   */
  ExplosivePlanV2 get(String tenant, String uuid, String... fetchParts);

  /**
   * 启用爆品计划
   *
   * @param tenant 租户
   * @param uuid   爆品计划uuid
   * @param operateInfo
   * @throws BaasException
   */
  void enable(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 停用爆品计划
   *
   * @param tenant 租户
   * @param uuid   爆品计划uuid
   * @param operateInfo
   * @throws BaasException
   */
  void disable(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 删除爆品计划
   *
   * @param tenant 租户
   * @param uuid   爆品计划uuid
   * @throws BaasException
   */
  void delete(String tenant, String uuid) throws BaasException;

  /**
   * 条件查询爆品计划
   *
   * @param tenant 租户
   * @param qd     查询条件
   * @return 爆品计划结果集合
   */
  QueryResult<ExplosivePlanV2> query(String tenant, QueryDefinition qd);
}
