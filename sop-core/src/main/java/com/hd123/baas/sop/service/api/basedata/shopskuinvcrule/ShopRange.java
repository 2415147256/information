/**
 *
 */
package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qyh
 */
@Getter
@Setter
@ApiModel("门店范围")
public class ShopRange {
  @ApiModelProperty(value = "是否限制")
  private boolean limit = false;
  @ApiModelProperty(value = "限制类型，存在的值有include,exclude,query")
  private String limitType;
  @ApiModelProperty(value = "门店信息")
  private List<RsIdName> shops = new ArrayList<RsIdName>();
}
