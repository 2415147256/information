/**
 *
 */
package com.hd123.baas.sop.remote.rsmas.shopskuinvcrule;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author qyh
 */
@Setter
@Getter
@ApiModel("门店商品库存规则查询条件")
public class RsShopSkuInvRuleFilter extends RsMasFilter {
  private static final long serialVersionUID = 4497174518247471245L;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;
  @ApiModelProperty(value = "type等于")
  private String typeEq;
  @ApiModelProperty(value = "SkuId在...范围内")
  private List<String> skuIdIn;
  @ApiModelProperty(value = "ShopId在...范围内")
  private List<String> shopIdIn;
  @ApiModelProperty(value = "是否启用")
  private Boolean enabledEq;
  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;
}
