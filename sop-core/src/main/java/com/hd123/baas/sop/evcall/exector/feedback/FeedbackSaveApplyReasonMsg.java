package com.hd123.baas.sop.evcall.exector.feedback;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author yu lilin on 2020/11/20
 */
@Getter
@Setter
public class FeedbackSaveApplyReasonMsg extends AbstractTenantEvCallMessage {
    private List<String> reasons;
}
