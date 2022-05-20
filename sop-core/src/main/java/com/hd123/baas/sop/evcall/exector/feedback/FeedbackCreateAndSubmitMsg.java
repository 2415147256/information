package com.hd123.baas.sop.evcall.exector.feedback;

import com.hd123.baas.sop.service.api.feedback.FeedbackCreation;
import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yu lilin on 202/11/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackCreateAndSubmitMsg extends AbstractTenantEvCallMessage {
  private static final long serialVersionUID = -5444613784421519175L;
  /**
   * APP标识
   */
  private String appId;

  /**
   * 指定创建并提交对象
   */
  private FeedbackCreation creation;
}
