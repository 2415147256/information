package com.hd123.baas.sop.service.impl.approval;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WorkWxApprovalRecord {
  // 审批节点状态：1-审批中；2-已同意；3-已驳回；4-已转审；11-已退回；12-已加签；13-已同意并加签
  private int status;
  // value = "节点审批方式：1-或签；2-会签"
  private int approverattr;
  // value = "审批节点详情,一个审批节点有多个审批人"
  private List<RecordDetail> details = new ArrayList<>();
}
