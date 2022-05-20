package com.hd123.baas.sop.fcf.controller.process;

import com.hd123.baas.sop.fcf.controller.BaseAppRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("智能制作计划单执行请求")
public class ProcessPlanExecuteRequest extends BaseAppRequest {
  @ApiModelProperty(value = "计划单物理主键", example = "ee739156-b529-44e6-8fc7-ce5eb0244863")
  private String planId;

  @ApiModelProperty("选择执行的商品")
  private List<ProcessGoods> goods = new ArrayList<>();

  @ApiModelProperty(value = "门店代码", example = "444", required = true)
  private String storeCode;
}
