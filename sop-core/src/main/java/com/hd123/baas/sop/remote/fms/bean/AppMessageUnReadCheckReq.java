package com.hd123.baas.sop.remote.fms.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zyt
 */
@ApiModel("未读消息校验")
@Setter
@Getter
public class AppMessageUnReadCheckReq {

  @ApiModelProperty(value = "门店ID")
  private String shop;
  @ApiModelProperty(value = "应用ID")
  private String appId;
  @ApiModelProperty(value = "用户ID")
  private String userId;

  @ApiModelProperty(value = "时间范围-开始")
  private Date lowDate;
  @ApiModelProperty(value = "时间范围-结束")
  private Date highDate;

}