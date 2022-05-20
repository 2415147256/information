package com.hd123.baas.sop.service.api.basedata.sku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Silent
 **/
@Getter
@Setter
@ApiModel(description = "销售商品更新信息")
public class SkuUpdate {

  @ApiModelProperty(value = "代码")
  public String code;
  @ApiModelProperty(value = "名称")
  public String name;
  @ApiModelProperty(value = "规格")
  public BigDecimal qpc;
  @ApiModelProperty(value = "单位")
  public String unit;
  @ApiModelProperty(value = "单位转化")
  public List<String> bom;
  @ApiModelProperty(value = "标签")
  public List<Tag> tags;
  @ApiModelProperty(value = "原价")
  public BigDecimal price;
  @ApiModelProperty(value = "后台分类ID")
  public String categoryId;
  @ApiModelProperty(value = "H6商品类型")
  public String h6GoodsType;
  @ApiModelProperty(value = "商品助记码", example = "sm")
  public String pyCode;
  @ApiModelProperty(value = "是否配货规格，取值 0-否 1-是 2-默认值，值为2时才是默认的配货规格。", example = "2")
  public Integer du = 0;
}
