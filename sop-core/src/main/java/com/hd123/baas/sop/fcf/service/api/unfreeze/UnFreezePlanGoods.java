package com.hd123.baas.sop.fcf.service.api.unfreeze;

import com.hd123.baas.sop.fcf.controller.process.PlanGoods;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangweigang
 */
@ApiModel("解冻商品")
@Getter
@Setter
public class UnFreezePlanGoods extends PlanGoods {
  private static final long serialVersionUID = -2887972662356302279L;
  @ApiModelProperty(value = "商品解冻状态", example = "confirmed/unfreeze")
  private String state = "unfreeze";
}
