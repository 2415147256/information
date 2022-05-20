package com.hd123.baas.sop.remote.rsmas.cat;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录SKU新建")
public class CatSKUCreation extends CatSKUMod {
  @ApiModelProperty("目录id")
  private String catId;
  @ApiModelProperty("SKUID")
  private String skuId;

  public CatSKUCreation() {
  }

  public String getCatId() {
    return this.catId;
  }

  public String getSkuId() {
    return this.skuId;
  }

  public void setCatId(String catId) {
    this.catId = catId;
  }

  public void setSkuId(String skuId) {
    this.skuId = skuId;
  }
}
