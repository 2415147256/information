package com.hd123.baas.sop.fcf.controller.unfreeze;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("每日解冻时间")
public class BDailyUnFreezeTime implements Serializable {

  private static final long serialVersionUID = -1381873446050804665L;
  @ApiModelProperty("主键")
  private String uuid;
  @ApiModelProperty(example = "16:00:00", value = "每日解冻开始时间 格式 HH:mm:ss")
  private String startTime;
  @ApiModelProperty(value = "每日解冻截止时间 格式 HH:mm:ss", example = "23:00:00")
  private String endTime;

}
