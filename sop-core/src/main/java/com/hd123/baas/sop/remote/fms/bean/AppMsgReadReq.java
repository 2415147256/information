package com.hd123.baas.sop.remote.fms.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@ApiModel("新建消息请求")
@Setter
@Getter
public class AppMsgReadReq {
  @ApiModelProperty(value = "消息ID", required = true)
  private String uuid;
  @ApiModelProperty(value = "操作的用户ID", required = true)
  private String operator;
  @ApiModelProperty(value = "操作的用户名称", required = true)
  private String operatorName;
  @ApiModelProperty(value = "操作的APPID", required = true)
  private String operateAppId;
}