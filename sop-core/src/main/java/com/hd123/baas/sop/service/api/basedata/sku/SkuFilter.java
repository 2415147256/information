package com.hd123.baas.sop.service.api.basedata.sku;

import java.math.BigDecimal;
import java.util.List;

import com.hd123.baas.sop.service.api.basedata.Filter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "销售商品查询定义")
public class SkuFilter extends Filter {

  private static final long serialVersionUID = 1L;
  @ApiModelProperty(value = "orgId在...范围内")
  private List<String> orgIdIn;
  @ApiModelProperty(value = "orgId等于")
  private String orgIdEq;
  @ApiModelProperty(value = "orgType等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "分类ID在...范围内")
  private List<String> categoryIdIn;
  @ApiModelProperty(value = "分类ID在...范围内")
  private List<String> categoryIdNotIn;
  @ApiModelProperty(value = "分类ID等于")
  private String categoryUpperIdEq;
  @ApiModelProperty(value = "ID在...范围内")
  private List<String> idIn;
  @ApiModelProperty(value = "CODE在...范围内")
  private List<String> codeIn;
  @ApiModelProperty(value = "代码名称类似于")
  private String keywordLike;
  @ApiModelProperty(value = "QPC等于")
  private BigDecimal qpcEq;
  @ApiModelProperty(value = "是否已删除等于")
  private Boolean deletedEq;
  @ApiModelProperty(value = "主档ID在...范围内")
  private List<String> goodsGidIn;
  @ApiModelProperty(value = "主档ID不在...范围内")
  private List<String> goodsGidNotIn;
  @ApiModelProperty(value = "商品定义等于")
  private String skuDefineEq;
  @ApiModelProperty(value = "是否必选等于")
  private Boolean requiredEq;
  @ApiModelProperty(value = "plu是否为空")
  private Boolean pluIsNull;
  @ApiModelProperty(value = "inputCode等于")
  private String inputCodeEq;
  @ApiModelProperty(value = "inputCode在...内")
  private List<String> inputCodeIn;
  @ApiModelProperty(value = "inputCode是否为空")
  private Boolean inputCodeIsNull;
  @ApiModelProperty(value = "商品类型在...内")
  private List<String> goodsTypeIn;
  @ApiModelProperty(value = "商品类型不在...内")
  private List<String> goodsTypeNotIn;
  @ApiModelProperty(value = "是否配货规格，取值 0-否 1-是 2-默认值，值为2时才是默认的配货规格")
  private String skuDuEq;
}
