package com.hd123.baas.sop.remote.dingtalk;

import lombok.Getter;
import lombok.Setter;

/**
 * 获取token的响应实体
 */
@Setter
@Getter
public class AcquireTokenResponse extends BaseResponse {
  /**
   * token
   */
  private String access_token;
  /**
   * token过期时间
   */
  private Integer expires_in;
}
