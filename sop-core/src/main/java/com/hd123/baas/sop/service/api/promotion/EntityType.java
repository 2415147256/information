package com.hd123.baas.sop.service.api.promotion;

import io.swagger.annotations.ApiModelProperty;

/**
 * POM数据实体类型。
 *
 * @author chenwenfeng
 */
public enum EntityType {
  @ApiModelProperty("商品")
  product,
  @ApiModelProperty("类别")
  category,
}
