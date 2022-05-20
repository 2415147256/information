package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录SKU关系")
public class CatSKURelation {
  @ApiModelProperty("目录id")
  private String catId;
  @ApiModelProperty("SKUID")
  private String skuId;
  @ApiModelProperty("是否上架")
  private Boolean enabled;
  @ApiModelProperty("是否必选")
  private Boolean required;

  public CatSKURelation() {
  }

  public String getCatId() {
    return this.catId;
  }

  public String getSkuId() {
    return this.skuId;
  }

  public Boolean getEnabled() {
    return this.enabled;
  }

  public Boolean getRequired() {
    return this.required;
  }

  public void setCatId(String catId) {
    this.catId = catId;
  }

  public void setSkuId(String skuId) {
    this.skuId = skuId;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }
}
