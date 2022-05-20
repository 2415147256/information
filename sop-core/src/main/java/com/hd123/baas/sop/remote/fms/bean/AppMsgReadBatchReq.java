package com.hd123.baas.sop.remote.fms.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author W.J.H.7
 */
@ApiModel("消息批量阅读")
@Setter
@Getter
public class AppMsgReadBatchReq {
  @ApiModelProperty(value = "消息ID集合", required = true)
  private List<String> uuids;
  @ApiModelProperty(value = "操作的用户ID", required = true)
  private String operator;
  @ApiModelProperty(value = "操作的用户名称", required = true)
  private String operatorName;
  @ApiModelProperty(value = "操作的APPID", required = true)
  private String operateAppId;
}