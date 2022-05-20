package com.hd123.baas.sop.evcall.exector.feedback;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import com.hd123.baas.sop.service.api.feedback.Feedback;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yu lilin on 2020/11/23
 */
@Getter
@Setter
public class FeedbackSubmittedMsg extends AbstractTenantEvCallMessage {
  /**
   * 指定反馈单数据标识
   */
  private String billId;
}
