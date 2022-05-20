package com.hd123.baas.sop.service.api.basedata.platshopcategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silent
 **/
@Getter
@Setter
@ApiModel(description = "元初关联分类和商品列表")
public class PlatShopCategorySkuList {
  @ApiModelProperty(value = "元初关联分类和商品列表")
  public List<PlatShopCategorySkuKey> items = new ArrayList<>();
}
