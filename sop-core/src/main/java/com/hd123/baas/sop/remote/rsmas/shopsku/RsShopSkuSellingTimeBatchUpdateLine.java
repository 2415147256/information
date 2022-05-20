package com.hd123.baas.sop.remote.rsmas.shopsku;

import com.hd123.baas.sop.remote.rsmas.sku.RsSellingTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lina
 */
@Getter
@Setter
@ApiModel("批量更新门店商品销售时间请求明细")
public class RsShopSkuSellingTimeBatchUpdateLine {
  @ApiModelProperty(value = "门店商品ID")
  private String shopSkuId;
  @ApiModelProperty(value = "销售时间")
  private RsSellingTime sellingTime;
}
