package com.hd123.baas.sop.remote.rsmkhpms.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BStateReason {
  @ApiModelProperty("促销单ID")
  private String uuid;
  @ApiModelProperty("状态改变原因")
  private String reason;

  @ApiModelProperty("操作人")
  private BOperator operator;
}
