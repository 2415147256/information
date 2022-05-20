package com.hd123.baas.sop.evcall.exector.shoptask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.task.ShopTaskSummaryService;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author maodapeng
 * @Since
 */
@Slf4j
@Component
public class ShopTaskSummaryEvCallExecutor extends AbstractEvCallExecutor<ShopTaskSummaryMsg> {

  public static final String SHOP_TASK_SUMMARY_EXECUTOR_ID = ShopTaskSummaryEvCallExecutor.class.getSimpleName();

  @Autowired
  private ShopTaskSummaryService shopTaskSummaryService;

  @Override
  protected void doExecute(ShopTaskSummaryMsg msg, EvCallExecutionContext context) throws Exception {
    String uuid = msg.getUuid();
    String tenant = msg.getTenant();
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    shopTaskSummaryService.summary(tenant, uuid);
  }

  @Override
  protected ShopTaskSummaryMsg decodeMessage(String msg) throws BaasException {
    log.info("收到ShopTaskSummaryMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ShopTaskSummaryMsg.class);
  }
}
