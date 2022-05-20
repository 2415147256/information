package com.hd123.baas.sop.fcf.service.api.unfreeze;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("解冻的商品")
public class UnFreezeGoods {

  @ApiModelProperty(value = "规格商品主键", example = "10001949", required = true)
  private String skuId;

  @ApiModelProperty(value = "实际数", example = "3", required = true)
  private BigDecimal qty = BigDecimal.ZERO;

}
