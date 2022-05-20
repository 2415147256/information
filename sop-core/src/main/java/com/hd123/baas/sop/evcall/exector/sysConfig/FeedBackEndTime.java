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
public class FeedBackEndTime {
  @ApiModelProperty(value = "收货质量反馈源", example = "online")
  private String source;
  @ApiModelProperty(value = "收货质量反馈截止时间，时间格式：HH:mm:ss", example = "08:10")
  private String endTime;
}
