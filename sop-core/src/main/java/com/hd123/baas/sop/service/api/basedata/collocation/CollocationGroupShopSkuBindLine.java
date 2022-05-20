package com.hd123.baas.sop.service.api.basedata.collocation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("门店商品加料组绑定请求明细")
public class CollocationGroupShopSkuBindLine {

  @ApiModelProperty(value = "sku的id")
  private String skuId;
  @ApiModelProperty(value = "门店的id")
  private String shopId;
  @ApiModelProperty(value = "加料组id")
  private String collocationGroupId;
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
}
