package com.hd123.baas.sop.remote.rsias.inv;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RsShopSkuInv {

	private String skuCode;
	private String skuId;
	private String skuName;
	private BigDecimal qty;

}
