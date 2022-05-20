package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplyInfo {
  @JsonProperty("sp_no")
  private String spNo;

  @JsonProperty("sp_name")
  private String spName;

  @JsonProperty("template_id")
  private String templateId;

  @JsonProperty("sp_status")
  private Integer spStatus;

  @JsonProperty("apply_time")
  private Long applyTime;

  private Applyer applyer;

  private List<UserId> notifyer;

  @JsonProperty("sp_record")
  private List<SpRecord> spRecords;

  private List<ApprovalComment> comments;

  @JsonProperty("apply_data")
  private ApplyData applyData;

}
