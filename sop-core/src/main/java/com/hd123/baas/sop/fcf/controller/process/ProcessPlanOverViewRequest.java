package com.hd123.baas.sop.fcf.controller.process;

import com.hd123.baas.sop.fcf.controller.BaseAppRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("智能制作计划单概览请求")
public class ProcessPlanOverViewRequest extends BaseAppRequest {
  @ApiModelProperty(value = "门店代码", example = "444", required = true)
  private String storeCode;

  @ApiModelProperty(value = "餐段UUID", example = "d0534829-979f-45bc-8800-96c921096776", required = true)
  private String mealTimeId;

  @ApiModelProperty(value = "处理日期, 每天应该只有一单", example = "2021-03-31 00:00:00", required = true)
  private Date date;
}
