package com.hd123.baas.sop.service.api.basedata.sku;

import com.hd123.baas.sop.service.api.basedata.category.Category;
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
@ApiModel(description = "销售商品")
public class Sku {

  /**
   * 级联分类
   */
  public static final String PART_CATEGORY = "category";
  /**
   * 级联转化信息
   */
  public static final String PART_BOM = "bom";
  public static final String PART_TAG = "tag";

  @ApiModelProperty(value = "ERP商品GID")
  public String goodsGid;
  @ApiModelProperty(value = "ID")
  public String id;
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
  @ApiModelProperty(value = "后台分类")
  public Category category;
  @ApiModelProperty(value = "已删除")
  private Boolean deleted = false;
  @ApiModelProperty(value = "PLU码，称重商品才有")
  public String plu;
  @ApiModelProperty(value = "是否必选")
  private Boolean required;
  @ApiModelProperty(value = "输入码")
  private String inputCode;
  @ApiModelProperty(value = "H6商品类型")
  public String h6GoodsType;
  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "箱规描述",required = false)
  private String qpcDesc;
  @ApiModelProperty(value = "商品助记码", example = "sm")
  public String pyCode;
  @ApiModelProperty(value = "是否配货规格，取值 0-否 1-是 2-默认值，值为2时才是默认的配货规格。", example = "2")
  public Integer du;
}
