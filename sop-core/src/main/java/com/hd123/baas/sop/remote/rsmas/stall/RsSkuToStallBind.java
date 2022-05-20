package com.hd123.baas.sop.remote.rsmas.stall;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * sku与出品部门绑定关系
 *
 * @author lins
 */
@Getter
@Setter
@ApiModel("sku与出品部门绑定关系")
public class RsSkuToStallBind implements Serializable {
  private static final long serialVersionUID = -2962787818259174172L;

  @ApiModelProperty(value = "出品部门ID", required = true)
  private String stallId;
  @ApiModelProperty(value = "商品id列表", required = true)
  private List<String> skuIdList;
}
