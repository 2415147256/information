package com.hd123.baas.sop.remote.uas;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("数据授权")
@Getter
@Setter
public class BDataPermission {
  @ApiModelProperty("应用程序id")
  private String appId;
  @ApiModelProperty(value = "资源是指被授权的具体对象", required = true, example = "SHOP")
  private String resource;
  @ApiModelProperty(value = "限制条件是指授权生效的限制条件", required = true, example = "[\"123456\"]")
  private List<String> conditions;
}
