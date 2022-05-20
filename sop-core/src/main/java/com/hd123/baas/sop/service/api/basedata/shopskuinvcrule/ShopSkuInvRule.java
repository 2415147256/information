package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 门店商品库存规则
 *
 * @author qyh
 */
@Setter
@Getter
@ApiModel("门店商品库存规则")
public class ShopSkuInvRule extends RsMasEntity {
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
  @ApiModelProperty(value = "规则名称")
  private String name;
  @ApiModelProperty(value = "规则数量")
  private Integer qty;
  @ApiModelProperty(value = "门店与商品范围信息")
  private List<ShopSkuRange> shopSkuRange;
  @ApiModelProperty(value = "开始生效时间")
  private Date startEffectTime;

}
