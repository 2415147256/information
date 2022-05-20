package com.hd123.baas.sop.evcall.exector.subsidyplan;

import com.hd123.baas.sop.annotation.Tx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.subsidyplan.StorePromPlan;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuhaoxin
 */
@Component
@Slf4j
public class PlanPushEvCallExecutor extends AbstractEvCallExecutor<PlanPushEvCallMsg> {
  public static final String PLAN_PUSH_EXECUTOR_ID = PlanPushEvCallExecutor.class.getSimpleName();

  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  @Tx
  protected void doExecute(PlanPushEvCallMsg message, EvCallExecutionContext context) throws Exception {
    log.info("SOP->H6:补贴计划{}推送中...", message.getPlanName());
    StorePromPlan storePromPlan = buildStorePromPlan(message);
    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(message.getTenant(), null, RsH6SOPClient.class);
    rsH6SOPClient.promPlanSave(message.getTenant(), storePromPlan);
    log.info("SOP->H6:补贴计划{}推送成功", message.getPlanId());

  }

  @Override
  protected PlanPushEvCallMsg decodeMessage(String arg) throws BaasException {
    return JsonUtil.jsonToObject(arg, PlanPushEvCallMsg.class);
  }

  private StorePromPlan buildStorePromPlan(PlanPushEvCallMsg message) {
    StorePromPlan storePromPlan = new StorePromPlan();
    storePromPlan.setPlanId(message.getPlanId());
    storePromPlan.setPlanName(message.getPlanName());
    storePromPlan.setStoreGid(message.getStoreGid());
    storePromPlan.setState(message.getState());
    storePromPlan.setAmount(message.getAmount());
    storePromPlan.setEffectiveStartTime(message.getEffectiveStartTime());
    storePromPlan.setEffectiveEndTime(message.getEffectiveEndTime());
    return storePromPlan;
  }
}
