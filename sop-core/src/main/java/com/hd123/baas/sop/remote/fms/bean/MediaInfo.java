package com.hd123.baas.sop.remote.fms.bean;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MediaInfo {
  @ApiModelProperty("媒体类型")
  private String type;
  @ApiModelProperty("页面ID")
  private String id;
  @ApiModelProperty("页面链接")
  private String content;
  @ApiModelProperty("跳转参数")
  private JsonNode params;
}
