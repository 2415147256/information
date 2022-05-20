package com.hd123.baas.sop.fcf.controller.process;

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
@ApiModel("执行商品")
public class ProcessGoods {
  @ApiModelProperty(value = "执行制作的商品。此商品可以是计划外的，由服务端从计划单中判断", example = "10001949")
  private String skuId;

  @ApiModelProperty(value = "制作数量", example = "10")
  private BigDecimal qty;
}
