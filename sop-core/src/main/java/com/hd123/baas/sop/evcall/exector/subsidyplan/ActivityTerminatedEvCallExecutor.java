package com.hd123.baas.sop.evcall.exector.subsidyplan;

import com.hd123.baas.sop.remote.rsmkhpms.entity.BOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotion;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionService;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionState;
import com.hd123.baas.sop.service.api.subsidyplan.ActivityType;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
import com.hd123.baas.sop.service.dao.activity.PromActivityDao;
import com.hd123.baas.sop.service.api.activity.PromActivityService;
import com.hd123.baas.sop.remote.rsmkhpms.RsMkhPmsClient;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BBasePricePromRes;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BStateReason;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BasePricePromotionState;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuhaoxin
 */
@Component
@Slf4j
public class ActivityTerminatedEvCallExecutor extends AbstractEvCallExecutor<ActivityTerminatedEvCallMsg> {
  public static final String ACTIVITY_TERMINATED_EXECUTOR_ID = ActivityTerminatedEvCallExecutor.class.getSimpleName();

  @Autowired
  private PromActivityService promActivityService;
  @Autowired
  private PricePromotionService promotionService;
  @Autowired
  private RsMkhPmsClient rsMkhPmsClient;
  @Autowired
  private PromActivityDao promActivityDao;


  @Override
  @Tx
  protected void doExecute(ActivityTerminatedEvCallMsg message, EvCallExecutionContext context) throws Exception {
    log.info("终止补贴计划活动");
    OperateInfo operateInfo = message.getOperateInfo();
    if (ActivityType.PRICE_PROMOTION == message.getActivityType()) {
      log.info("价格促销终止");
      PricePromotion pricePromotion = promotionService.get(message.getTenant(), message.getActivityId());
      if (PricePromotionState.PUBLISHED.equals(pricePromotion.getState())
          || PricePromotionState.AUDITED.equals(pricePromotion.getState())) {
        promotionService.terminate(message.getTenant(), message.getActivityId(),"补贴计划终止", SopUtils.getSysOperateInfo());
      } else if (PricePromotionState.CONFIRMED.equals(pricePromotion.getState())) {
        promotionService.cancel(message.getTenant(), message.getActivityId(), "补贴计划作废", SopUtils.getSysOperateInfo());
      }
      log.info("价格促销：状态={},不需要终止", pricePromotion.getState().toString());
      return;
    }
    if (ActivityType.PRICE_PROMOTION_MODEL.equals(message.getActivityType())) {
      log.info("价格促销模型终止");
      BStateReason stateReason = new BStateReason();
      stateReason.setUuid(message.getActivityId());
      BOperator operator = new BOperator();
      operator.setLastModified(operateInfo.getTime());
      operator.setLastModifierId(operateInfo.getOperator().getId());
      operator.setLastModifierName(operateInfo.getOperator().getFullName());
      stateReason.setOperator(operator);
      BaasResponse<BBasePricePromRes> response = rsMkhPmsClient.get(message.getTenant(), message.getActivityId());
      BBasePricePromRes pricePromRes = response.getData();

      if (BasePricePromotionState.EFFECT.name().equals(pricePromRes.getState())
          || BasePricePromotionState.AUDITED.name().equals(pricePromRes.getState())) {
        stateReason.setReason("补贴计划终止");
        rsMkhPmsClient.terminate(message.getTenant(), stateReason);
      } else if (BasePricePromotionState.SUBMITTED.name().equals(pricePromRes.getState())) {
        stateReason.setReason("补贴计划作废");
        rsMkhPmsClient.canceled(message.getTenant(), stateReason);
      }
      log.info("价格促销模型：状态={},不需要终止", pricePromRes.getState());
      return;
    }
    if (ActivityType.PROMOTE_ACTIVITY.equals(message.getActivityType())) {
      log.info("营销活动终止");
      PromActivity promActivity = promActivityDao.get(message.getTenant(), message.getActivityId());

      if (PromActivity.State.audited.equals(promActivity.getState())) {
        if (PromActivity.FRONT_STATE_EFFECT.equals(promActivity.getFrontState())) {
          promActivityService.stopped(message.getTenant(), message.getActivityId(), SopUtils.getSysOperateInfo());
        } else if (PromActivity.State.audited.name().equals(promActivity.getFrontState())) {
          promActivityService.cancel(message.getTenant(), message.getActivityId(), SopUtils.getSysOperateInfo());
        }
        log.info("营销活动：状态={},不需要终止", promActivity.getFrontState());
      }
    }
  }

  @Override
  protected ActivityTerminatedEvCallMsg decodeMessage(String arg) throws BaasException {
    return JsonUtil.jsonToObject(arg, ActivityTerminatedEvCallMsg.class);
  }
}
