package com.hd123.baas.sop.remote.rsmas.cat;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录商品查询条件定义")
public class CatSKUFilter extends Filter {
  private static final long serialVersionUID = 1L;
  @ApiModelProperty("商品名称/商品代码/商品条码/商品ID类似于")
  private String keyword;
  @ApiModelProperty("名称类似于")
  private String nameLike;
  @ApiModelProperty("商品SKU的ID在...范围内")
  private List<String> skuIdIn;
  @ApiModelProperty("目录商品名称类似于")
  private String catSkuNameLike;
  @ApiModelProperty("sku的name/sku的Title的模糊查询")
  private String nameOrTitleLike;
  @ApiModelProperty("商品SKU品牌名称类似于")
  private String skuBrandNameLike;
  @ApiModelProperty("商品SKU类型等于，普通/组合")
  private String skuComboTypeEq;
  @ApiModelProperty("商品SKU销售方式等于: 散装/标准")
  private String skuSaleTypeEq;
  @ApiModelProperty("商品SKU主图是否为空")
  private Boolean skuImage;
  @ApiModelProperty("合格标记等于：合格/不合格")
  private Boolean skuQualifiedEq;
  @ApiModelProperty("是否上架")
  private Boolean enabled;
  @ApiModelProperty("明康汇专用，如果商品价格不存在，则不返回目录商品，使用时customerIdEq和shopIdEq最好一起给值，才能准确定位")
  private Boolean priceExists = false;
  @ApiModelProperty(value = "客户ID等于", required = true)
  private String customerIdEq;
  @ApiModelProperty("门店ID等于")
  private String shopIdEq;

  public CatSKUFilter() {
  }

  public String getKeyword() {
    return this.keyword;
  }

  public String getNameLike() {
    return this.nameLike;
  }

  public List<String> getSkuIdIn() {
    return this.skuIdIn;
  }

  public String getCatSkuNameLike() {
    return this.catSkuNameLike;
  }

  public String getNameOrTitleLike() {
    return this.nameOrTitleLike;
  }

  public String getSkuBrandNameLike() {
    return this.skuBrandNameLike;
  }

  public String getSkuComboTypeEq() {
    return this.skuComboTypeEq;
  }

  public String getSkuSaleTypeEq() {
    return this.skuSaleTypeEq;
  }

  public Boolean getSkuImage() {
    return this.skuImage;
  }

  public Boolean getSkuQualifiedEq() {
    return this.skuQualifiedEq;
  }

  public Boolean getEnabled() {
    return this.enabled;
  }

  public Boolean getPriceExists() {
    return this.priceExists;
  }

  public String getCustomerIdEq() {
    return this.customerIdEq;
  }

  public String getShopIdEq() {
    return this.shopIdEq;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public void setNameLike(String nameLike) {
    this.nameLike = nameLike;
  }

  public void setSkuIdIn(List<String> skuIdIn) {
    this.skuIdIn = skuIdIn;
  }

  public void setCatSkuNameLike(String catSkuNameLike) {
    this.catSkuNameLike = catSkuNameLike;
  }

  public void setNameOrTitleLike(String nameOrTitleLike) {
    this.nameOrTitleLike = nameOrTitleLike;
  }

  public void setSkuBrandNameLike(String skuBrandNameLike) {
    this.skuBrandNameLike = skuBrandNameLike;
  }

  public void setSkuComboTypeEq(String skuComboTypeEq) {
    this.skuComboTypeEq = skuComboTypeEq;
  }

  public void setSkuSaleTypeEq(String skuSaleTypeEq) {
    this.skuSaleTypeEq = skuSaleTypeEq;
  }

  public void setSkuImage(Boolean skuImage) {
    this.skuImage = skuImage;
  }

  public void setSkuQualifiedEq(Boolean skuQualifiedEq) {
    this.skuQualifiedEq = skuQualifiedEq;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public void setPriceExists(Boolean priceExists) {
    this.priceExists = priceExists;
  }

  public void setCustomerIdEq(String customerIdEq) {
    this.customerIdEq = customerIdEq;
  }

  public void setShopIdEq(String shopIdEq) {
    this.shopIdEq = shopIdEq;
  }
}
