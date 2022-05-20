package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SpRecord {
  @JsonProperty("sp_status")
  private Integer spStatus;

  @JsonProperty("approverattr")
  private Integer approverAttr;

  @JsonProperty("details")
  private List<SpRecordDetail> details;
}
