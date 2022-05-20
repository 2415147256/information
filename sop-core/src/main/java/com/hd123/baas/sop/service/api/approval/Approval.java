package com.hd123.baas.sop.service.api.approval;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class Approval {
  /** 审批单单 */
  private String spNo;
  /** 审批单单 */
  private ApprovalState state;
}
