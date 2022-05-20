package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Y.U.A.N
 */
@Getter
@Setter
public class SummaryInfo {
  @JsonProperty("summary_info")
  private List<TemplateText> summaryInfo;

}
