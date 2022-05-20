package com.hd123.baas.sop.evcall.exector.feedback;

import com.hd123.baas.sop.service.api.feedback.FeedbackApproval;
import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yu lilin on 2020/11/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackApprovalMsg extends AbstractTenantEvCallMessage {
  private static final long serialVersionUID = -3889835845986066657L;
  /**
   * APP标识
   */
  private String appId;
  /**
   * 指定反馈同意对象
   */
  private FeedbackApproval approval;
}
