package com.hd123.baas.sop.service.api.sku.publishplan;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * @author liuhaoxin
 * @since 2021年12月30日
 */
public interface SkuPublishPlanBuilder {

  /**
   * 自动创建构建策略
   *
   * @param tenant
   *     租户
   * @param uuid
   *     方案id
   */
  List<SkuPublishPlan> build(String tenant, String uuid) throws BaasException;

  /**
   * 下架策略
   *
   * @param tenant
   *     租户
   * @param uuid
   *     方案ID
   * @param operateInfo 操作信息
   * @return 返回参数
   */
  void off(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;
}
