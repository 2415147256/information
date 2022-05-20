package com.hd123.baas.sop.service.api.sku.publishplan;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * @author liuhaoxin
 * @since 上下架策略选择器
 */
public class SkuPublishPlanBuilderService {
  /** 上下架策略服务 */
  private SkuPublishPlanBuilder builder;

  public SkuPublishPlanBuilderService(SkuPublishPlanBuilder builder) {
    this.builder = builder;
  }

  /**
   * 构建策略执行器
   *
   * @param tenant
   *     租户
   * @param uuid
   *     方案ID
   * @return 需要创建的上下级方案
   */
  public List<SkuPublishPlan> executeBuilder(String tenant, String uuid) throws BaasException {
    return builder.build(tenant, uuid);
  }

  /**
   * 下架策略执行器
   *
   * @param tenant
   *     租户
   * @param uuid
   *     方案ID
   * @return 需要创建的上下级方案
   */
  public void executeOff(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    builder.off(tenant, uuid, operateInfo);
  }
}
