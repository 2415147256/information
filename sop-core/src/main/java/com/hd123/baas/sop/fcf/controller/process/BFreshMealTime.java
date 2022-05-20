package com.hd123.baas.sop.fcf.controller.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangweigang
 */
@ApiModel("鲜食餐段明细")
@Getter
@Setter
public class BFreshMealTime implements Serializable {

  private static final long serialVersionUID = 8066498580030005527L;
  @ApiModelProperty(value = "主键", example = "bca21b25-206d-417b-b6fa-970e9cc638cf")
  private String uuid;
  @ApiModelProperty(value = "名称", example = "午餐")
  private String name;
  @ApiModelProperty(value = "餐段开始时间 格式 HH:mm:ss", example = "12:00:00")
  private String startTime;
  @ApiModelProperty(value = "餐段截止时间 格式 HH:mm:ss", example = "14:00:00")
  private String endTime;
  @ApiModelProperty(value = "最后更新时间", example = "2021-03-31 13:20:00")
  private Date lastUpdate;
}
