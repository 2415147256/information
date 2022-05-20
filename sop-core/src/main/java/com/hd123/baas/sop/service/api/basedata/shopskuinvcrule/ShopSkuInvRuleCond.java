package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 规则条件
 *
 * @author qyh
 */
@Setter
@Getter
@ApiModel("规则条件")
public class ShopSkuInvRuleCond implements Serializable {
  private static final long serialVersionUID = -7130166136884318815L;
  @ApiModelProperty(value = "门店范围")
  private ShopRange shopRange;
  @ApiModelProperty(value = "商品范围")
  private SkuRange skuRange;
  @ApiModelProperty(value = "其他条件")
  private List<RsParameter> parameters = new ArrayList<>();
}
