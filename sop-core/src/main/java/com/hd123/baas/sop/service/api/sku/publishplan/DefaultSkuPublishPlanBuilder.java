package com.hd123.baas.sop.service.api.sku.publishplan;

import com.hd123.rumba.commons.biz.entity.OperateInfo;

import java.util.List;

/**
 * @author liuhaoxin
 * @since 2021年12月30日
 */
public class DefaultSkuPublishPlanBuilder implements SkuPublishPlanBuilder {

  @Override
  public List<SkuPublishPlan> build(String tenant, String uuid) {
    return null;
  }

  @Override
  public void off(String tenant, String uuid, OperateInfo operateInfo) {
  }
}
