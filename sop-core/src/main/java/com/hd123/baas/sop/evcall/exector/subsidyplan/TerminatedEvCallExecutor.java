package com.hd123.baas.sop.evcall.exector.subsidyplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
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
public class TerminatedEvCallExecutor extends AbstractEvCallExecutor<TerminatedEvCallMsg> {
  public static final String TERMINATED_EXECUTOR_ID = TerminatedEvCallExecutor.class
      .getSimpleName();

  @Autowired
  private FeignClientMgr feignClientMgr;


  @Override
  @Tx
  protected void doExecute(TerminatedEvCallMsg message, EvCallExecutionContext context) throws Exception {
    log.info("推送h6终止补贴活动:{}", message.getPlanId());
    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(message.getTenant(), null, RsH6SOPClient.class);
    BaasResponse<Void> response = rsH6SOPClient.planabort(message.getTenant(), message.getPlanId());
    if (!response.isSuccess()) {
      throw new BaasException(response.getMsg());
    }
    // todo
  }

  @Override
  protected TerminatedEvCallMsg decodeMessage(String arg) throws BaasException {
    return JsonUtil.jsonToObject(arg, TerminatedEvCallMsg.class);
  }
}
