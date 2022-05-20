package com.hd123.baas.sop.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 目标值配置值
 *
 * @Author shenmin
 */
@Getter
@Setter
public class PromotionTarget {
  @ApiModelProperty(value = "定义")
  private String def;
  @ApiModelProperty(value = "配置值")
  private BigDecimal val;
}
