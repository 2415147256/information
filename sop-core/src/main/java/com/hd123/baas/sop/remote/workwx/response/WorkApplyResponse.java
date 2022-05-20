package com.hd123.baas.sop.remote.workwx.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class WorkApplyResponse extends BaseWorkWxResponse {

  @JsonProperty(value = "sp_no")
  private String spNo;

}
