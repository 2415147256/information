package com.hd123.baas.sop.remote.rsh6sop.inv;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 可用库存查询对象
 *
 * @author liuhaoxin
 * @date 2021-11-30
 */
@Data
@ApiModel(description = "可用库存")
public class AvailableInv {

  @ApiModelProperty(value = "商品GID", required = true)
  private Integer gdGid;
  @ApiModelProperty(value = "商品代码", required = true)
  private String gdCode;
  @ApiModelProperty(value = "商品名称", required = true)
  private String gdName;

  @ApiModelProperty(value = "类别代码", required = true)
  private String sortCode;
  @ApiModelProperty(value = "类别名称", required = true)
  private String sortName;

  @ApiModelProperty(value = "物流中心GID", required = true)
  private Integer storeGid;
  @ApiModelProperty(value = "物流中心代码", required = true)
  private String storeCode;
  @ApiModelProperty(value = "物流中心名称", required = true)
  private String storeName;

  @ApiModelProperty(value = "仓位GID", required = true)
  private Integer wrhGid;
  @ApiModelProperty(value = "仓位代码", required = true)
  private String wrhCode;
  @ApiModelProperty(value = "仓位名称", required = true)
  private String wrhName;

  @ApiModelProperty(value = "组织GID", required = true)
  private Integer orgGid;
  @ApiModelProperty(value = "组织代码", required = true)
  private String orgCode;
  @ApiModelProperty(value = "组织名称", required = true)
  private String orgName;

  @ApiModelProperty(value = "包装规格", required = true)
  private String qpcStr;
  @ApiModelProperty(value = "包装单位", required = true)
  private String munit;
  @ApiModelProperty(value = "规格", required = true)
  private BigDecimal qpc;

  @ApiModelProperty(value = "可用库存件数", required = true)
  private BigDecimal qtyCount;
  @ApiModelProperty(value = "在途库存件数", required = true)
  private BigDecimal wayQtyCount;
  @ApiModelProperty(value = "成本价(规格价)", required = true)
  private BigDecimal price;

}
