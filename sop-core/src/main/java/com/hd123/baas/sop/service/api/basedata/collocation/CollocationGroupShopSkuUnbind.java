package com.hd123.baas.sop.service.api.basedata.collocation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("搭配组和商品的关系解除请求")
public class CollocationGroupShopSkuUnbind {

  @ApiModelProperty(value = "解除关系请求明细列表")
  private List<CollocationGroupShopSkuUnbindLine> lines = new ArrayList<CollocationGroupShopSkuUnbindLine>();
}
