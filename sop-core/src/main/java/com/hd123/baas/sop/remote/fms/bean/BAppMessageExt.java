package com.hd123.baas.sop.remote.fms.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class BAppMessageExt {
  @ApiModelProperty("消息类型")
  private String type;
  @ApiModelProperty("消息参数")
  private Map<String, Object> params;
}
