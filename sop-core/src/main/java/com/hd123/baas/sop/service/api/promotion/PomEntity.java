package com.hd123.baas.sop.service.api.promotion;

import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class PomEntity extends UCN {
  private EntityType entityType;

  @ApiModelProperty("skuId")
  private String skuId;
  @ApiModelProperty("配货规格")
  private BigDecimal qpc;
  @ApiModelProperty("配货规格")
  private BigDecimal alcQpc;
  @ApiModelProperty("配货单位")
  private String alcUnit;
  @ApiModelProperty(value = "最小规格单位")
  private String minMunit;

  @ApiModelProperty("商品图片")
  private String imageUrl;
  @ApiModelProperty("商品分类")
  private Category category;
  @ApiModelProperty("规格")
  private String specification;
  @ApiModelProperty("生产厂家")
  private String manufactory;
  @ApiModelProperty("计量单位")
  private String measureUnit;
  @ApiModelProperty("原价")
  private BigDecimal price;
  @ApiModelProperty(value = "箱规描述",required = false)
  private String qpcDesc;
  @ApiModelProperty(value = "商品助记码", example = "sm")
  public String pyCode;
  @ApiModelProperty(value = "是否配货规格，取值 0-否 1-是 2-默认值，值为2时才是默认的配货规格。", example = "2")
  public Integer du;
}
