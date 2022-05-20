package com.hd123.baas.sop.evcall.exector.feedback;

import com.hd123.baas.sop.utils.ApplicationContextUtils;
import com.hd123.baas.sop.service.api.feedback.Feedback;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.feedback.FeedbackToRsH6Converter;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yu lilin on 2020/11/25
 */
@Slf4j
@Component
public class FeedbackPushEvCallExecutor extends AbstractEvCallExecutor<FeedbackPushMsg> {
  public static final String FEEDBACK_PUSH_EXECUTOR_ID = FeedbackPushEvCallExecutor.class
    .getSimpleName();

  @Override
  protected void doExecute(FeedbackPushMsg message, EvCallExecutionContext context)
    throws Exception {
    String tenant = message.getTenant();
    Feedback feedback = message.getFeedback();

    BaasResponse<Void> response = getClient(tenant).feedbackAccept(tenant, new FeedbackToRsH6Converter().convert(feedback));
    if (!response.isSuccess()) {
      throw new Exception(response.getMsg());
    }
  }

  @Override
  protected FeedbackPushMsg decodeMessage(String msg) throws BaasException {
    log.info("收到FeedbackPush:{}", msg);
    return BaasJSONUtil.safeToObject(msg, FeedbackPushMsg.class);
  }

  private RsH6SOPClient getClient(String tenant) throws BaasException {
    return getClientMgr().getClient(tenant, null, RsH6SOPClient.class);
  }

  private FeignClientMgr getClientMgr() {
    return ApplicationContextUtils.getBean(FeignClientMgr.class);
  }

}
