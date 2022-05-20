package com.hd123.baas.sop.evcall.exector.price;

import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotion;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionService;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionState;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.spms.commons.json.JsonUtil;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Author maodapeng
 * @Since
 */
@Slf4j
@Component
public class PricePromotionAutoAuditEvCallExecutor extends AbstractEvCallExecutor<PricePromotionAutoAuditMsg> {
  public static final String PRICE_PROMOTION_AUTO_AUDIT_EXECUTOR_ID = PricePromotionAutoAuditEvCallExecutor.class
      .getSimpleName();
  @Autowired
  private PricePromotionService pricePromotionService;

  @Override
  protected void doExecute(PricePromotionAutoAuditMsg msg, EvCallExecutionContext context) throws Exception {
    PricePromotion pricePromotion = pricePromotionService.get(msg.getTenant(), msg.getUuid());
    if (pricePromotion.getState() == PricePromotionState.AUDITED) {
      log.info("促销单flowNo={}已审核", pricePromotion.getFlowNo());
      return;
    }
    if (pricePromotion.getHeadSharingRate().compareTo(BigDecimal.ZERO) != 0) {
      log.info("促销单flowNo={}不需要自动审核", pricePromotion.getFlowNo());
      return;
    }
    try {
      pricePromotionService.audit(msg.getTenant(), pricePromotion.getOrgId(), msg.getUuid(), getSysOperateInfo());
    } catch (Exception e) {
      log.info("自动审核促销单flowNo={}失败，失败原因：{}", pricePromotion.getFlowNo(), JsonUtil.objectToJson(e));
    }
  }

  @Override
  protected PricePromotionAutoAuditMsg decodeMessage(String msg) throws BaasException {
    log.info("收到PricePromotionAutoAuditMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, PricePromotionAutoAuditMsg.class);
  }
}
