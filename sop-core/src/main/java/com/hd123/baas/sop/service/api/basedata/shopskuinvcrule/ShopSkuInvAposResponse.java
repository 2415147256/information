package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 门店商品库存规则，APOS版本返回
 *
 * @author qyh
 */
@Setter
@Getter
@ApiModel("门店商品库存规则，APOS版本返回")
public class ShopSkuInvAposResponse extends RsMasEntity {
  private static final long serialVersionUID = -4181450075262828397L;

  public final static String TYPE_FILTER_ERP_INV = "filterErpInv";
  public static final String TYPE_FIXE_QTY_INV = "fixedShopSkuQty";

  public final static String LIMIT_INCLUDE = "include";
  public final static String LIMIT_EXCLUDE = "exclude";
  public final static String LIMIT_QUERY = "query";


  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织Id")
  private String orgId;
  @ApiModelProperty(value = "可售卖数量")
  private Integer qty;
  @ApiModelProperty(value = "是否有库存规则")
  private Boolean hasInvRule;
  @ApiModelProperty(value = "门店信息")
  private RsIdName shop;
  @ApiModelProperty(value = "商品信息")
  private RsIdName sku;

  @ApiModelProperty(value = "库存数量")
  private BigDecimal shopSkuQty;
  @ApiModelProperty(value = "是否有库存")
  private Boolean hasQty;
}
