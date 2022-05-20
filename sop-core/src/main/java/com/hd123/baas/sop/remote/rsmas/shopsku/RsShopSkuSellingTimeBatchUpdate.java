package com.hd123.baas.sop.remote.rsmas.shopsku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lina
 */
@Getter
@Setter
@ApiModel("批量更新门店商品销售时间请求")
public class RsShopSkuSellingTimeBatchUpdate {
  @ApiModelProperty(value = "批量更新门店商品销售时间请求明细")
  private List<RsShopSkuSellingTimeBatchUpdateLine> items = new ArrayList<>();
}
