package com.hd123.baas.sop.remote.rsh6sop.sku.publishplan;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 日均叫货量查询结果
 *
 * @author liuhaoxin
 * @since 2021-11-29
 */
@Data
@ApiModel(description = "日均叫货量查询结果")
public class AvgReqQty {

  @ApiModelProperty(value = " 日均叫货量(单品数)")
  private BigDecimal avgReqQty;
  @ApiModelProperty(value = "商品GID")
  private Integer gdGid;
}
