package com.hd123.baas.sop.fcf.controller.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("计划外商品")
public class BUnPlanGoods {
  private static final long serialVersionUID = 5620804755670269205L;
  @ApiModelProperty(value = "规格商品主键", example = "10001949")
  private String skuId;
  @ApiModelProperty(value = "商品名称， 应当提供别名(FreshGoods.aliasName)", example = "关东煮萝卜")
  private String productName;
}
