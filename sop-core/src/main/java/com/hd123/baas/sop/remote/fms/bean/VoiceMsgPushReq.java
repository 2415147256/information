package com.hd123.baas.sop.remote.fms.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class VoiceMsgPushReq {
  @ApiModelProperty(value = "模板id", required = true, example = "96ea4694-21f6-4f47-b66e-0bc28b0d21e9")
  private String templateId;
  @ApiModelProperty(value = "消息来源id", required = true, example = "96ea4694-21f6-4f47-b66e-0bc28b0d21e9")
  private String outId;
  @ApiModelProperty(value = "被叫手机号码，钉钉，微信标识等", required = true, example = "18812345678")
  private String callee;
  @ApiModelProperty(value = "请求额外字段", required = true, example = "{\"key\":\"value\",\"key\":\"value\"}")
  private Map<String, String> ext = new HashMap<>();
  @ApiModelProperty(value = "模板参数", required = true, example = "{\"name\":\"张三\",\"time\":\"2021-10-1\"}")
  private Map<String, String> templateParams = new HashMap<>();
}
