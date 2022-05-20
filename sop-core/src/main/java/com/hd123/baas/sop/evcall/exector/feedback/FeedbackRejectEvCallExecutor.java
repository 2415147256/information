package com.hd123.baas.sop.evcall.exector.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.feedback.FeedbackRejection;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rssos.RsSOSClient;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yu lilin on 2020/11/19
 */
@Slf4j
@Component
public class FeedbackRejectEvCallExecutor extends AbstractEvCallExecutor<FeedbackRejectMsg> {
  public static final String FEEDBACK_REJECT_EXECUTOR_ID = FeedbackRejectEvCallExecutor.class.getSimpleName();
  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  protected void doExecute(FeedbackRejectMsg message, EvCallExecutionContext context) throws Exception {
    String tenant = message.getTenant();
    FeedbackRejection rejection = message.getRejection();

    BaasResponse<Void> response = feignClientMgr.getClient(tenant,null, RsSOSClient.class)
        .feedbackReject(tenant, rejection.getShop(), message.getAppId(), rejection);
    if (!response.isSuccess()) {
      throw new Exception(response.getMsg());
    }
  }

  @Override
  protected FeedbackRejectMsg decodeMessage(String msg) throws BaasException {
    log.info("收到FeedbackRejectMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, FeedbackRejectMsg.class);
  }
}
