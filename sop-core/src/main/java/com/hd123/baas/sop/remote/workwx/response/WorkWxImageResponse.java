package com.hd123.baas.sop.remote.workwx.response;

import lombok.Getter;
import lombok.Setter;

/**
 * 企业微信上传回执
 */
@Setter
@Getter
public class WorkWxImageResponse extends BaseWorkWxResponse {

  private String url;
}
