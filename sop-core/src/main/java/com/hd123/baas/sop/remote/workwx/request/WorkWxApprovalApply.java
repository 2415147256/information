package com.hd123.baas.sop.remote.workwx.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hd123.baas.sop.remote.workwx.apply.ApplyData;
import com.hd123.baas.sop.remote.workwx.apply.ApproverAttr;
import com.hd123.baas.sop.remote.workwx.apply.SummaryInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WorkWxApprovalApply {

  // 申请人userId，此审批申请将以此员工身份提交，申请人需在应用可见范围内。required = true
  @JsonProperty("creator_userid")
  private String creatorUserid;

  @JsonProperty("template_id")
  // value = "模板ID", required = true, example = "12")
  private String templateId;

  // value = "审批人模式", required = true, example = "1")
  @JsonProperty("use_template_approver")
  private int useTemplateApprover = 1;

  // value = "审批流程信息", notes = "仅当use_template_approver=0可用")
  @JsonProperty("approver")
  private List<ApproverAttr> approverAttrs = new ArrayList<>();

  // value = "抄送人节点userid列表", notes = "仅当use_template_approver=0可用")
  private List<String> notifyer = new ArrayList<>();

  // value = "抄送方式", example = "1-提单时抄送（默认值）； 2-单据通过后抄送；3-提单和单据通过后抄送", notes = "use_template_approver为0时生效")
  @JsonProperty("notify_type")
  private int notifyType = 1;

  // value = "审批申请数据")
  @JsonProperty("apply_data")
  private ApplyData applyData;

  // value = "摘要信息,最多3行")
  @JsonProperty("summary_list")
  private List<SummaryInfo> summaryList = new ArrayList<>();


}