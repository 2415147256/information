package com.hd123.baas.sop.service.api.pms.activity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("活动类型")
public enum PromActivityType {
  @ApiModelProperty("促销活动")
  prom,
  @ApiModelProperty("爆品活动")
  explosive
}
