package com.hd123.baas.sop.remote.rsmas.shopsku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("搭配组和商品的关系解除请求")
public class RsCollocationGroupShopSkuUnbind {

  @ApiModelProperty(value = "解除关系请求明细列表")
  private List<RsCollocationGroupShopSkuUnbindLine> lines = new ArrayList<RsCollocationGroupShopSkuUnbindLine>();
}
