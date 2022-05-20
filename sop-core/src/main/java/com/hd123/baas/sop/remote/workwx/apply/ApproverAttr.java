package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Y.U.A.N
 */
@Getter
@Setter
public class ApproverAttr {
  private String attr;

  @JsonProperty("userid")
  private String userId;
}
