package com.hd123.baas.sop.remote.tas.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * 外部服务-任务创建请求
 *
 * @since 1.3.0
 */
@Setter
@Getter
@ApiModel("外部服务-任务创建请求")
public class SkuRetailTerminateTaskReq {

  @ApiModelProperty(value = "任务计划ID", required = true)
  @NotEmpty
  private String planId;

  @ApiModelProperty(value = "发生单据编号", required = true)
  @NotEmpty
  private String orderNo;

  @ApiModelProperty(value = "规则模式", required = true)
  private String ruleModel;
}
