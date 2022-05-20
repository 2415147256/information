package com.hd123.baas.sop.remote.rsmas.cat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录SKU修改")
public class CatSKUMod implements Serializable {
  @ApiModelProperty("商品名称(别名)")
  private String skuName;
  @ApiModelProperty("批发价")
  private BigDecimal price;
  @ApiModelProperty("最小起订量")
  private BigDecimal minOrderQty;
  @ApiModelProperty("最大日订货量")
  private BigDecimal maxOrderQty;
  @ApiModelProperty("销售倍数")
  private BigDecimal unitQty;
  @ApiModelProperty("库存显示方式")
  private String stockShowType;
  @ApiModelProperty("库存数量")
  private BigDecimal stockQty;
  @ApiModelProperty("商品特征")
  List<CatSKUFeat> feats;
  @ApiModelProperty("商品单位列表")
  List<CatSKUCase> cases;
  @ApiModelProperty("是否上架")
  private Boolean enabled;
  @ApiModelProperty("是否允许退货")
  private Boolean allowRtn;
  @ApiModelProperty("外部商品ID")
  private String outSkuId;
  @ApiModelProperty("是否必选")
  private Boolean required;

  public CatSKUMod() {
    this.price = BigDecimal.ZERO;
    this.minOrderQty = BigDecimal.ZERO;
    this.maxOrderQty = BigDecimal.ZERO;
    this.unitQty = BigDecimal.ZERO;
    this.stockQty = BigDecimal.ZERO;
    this.feats = new ArrayList();
    this.cases = new ArrayList();
  }

  public String getSkuName() {
    return this.skuName;
  }

  public BigDecimal getPrice() {
    return this.price;
  }

  public BigDecimal getMinOrderQty() {
    return this.minOrderQty;
  }

  public BigDecimal getMaxOrderQty() {
    return this.maxOrderQty;
  }

  public BigDecimal getUnitQty() {
    return this.unitQty;
  }

  public String getStockShowType() {
    return this.stockShowType;
  }

  public BigDecimal getStockQty() {
    return this.stockQty;
  }

  public List<CatSKUFeat> getFeats() {
    return this.feats;
  }

  public List<CatSKUCase> getCases() {
    return this.cases;
  }

  public Boolean getEnabled() {
    return this.enabled;
  }

  public Boolean getAllowRtn() {
    return this.allowRtn;
  }

  public String getOutSkuId() {
    return this.outSkuId;
  }

  public Boolean getRequired() {
    return this.required;
  }

  public void setSkuName(String skuName) {
    this.skuName = skuName;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public void setMinOrderQty(BigDecimal minOrderQty) {
    this.minOrderQty = minOrderQty;
  }

  public void setMaxOrderQty(BigDecimal maxOrderQty) {
    this.maxOrderQty = maxOrderQty;
  }

  public void setUnitQty(BigDecimal unitQty) {
    this.unitQty = unitQty;
  }

  public void setStockShowType(String stockShowType) {
    this.stockShowType = stockShowType;
  }

  public void setStockQty(BigDecimal stockQty) {
    this.stockQty = stockQty;
  }

  public void setFeats(List<CatSKUFeat> feats) {
    this.feats = feats;
  }

  public void setCases(List<CatSKUCase> cases) {
    this.cases = cases;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public void setAllowRtn(Boolean allowRtn) {
    this.allowRtn = allowRtn;
  }

  public void setOutSkuId(String outSkuId) {
    this.outSkuId = outSkuId;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }
}
