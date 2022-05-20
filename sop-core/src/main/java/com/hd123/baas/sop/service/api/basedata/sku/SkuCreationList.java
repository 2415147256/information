package com.hd123.baas.sop.service.api.basedata.sku;

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
@ApiModel(description = "销售商品新建信息列表")
public class SkuCreationList {
  @ApiModelProperty(value = "销售商品新建信息列表")
  public List<SkuCreation> items = new ArrayList<>();
}
