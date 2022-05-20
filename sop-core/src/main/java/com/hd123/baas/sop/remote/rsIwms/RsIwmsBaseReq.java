package com.hd123.baas.sop.remote.rsIwms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class RsIwmsBaseReq {
  @ApiModelProperty(value = "traceId，随机数", example = "1234", required = true)
  private String traceId;
  @ApiModelProperty(value = "sendTime，当前时间", example = "2020-02-02 10:00:00", required = true)
  private String sendTime;
  @ApiModelProperty(value = "source", example = "XXX", required = true)
  private String source = "SOP";
  @ApiModelProperty(value = "配送中⼼代码", example = "abc", required = true)
  private String dcCode;
  @ApiModelProperty(value = "⻔店代码", example = "12345", required = true)
  private String storeCode;
}
