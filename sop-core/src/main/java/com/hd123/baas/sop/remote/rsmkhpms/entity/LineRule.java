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
public class LineRule implements Serializable {
  @ApiModelProperty(value = "最低折扣率")
  private BigDecimal discount;
  @ApiModelProperty(value = "折扣起点")
  private BigDecimal minQty;
  @ApiModelProperty(value = "折扣终点")
  private BigDecimal maxQty;
}
