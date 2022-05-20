package com.hd123.baas.sop.evcall.exector.feedback;

import com.hd123.baas.sop.service.api.feedback.Feedback;
import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yu lilin on 2020/11/25
 */
@Getter
@Setter
public class FeedbackPushMsg extends AbstractTenantEvCallMessage {
    /**
     * 指定质量反馈单
     */
    private Feedback feedback;
}
