package com.hd123.baas.sop.evcall.exector.price;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotion;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionService;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionState;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.utils.BaasJSONUtil;
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
public class PricePromotionTerminateEvCallExecutor extends AbstractEvCallExecutor<PricePromotionTerminateMsg> {
  public static final String PRICE_PROMOTION_TERMINATE_EXECUTOR_ID = PricePromotionTerminateEvCallExecutor.class
      .getSimpleName();
  @Autowired
  private PricePromotionService pricePromotionService;
  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  protected void doExecute(PricePromotionTerminateMsg msg, EvCallExecutionContext context) throws Exception {
    PricePromotion pricePromotion = pricePromotionService.get(msg.getTenant(), msg.getUuid());
    if (pricePromotion.getState() != PricePromotionState.TERMINATE) {
      throw new BaasException("促销单未终止");
    }
    try {
      RsH6SOPClient h6SOPClient = feignClientMgr.getClient(msg.getTenant(), null, RsH6SOPClient.class);
      BaasResponse<Void> response = h6SOPClient.billAbort(msg.getTenant(), pricePromotion.getUuid());
      if (!response.isSuccess()) {
        throw new BaasException("终止促销单失败，msg:{}", response.getMsg());
      }
    } catch (Exception e) {
      log.error("PricePromotionTerminateEvCallExecutor错误", e);
      throw e;
    }
  }

  @Override
  protected PricePromotionTerminateMsg decodeMessage(String msg) throws BaasException {
    log.info("收到PricePromotionTerminateMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, PricePromotionTerminateMsg.class);
  }
}
