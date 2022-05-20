/**
 *
 */
package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author qyh
 */
@Getter
@Setter
@ApiModel("门店商品信息")
public class ShopSkuRange {
  @ApiModelProperty(value = "门店信息")
  private RsIdName shop;
  @ApiModelProperty(value = "商品信息")
  private RsIdName sku;
}
