package com.hd123.baas.sop.remote.rsmas.shopskuinvcrule;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 门店商品库存规则
 *
 * @author qyh
 */
@Setter
@Getter
@ApiModel("门店商品库存规则")
public class RsShopSkuInvRule extends RsMasEntity {
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
  @ApiModelProperty(value = "规则名称", example = "测试")
  private String name;
  @ApiModelProperty(value = "规则类型,过滤ERP规则，自动补货规则（期初库存）", example = "filterErpInv")
  private String type;
  @ApiModelProperty(value = "规则条件")
  private RsShopSkuInvRuleCond condition;
  @ApiModelProperty(value = "规则定义内容")
  private RsShopSkuInvRuleDef definition;
  @ApiModelProperty("是否启用")
  private Boolean enabled = true;

}
