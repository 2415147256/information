package com.hd123.baas.sop.evcall.exector.pricescreen;

import com.hd123.baas.sop.configuration.FeignClientMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.screen.PriceScreen;
import com.hd123.baas.sop.service.api.screen.PriceScreenService;
import com.hd123.baas.sop.service.api.screen.PriceScreenState;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.screen.MkhScreenClient;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author maodapeng
 * @Since
 */
@Slf4j
@Component
public class PriceScreenTerminateEvCallExecutor extends AbstractEvCallExecutor<PriceScreenTerminateMsg> {

  public static final String PRICE_SCREEN_TERMINATE_EXECUTOR_ID = PriceScreenTerminateEvCallExecutor.class
      .getSimpleName();
  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private PriceScreenService priceScreenService;

  @Override
  protected void doExecute(PriceScreenTerminateMsg msg, EvCallExecutionContext context) throws Exception {
    Assert.notNull(msg.getTenant(), "tenant");
    Assert.notNull(msg.getUuid(), "uuid");
    PriceScreen priceScreen = priceScreenService.get(msg.getTenant(), msg.getUuid());
    if (priceScreen == null) {
      log.error("价格屏方案<{}>不存在", priceScreen.getUuid());
      return;
    }
    if (priceScreen.getState() != PriceScreenState.TERMINATED) {
      throw new BaasException("价格屏方案状态不是TERMINATED，当前状态为：{}", priceScreen.getState());
    }
    MkhScreenClient mkhScreenClient = feignClientMgr.getClient(msg.getTenant(), null, MkhScreenClient.class);
    BaasResponse<Void> response = mkhScreenClient.stopScheme(msg.getTenant(), msg.getUuid());
    if (!response.isSuccess()) {
      throw new BaasException(response.getCode(), response.getMsg());
    }
  }

  @Override
  protected PriceScreenTerminateMsg decodeMessage(String msg) throws BaasException {
    log.info("收到PriceScreenTerminateMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, PriceScreenTerminateMsg.class);
  }
}
