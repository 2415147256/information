package com.hd123.baas.sop.remote.bigdata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author zhangweigang
 */
@ApiModel("日期信息")
@Getter
@Setter
public class DayInfo implements Serializable {
  private static final long serialVersionUID = -3898119043252182021L;
  @ApiModelProperty(value = "周几", example = "周一")
  private String weekInfo;

  @ApiModelProperty(value = "是否工作日", example = "true")
  private boolean workDay;
}
