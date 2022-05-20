package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录门店查询条件定义")
public class CatShopFilter extends Filter {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;

  @ApiModelProperty("名称类似于")
  private String nameLike;
  @ApiModelProperty("代码类似于")
  private String codeLike;
  @ApiModelProperty("门店ID等于")
  private String shopIdEq;
  @ApiModelProperty("门店ID类似于")
  private String shopIdLike;
  @ApiModelProperty("门店ID在列表")
  private List<String> shopIdIn;

  public CatShopFilter() {
  }

  public String getNameLike() {
    return this.nameLike;
  }

  public String getCodeLike() {
    return this.codeLike;
  }

  public String getShopIdEq() {
    return this.shopIdEq;
  }

  public String getShopIdLike() {
    return this.shopIdLike;
  }

  public List<String> getShopIdIn() {
    return this.shopIdIn;
  }

  public void setNameLike(String nameLike) {
    this.nameLike = nameLike;
  }

  public void setCodeLike(String codeLike) {
    this.codeLike = codeLike;
  }

  public void setShopIdEq(String shopIdEq) {
    this.shopIdEq = shopIdEq;
  }

  public void setShopIdLike(String shopIdLike) {
    this.shopIdLike = shopIdLike;
  }

  public void setShopIdIn(List<String> shopIdIn) {
    this.shopIdIn = shopIdIn;
  }
}