package com.hd123.baas.sop.remote.rsmas.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品规格
 * @author lina
 */
@Getter
@Setter
public class RsGoodsQpc implements Serializable {

  private static final long serialVersionUID = -7745422107486008901L;

  @ApiModelProperty(value = "规格ID")
  private String id;
  @ApiModelProperty(value = "规格")
  private BigDecimal qpc;
  @ApiModelProperty(value = "规格文字")
  private String qpcStr;
  @ApiModelProperty(value = "规格单位")
  private String unit;
  @ApiModelProperty(value = "重量")
  private BigDecimal weight;
}
