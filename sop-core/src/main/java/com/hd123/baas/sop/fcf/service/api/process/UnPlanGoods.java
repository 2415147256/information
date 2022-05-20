package com.hd123.baas.sop.fcf.service.api.process;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author zhangweigang
 */
@Getter
@Setter
public class UnPlanGoods {
  private static final long serialVersionUID = 5620804755670269205L;
  @ApiModelProperty(value = "规格商品主键", example = "10001949")
  private String skuId;
  @ApiModelProperty(value = "商品名称， 应当提供别名(FreshGoods.aliasName)", example = "关东煮萝卜")
  private String productName;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UnPlanGoods that = (UnPlanGoods) o;
    return Objects.equals(skuId, that.skuId) && Objects.equals(productName, that.productName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(skuId, productName);
  }
}
