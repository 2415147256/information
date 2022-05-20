package com.hd123.baas.sop.evcall.exector.sysConfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 质量反馈结束时间
 *
 * @Author liuhaoxin
 */
@Getter
@Setter
public class FeedBackDays {
  @ApiModelProperty(value = "收货质量反馈源", example = "EC,offline")
  private String source;
  @ApiModelProperty(value = "反馈时间设置（确认收货后X天可反馈）")
  private Integer days;
}
