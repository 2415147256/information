package com.hd123.baas.sop.service.api.basedata.collocation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("门店商品加料组解除绑定请求明细")
public class CollocationGroupShopSkuUnbindLine {

  @ApiModelProperty(value = "加料的ID")
  private String collocationGroupId;
  @ApiModelProperty(value = "SKU的ID")
  private String skuId;
  @ApiModelProperty(value = "门店的ID")
  private String shopId;
}
