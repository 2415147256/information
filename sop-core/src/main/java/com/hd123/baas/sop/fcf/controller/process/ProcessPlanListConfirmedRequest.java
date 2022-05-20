package com.hd123.baas.sop.fcf.controller.process;

import com.hd123.baas.sop.fcf.controller.BaseAppRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel
public class ProcessPlanListConfirmedRequest extends BaseAppRequest {
  private static final long serialVersionUID = -1195643800224774335L;
  @ApiModelProperty(value = "计划单uuid", example = "ee739156-b529-44e6-8fc7-ce5eb0244863", required = true)
  private String planUuid;

  @ApiModelProperty(value = "指定类别的uuid", required = true, example = "c008360c-123a-4ca5-9cd2-43205bfed7aa")
  private String categoryUuid;

  @ApiModelProperty(value = "指定餐段的uuid", example = "bca21b25-206d-417b-b6fa-970e9cc638cf")
  private String mealUuid;

  @ApiModelProperty(value = "门店代码", example = "444", required = true)
  private String storeCode;
}
