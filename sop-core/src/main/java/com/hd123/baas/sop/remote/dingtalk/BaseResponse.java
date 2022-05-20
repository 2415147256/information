package com.hd123.baas.sop.remote.dingtalk;

import lombok.Getter;
import lombok.Setter;

/**
 * 钉钉响应实体：基础字段
 */
@Setter
@Getter
public class BaseResponse {
  // 响应码
  private Integer errcode;
  // 响应实体
  private String errmsg;
}
