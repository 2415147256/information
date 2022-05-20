package com.hd123.baas.sop.fcf.controller.process;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("商品")
public class PlanGoods implements Serializable {
  private static final long serialVersionUID = -1349886006905374044L;
  @ApiModelProperty(value = "逻辑主键", example = "2d563fac-aa42-4a0f-ba5b-e5beeb01d6be")
  private String uuid;
  @ApiModelProperty(value = "规格商品主键", example = "10001949")
  private String skuId;
  @ApiModelProperty(value = "上周销量", example = "9.0000")
  private BigDecimal lastWeekSale;
  @ApiModelProperty(value = "商品名称， 应当提供FreshGoods.aliasName", example = "大肉包")
  private String productName;

  @ApiModelProperty(value = "建议数", example = "12")
  private BigDecimal suggestQty;
  @ApiModelProperty(value = "实际数", example = "10")
  private BigDecimal qty = BigDecimal.ZERO;

  @ApiModelProperty(value = "规格", example = "10")
  private String qpc;
  @ApiModelProperty(value = "包装规格", example = "个")
  private String spec;
  @ApiModelProperty(value = "别名", example = "小笼包")
  private String aliasName;

  public PlanGoods() {
  }

  public PlanGoods(String uuid, String skuId, BigDecimal lastWeekSale, String productName, BigDecimal suggestQty,
      BigDecimal qty) {
    this.uuid = uuid;
    this.skuId = skuId;
    this.lastWeekSale = lastWeekSale;
    this.productName = productName;
    this.suggestQty = suggestQty;
    this.qty = qty;
  }
}
