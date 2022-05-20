package com.hd123.baas.sop.remote.bigdata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangweigang
 */
@ApiModel("天气")
@Getter
@Setter
public class Weather {
  @ApiModelProperty("天气图标url")
  private String imgUrl;

  @ApiModelProperty(value = "天气描述", example = "小雨")
  private String description;

  @ApiModelProperty(value = "气温", example = "6~12")
  private String temperature;
}
