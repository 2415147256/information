package com.hd123.baas.sop.evcall.exector.feedback;

import com.hd123.baas.sop.service.api.feedback.FeedbackRejection;
import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yu lilin on 2020/11/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackRejectMsg extends AbstractTenantEvCallMessage {
  private static final long serialVersionUID = 8660254018575329821L;
  /**
   * APP标识
   */
  private String appId;
  /**
   * 指定反馈拒绝对象
   */
  private FeedbackRejection rejection;
}
