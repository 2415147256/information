package com.hd123.baas.sop.evcall.exector.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.feedback.FeedbackApproval;
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
public class FeedbackApprovalEvCallExecutor extends AbstractEvCallExecutor<FeedbackApprovalMsg> {
  public static final String FEEDBACK_APPROVAL_EXECUTOR_ID = FeedbackApprovalEvCallExecutor.class.getSimpleName();
  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  protected void doExecute(FeedbackApprovalMsg message, EvCallExecutionContext context) throws Exception {
    String tenant = message.getTenant();
    FeedbackApproval approval = message.getApproval();

    BaasResponse<Void> response = feignClientMgr.getClient(tenant,null, RsSOSClient.class)
        .feedbackAudit(tenant, approval.getShop(), message.getAppId(), approval);
    if (!response.isSuccess()) {
      throw new Exception(response.getMsg());
    }
  }

  @Override
  protected FeedbackApprovalMsg decodeMessage(String msg) throws BaasException {
    log.info("收到FeedbackApprovalMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, FeedbackApprovalMsg.class);
  }

}
