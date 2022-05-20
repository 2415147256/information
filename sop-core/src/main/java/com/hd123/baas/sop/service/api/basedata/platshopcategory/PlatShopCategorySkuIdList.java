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
@ApiModel(description = "元初门店分类SKUID列表对象")
public class PlatShopCategorySkuIdList {
  @ApiModelProperty(value = "SKUID列表")
  private List<String> skuIds = new ArrayList<String>();
}
