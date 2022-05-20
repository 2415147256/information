package com.hd123.baas.sop.remote.rsmas.cat;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录商品单位")
public class CatSKUCase {
  @ApiModelProperty("单位")
  private String unit;
  @ApiModelProperty("重量单位")
  private String weightUnit;
  @ApiModelProperty("重量")
  private BigDecimal weight;
  @ApiModelProperty("最小起订量")
  private BigDecimal minOrderQty;

  public CatSKUCase() {
    this.minOrderQty = BigDecimal.ZERO;
  }

  public String getUnit() {
    return this.unit;
  }

  public String getWeightUnit() {
    return this.weightUnit;
  }

  public BigDecimal getWeight() {
    return this.weight;
  }

  public BigDecimal getMinOrderQty() {
    return this.minOrderQty;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public void setWeightUnit(String weightUnit) {
    this.weightUnit = weightUnit;
  }

  public void setWeight(BigDecimal weight) {
    this.weight = weight;
  }

  public void setMinOrderQty(BigDecimal minOrderQty) {
    this.minOrderQty = minOrderQty;
  }
}