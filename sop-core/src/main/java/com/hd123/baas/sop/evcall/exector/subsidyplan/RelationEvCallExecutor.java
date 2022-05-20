package com.hd123.baas.sop.evcall.exector.subsidyplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.subsidyplan.StorePromPlanRelate;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuhaoxin
 */
@Component
@Slf4j
public class RelationEvCallExecutor extends AbstractEvCallExecutor<RelationEvCallMsg> {
  public static final String RELATION_EXECUTOR_ID = RelationEvCallExecutor.class.getSimpleName();

  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  @Tx
  protected void doExecute(RelationEvCallMsg message, EvCallExecutionContext context) throws Exception {
    // 推送一条关联
    StorePromPlanRelate relate = new StorePromPlanRelate();
    relate.setPlanId(message.getPlanId());
    relate.setPromType(message.getActivityType().name());
    relate.setPromId(message.getActivityId());
    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(message.getTenant(), null, RsH6SOPClient.class);
    BaasResponse<Void> response = rsH6SOPClient.uploadOne(message.getTenant(), relate);
    if (!response.isSuccess()) {
      throw new BaasException("H6关联活动下发失败");
    }
  }

  @Override
  protected RelationEvCallMsg decodeMessage(String arg) throws BaasException {
    log.info("推送关联关系");
    return JsonUtil.jsonToObject(arg, RelationEvCallMsg.class);
  }
}
