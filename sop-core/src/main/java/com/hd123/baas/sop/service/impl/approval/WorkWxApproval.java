package com.hd123.baas.sop.service.impl.approval;

import com.hd123.baas.sop.service.api.approval.Approval;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class WorkWxApproval extends Approval {
  // 审批流程信息，可能有多个审批节点。
  private List<WorkWxApprovalRecord> records = new ArrayList<>();
}
