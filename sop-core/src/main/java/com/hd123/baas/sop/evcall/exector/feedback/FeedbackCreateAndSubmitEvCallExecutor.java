package com.hd123.baas.sop.evcall.exector.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.feedback.FeedbackCreation;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rssos.RsSOSClient;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yu lilin on 2020/11/20
 */
@Slf4j
@Component
public class FeedbackCreateAndSubmitEvCallExecutor extends AbstractEvCallExecutor<FeedbackCreateAndSubmitMsg> {
  public static final String FEEDBACK_CREATE_AND_SUBMIT_EXECUTOR_ID = FeedbackCreateAndSubmitEvCallExecutor.class
      .getSimpleName();
  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  protected void doExecute(FeedbackCreateAndSubmitMsg message, EvCallExecutionContext context) throws Exception {
    String tenant = message.getTenant();
    FeedbackCreation creation = message.getCreation();

    BaasResponse<String> response = feignClientMgr.getClient(tenant,null, RsSOSClient.class)
        .feedbackCreateAndSubmit(tenant, creation.getShop(), creation.getShopNo(), creation.getShopName(),
            message.getAppId(), creation);
    if (!response.isSuccess()) {
      throw new Exception(response.getMsg());
    }
  }

  @Override
  protected FeedbackCreateAndSubmitMsg decodeMessage(String msg) throws BaasException {
    log.info("收到FeedbackCreateAndSubmitMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, FeedbackCreateAndSubmitMsg.class);
  }

}
