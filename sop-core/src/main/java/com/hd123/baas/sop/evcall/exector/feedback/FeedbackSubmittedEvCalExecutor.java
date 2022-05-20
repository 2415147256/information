package com.hd123.baas.sop.evcall.exector.feedback;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.FeedbackConfig;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.service.api.feedback.Feedback;
import com.hd123.baas.sop.service.api.feedback.FeedbackService;
import com.hd123.baas.sop.service.api.feedback.FeedbackState;
import com.hd123.baas.sop.service.impl.approval.FeedbackApprovalByWorkWxService;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeedbackSubmittedEvCalExecutor extends AbstractEvCallExecutor<FeedbackSubmittedMsg> {
  public static final String FEEDBACK_SUBMITTED_EXECUTOR_ID = FeedbackSubmittedEvCalExecutor.class.getSimpleName();

  @Autowired
  private FeedbackService feedbackService;
  @Autowired
  private FeedbackApprovalByWorkWxService approvalService;
  @Autowired
  private BaasConfigClient configClient;

  @Override
  protected void doExecute(FeedbackSubmittedMsg message, EvCallExecutionContext context) throws Exception {
    Assert.notNull(message);
    Assert.notNull(message.getBillId(), "billId");
    Assert.notNull(message.getTenant(), "tenant");

    String tenant = message.getTenant();
    String billId = message.getBillId();
    // 获取详情
    Feedback feedback = feedbackService.get(billId);
    if (null == feedback) {
      log.warn("未查询到质量反馈单,忽略,billId={}", billId);
      return;
    }
    // 检查策略
    if (!FeedbackConfig.WORK_WX_POLICY.equals(feedback.getCheckPolicy())) {
      log.info("当前质量单反馈无需审批,billId={},checkPolicy={}", billId, feedback.getCheckPolicy());
      return;
    }
    // 检查状态
    if (!FeedbackState.submitted.equals(feedback.getState())) {
      log.info("质量反馈单前置状态错误,billId={},state={}", billId, feedback.getState());
      return;
    }

    if (FeedbackState.checked.equals(feedback.getState())) {
      log.info("质量反馈单已确认,billId={}", billId);
      return;
    }
    // 读取状态
    if (FeedbackConfig.WORK_WX_POLICY.equals(feedback.getCheckPolicy())) {
      String spNo = approvalService.submit(tenant, feedback);
      feedback.setSpNo(spNo);
      feedbackService.addSpNo(tenant, feedback.getBillId(), spNo);
    }
  }

  @Override
  protected FeedbackSubmittedMsg decodeMessage(String msg) throws BaasException {
    log.info("收到FeedbackSubmittedMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, FeedbackSubmittedMsg.class);
  }

}
