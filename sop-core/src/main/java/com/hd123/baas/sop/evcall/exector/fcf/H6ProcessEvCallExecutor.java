package com.hd123.baas.sop.evcall.exector.fcf;

import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.fcf.H6ProcessOrder;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class H6ProcessEvCallExecutor extends AbstractEvCallExecutor<H6ProcessOrderMsg> {
  public static final String PROCESS_EXECUTOR_ID = H6ProcessEvCallExecutor.class.getSimpleName();

  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  protected void doExecute(H6ProcessOrderMsg message, EvCallExecutionContext context) throws Exception {
    H6ProcessOrder h6ProcessOrder = new H6ProcessOrder();
    h6ProcessOrder.getDetails().addAll(message.getDetails());
    h6ProcessOrder.setUuid(message.getUuid());
    h6ProcessOrder.setStoreGid(message.getStoreGid());
    h6ProcessOrder.setOcrTime(message.getOcrTime());
    feignClientMgr.getClient(message.getTenant(), null, RsH6SOPClient.class).uploadFreshFood(message.getTenant(),
        h6ProcessOrder);
  }

  @Override
  protected H6ProcessOrderMsg decodeMessage(String msg) throws BaasException {
    log.info("收到H6ProcessOrderMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, H6ProcessOrderMsg.class);
  }
}
