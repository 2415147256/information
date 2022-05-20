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
@ApiModel(description = "ERP商品GID列表")
public class GoodsIdList {
  @ApiModelProperty(value = "ERP商品GID列表")
  public List<String> goodsIds = new ArrayList<>();
}
