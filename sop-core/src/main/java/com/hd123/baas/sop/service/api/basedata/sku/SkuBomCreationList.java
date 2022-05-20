package com.hd123.baas.sop.service.api.basedata.sku;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Silent
 **/
@Getter
@Setter
@ApiModel(description = "销售商品转换关系新建信息列表")
public class SkuBomCreationList {
  @ApiModelProperty(value = "销售商品转换关系新建信息列表")
  public List<SkuBomCreation> items = new ArrayList<>();
}
