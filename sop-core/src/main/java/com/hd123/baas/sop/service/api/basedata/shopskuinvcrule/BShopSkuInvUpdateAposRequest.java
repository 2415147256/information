package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lina
 */
@Getter
@Setter
@ApiModel(value="更新门店商品库存请求")
public class BShopSkuInvUpdateAposRequest {

  @ApiModelProperty(value = "门店ID")
  private String shopId;
  @ApiModelProperty(value = "skuId列表")
  private List<String> skuIds = new ArrayList<String>();

  @ApiModelProperty(value = "规则数量")
  private Integer qty;
  @ApiModelProperty(value = "开始生效时间")
  private Date startEffectTime;

  @ApiModelProperty(value = "库存数量")
  private BigDecimal shopSkuQty;
  @ApiModelProperty(value = "是否售罄")
  private Boolean sellOut;
}
