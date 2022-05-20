package com.hd123.baas.sop.evcall.exector.feedback;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class FeedbackSaveApplyReasonEvCallExecutor extends AbstractEvCallExecutor<FeedbackSaveApplyReasonMsg> {
  public static final String FEEDBACK_SAVE_APPLY_REASON_EXECUTOR_ID = FeedbackSaveApplyReasonEvCallExecutor.class
      .getSimpleName();
  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  protected void doExecute(FeedbackSaveApplyReasonMsg message, EvCallExecutionContext context) throws Exception {
    String tenant = message.getTenant();
    List<String> reasons = message.getReasons();

    BaasResponse<Void> response = feignClientMgr.getClient(tenant, null, RsSOSClient.class)
        .feedbackSaveApplyReason(tenant, reasons);
    if (!response.isSuccess()) {
      throw new Exception(response.getMsg());
    }
  }

  @Override
  protected FeedbackSaveApplyReasonMsg decodeMessage(String msg) throws BaasException {
    log.info("收到FeedbackSaveApplyReason:{}", msg);
    return BaasJSONUtil.safeToObject(msg, FeedbackSaveApplyReasonMsg.class);
  }
}
