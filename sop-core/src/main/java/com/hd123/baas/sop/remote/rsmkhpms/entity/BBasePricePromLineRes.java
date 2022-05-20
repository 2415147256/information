package com.hd123.baas.sop.remote.rsmkhpms.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class BBasePricePromLineRes implements Serializable {
  @ApiModelProperty(value = "模板行uuid")
  private String uuid;
  @ApiModelProperty(value = "商品id")
  private String skuId;
  @ApiModelProperty(value = "商品GID")
  private String skuGid;
  @ApiModelProperty(value = "商品CODE")
  private String skuCode;
  @ApiModelProperty(value = "商品名称")
  private String skuName;
  @ApiModelProperty(value = "商品规格")
  private BigDecimal skuQpc;
  @ApiModelProperty(value = "商品单位")
  private String skuUnit;

  @ApiModelProperty(value = "促销规则类型，固定值/周环比中位数")
  private String ruleType;
  @ApiModelProperty(value = "促销规则", notes = "促销规则")
  private LineRule promRule;
}
