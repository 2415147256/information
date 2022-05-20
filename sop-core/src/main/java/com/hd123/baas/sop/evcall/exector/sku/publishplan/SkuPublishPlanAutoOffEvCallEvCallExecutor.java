package com.hd123.baas.sop.evcall.exector.sku.publishplan;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.SkuPublishPlanConfig;
import com.hd123.baas.sop.service.api.sku.publishplan.DefaultSkuPublishPlanBuilder;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanBuilder;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanBuilderService;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanService;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上下架方案推送
 *
 * @author liuhaoxin on 2021/11/25.
 */
@Slf4j
@Component
public class SkuPublishPlanAutoOffEvCallEvCallExecutor
    extends AbstractEvCallExecutor<SkuPublishPlanAutoOffEvCallMsg> {

  public static final String SKU_PUBLISH_PLAN_AUTO_OFF_EXECUTOR_ID = SkuPublishPlanAutoOffEvCallEvCallExecutor.class
      .getSimpleName();

  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private SkuPublishPlanService skuPublishPlanService;

  @Override
  @Tx
  protected void doExecute(SkuPublishPlanAutoOffEvCallMsg message, EvCallExecutionContext context) throws Exception {
    log.info("下架自动下架方案");String tenant = message.getTenant();
    String planId = message.getUuid();
    OperateInfo operateInfo = message.getOperateInfo();

     SkuPublishPlanConfig skuPublishPlanConfig = configClient.getConfig(tenant, SkuPublishPlanConfig.class);
    if (!skuPublishPlanConfig.isEnableSaveNew()) {
      log.info("未开启自动创建分公司的上下架方案");
      return;
    }
    SkuPublishPlanBuilder builder = getBuilderBean(skuPublishPlanConfig.getBeanName());
    SkuPublishPlanBuilderService builderService = new SkuPublishPlanBuilderService(builder);
    builderService.executeOff(tenant, planId, operateInfo);
  }

  private SkuPublishPlanBuilder getBuilderBean(String name) {
    if (StringUtils.isBlank(name)) {
      return new DefaultSkuPublishPlanBuilder();
    }
    return getBean(name, SkuPublishPlanBuilder.class);
  }

  @Override
  protected SkuPublishPlanAutoOffEvCallMsg decodeMessage(String msg) throws BaasException {
    log.info("商品下架自动下架推送消息SkuPublishPlanAutoOffEvCallMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, SkuPublishPlanAutoOffEvCallMsg.class);
  }

}
