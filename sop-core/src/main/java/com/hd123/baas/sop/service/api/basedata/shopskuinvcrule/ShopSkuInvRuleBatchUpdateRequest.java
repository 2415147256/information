package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 门店商品库存规则
 *
 * @author qyh
 */
@Setter
@Getter
@ApiModel("门店商品库存规则-ShopSkuInvRuleBatchUpdateRequest")
public class ShopSkuInvRuleBatchUpdateRequest extends RsMasEntity {
  private static final long serialVersionUID = -4181450075262828397L;

  public final static String TYPE_FILTER_ERP_INV = "filterErpInv";
  public static final String TYPE_FIXE_QTY_INV = "fixedShopSkuQty";

  public final static String LIMIT_INCLUDE = "include";
  public final static String LIMIT_EXCLUDE = "exclude";
  public final static String LIMIT_QUERY = "query";

  @ApiModelProperty(value = "规则数量")
  private int qty;
  @ApiModelProperty(value = "sku的ID列表")
  private List<String> skuIds = new ArrayList<String>();
  @ApiModelProperty(value = "门店的ID列表")
  private List<String> shopIds = new ArrayList<String>();
  @ApiModelProperty(value = "开始生效时间")
  private Date startEffectTime;

  @ApiModelProperty(value = "是否全部门店")
  private Boolean allShop = false;
  @ApiModelProperty(value = "是否全部商品")
  private Boolean allSku = false;
}
