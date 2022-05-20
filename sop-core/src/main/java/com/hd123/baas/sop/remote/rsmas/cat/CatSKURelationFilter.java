package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录商品关系查询条件定义")
public class CatSKURelationFilter extends Filter {

  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;

  @ApiModelProperty("目录的ID在...范围内")
  private List<String> catIdIn;
  @ApiModelProperty("商品SKU的ID在...范围内")
  private List<String> skuIdIn;
  @ApiModelProperty("是否必选")
  private Boolean required;

  public CatSKURelationFilter() {
  }

  public List<String> getCatIdIn() {
    return this.catIdIn;
  }

  public List<String> getSkuIdIn() {
    return this.skuIdIn;
  }

  public Boolean getRequired() {
    return this.required;
  }

  public void setCatIdIn(List<String> catIdIn) {
    this.catIdIn = catIdIn;
  }

  public void setSkuIdIn(List<String> skuIdIn) {
    this.skuIdIn = skuIdIn;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }
}
