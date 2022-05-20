package com.hd123.baas.sop.service.impl.approval;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RecordDetail {
  // value = "分支审批人"
  private RecordDetailApprover approver;
  // value = "审批意见"
  private String speech;
  // value = "申请状态"
  private int status;
  // value = "操作时间戳"
  private long time;
  // value = "节点分支审批人审批意见附件"
  private List<String> mediaIds = new ArrayList<>();

  @Getter
  @Setter
  public static class RecordDetailApprover {
    private String userId;
    private String userName;
    private String userMobil;

  }
}
