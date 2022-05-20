package com.hd123.baas.sop.remote.workwx.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class BaseWorkWxResponse {

  @JsonProperty("errcode")
  private int errCode;

  @JsonProperty("errmsg")
  private String errMsg;

  @JsonIgnore
  public boolean success() {
    return errCode == 0;
  }

}
