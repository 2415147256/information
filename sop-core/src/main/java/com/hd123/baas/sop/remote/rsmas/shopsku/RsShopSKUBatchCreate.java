package com.hd123.baas.sop.remote.rsmas.shopsku;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RsShopSKUBatchCreate {
  @ApiModelProperty(value = "门店商品关系列表")
  private List<RsShopSkuCreate> lines = new ArrayList<RsShopSkuCreate>();
}
