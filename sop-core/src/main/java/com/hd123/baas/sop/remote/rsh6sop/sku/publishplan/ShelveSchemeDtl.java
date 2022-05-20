/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	h6-sop-api
 * 文件名：	ShelveSchemeDtl.java
 * 模块说明：	
 * 修改历史：
 * 2021年11月11日 - panzhibin - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.sku.publishplan;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author panzhibin
 *
 */
@Data
@ApiModel(description = "上架方案商品明细")
public class ShelveSchemeDtl implements Serializable {

  private static final long serialVersionUID = 5521951025182095761L;

  @NotBlank
  @ApiModelProperty(value = "商品GID", example = "3003711", required = true)
  private String gdGid;
  @ApiModelProperty(value = "规格", example = "2", required = true)
  @NotNull
  private BigDecimal qpc;
  @ApiModelProperty(value = "包装规格", example = "1*2", required = true)
  @NotBlank
  @Length(max = 64)
  private String qpcStr;
  @ApiModelProperty(value = "包装单位", example = "箱", required = true)
  @NotBlank
  @Length(max = 32)
  private String munit;
  @NotBlank
  @Length(max = 38)
  @ApiModelProperty(value = "配送仓库", required = true)
  private String wrhGid;
  @NotNull
  @ApiModelProperty(value = "到店单价", required = true)
  private BigDecimal storeSinglePrice;
  @NotNull
  @ApiModelProperty(value = "到店规格价", required = true)
  private BigDecimal storePrice;
  @NotNull
  @ApiModelProperty(value = "到仓单价", required = true)
  private BigDecimal wrhSinglePrice;
  @NotNull
  @ApiModelProperty(value = "到仓规格价", required = true)
  private BigDecimal wrhPrice;
  @NotNull
  @ApiModelProperty(value = "是否限量", required = true)
  private Integer isLimit;
  @ApiModelProperty(value = "限量数，不限量时传0", required = true)
  private BigDecimal limitQty;
  @Length(max = 128)
  @ApiModelProperty(value = "产地", required = false)
  private String origin;
  @Length(max = 255)
  @ApiModelProperty(value = "备注", required = false)
  private String note;
}
