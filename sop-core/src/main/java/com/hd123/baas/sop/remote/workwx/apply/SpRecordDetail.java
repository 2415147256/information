package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SpRecordDetail {
  private UserId approver;
  private String speech;

  @JsonProperty("sptime")
  private Long spTime;

  @JsonProperty("sp_status")
  private Integer spStatus;

  @JsonProperty("media_id")
  private List<String> mediaIds;
}
