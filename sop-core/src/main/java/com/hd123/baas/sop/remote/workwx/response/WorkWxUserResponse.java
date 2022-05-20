package com.hd123.baas.sop.remote.workwx.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class WorkWxUserResponse extends BaseWorkWxResponse {
  @JsonProperty(value = "userid")
  private String userId;
  private String name;
  private String mobile;

}
