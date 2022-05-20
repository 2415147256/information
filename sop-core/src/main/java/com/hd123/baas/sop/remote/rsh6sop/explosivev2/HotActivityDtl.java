package com.hd123.baas.sop.remote.rsh6sop.explosivev2;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 爆品活动明细
 *
 * @author liuhaoxin
 * @date 2021-12-7
 */
@Getter
@Setter
public class HotActivityDtl {
  @ApiModelProperty(value = "商品GID", example = "3006987")
  private String gdGid;
  @ApiModelProperty(value = "是否限量")
  private Integer isLimit;
  @ApiModelProperty(value = "起订量（单品数）")
  private BigDecimal minQty;
  @ApiModelProperty(value = "包装单位", example = "箱")
  private String munit;

  @ApiModelProperty(value = "促销价（规格价）")
  private BigDecimal price;
  @ApiModelProperty(value = "规格", example = "2")
  private BigDecimal qpc;
  @ApiModelProperty(value = "包装规格", example = "1*2")
  private String qpcStr;
  @ApiModelProperty(value = "报名数（单品数）")
  private BigDecimal qty;

  @ApiModelProperty(value = "门店GID", example = "1003711")
  private String storeGid;

}
