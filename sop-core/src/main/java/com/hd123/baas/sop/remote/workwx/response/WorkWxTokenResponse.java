package com.hd123.baas.sop.remote.workwx.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class WorkWxTokenResponse extends BaseWorkWxResponse {

  // 获取到的凭证
  @JsonProperty(value = "access_token")
  private String accessToken;

  // 凭证有效时间，单位：秒。目前是7200秒之内的值。
  @JsonProperty(value = "expires_in")
  private int expiresIn;

}
