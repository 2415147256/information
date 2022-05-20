package com.hd123.baas.sop.remote.rsmas.shopskuinvcrule;

import com.hd123.baas.sop.remote.rsmas.RsParameter;
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
public class RsShopSkuInvRuleCond implements Serializable {
  private static final long serialVersionUID = -7130166136884318815L;
  @ApiModelProperty(value = "门店范围")
  private RsShopRange shopRange;
  @ApiModelProperty(value = "商品范围")
  private RsSkuRange skuRange;
  @ApiModelProperty(value = "其他条件")
  private List<RsParameter> parameters = new ArrayList<>();
}
