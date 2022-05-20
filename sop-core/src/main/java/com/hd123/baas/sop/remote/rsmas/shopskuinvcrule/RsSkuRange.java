/**
 *
 */
package com.hd123.baas.sop.remote.rsmas.shopskuinvcrule;

import com.hd123.baas.sop.remote.rsmas.RsIdCodeName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qyh
 */
@Getter
@Setter
@ApiModel("商品范围")
public class RsSkuRange implements Serializable {
  private static final long serialVersionUID = -8127574536900514051L;
  public static final String LIMIT_TYPE_INCLUDE = "include";
  public static final String LIMIT_TYPE_EXCLUDE = "exclude";
  public static final String LIMIT_TYPE_QUERY = "query";

  @ApiModelProperty(value = "是否限制")
  private boolean limit = false;
  @ApiModelProperty(value = "限制类型，存在的值有include,exclude,query")
  private String limitType;
  @ApiModelProperty(value = "商品信息")
  private List<RsIdCodeName> skus = new ArrayList<RsIdCodeName>();
}
