/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop-parent 文件名：	RsShopSkuIdKey.java 模块说明： 修改历史： 2021/8/4 - XLT - 创建。
 */
package com.hd123.baas.sop.qcy.controller.shopsku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author XLT
 */
@Getter
@Setter
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class RsShopSkuIdKey implements Serializable {
  private static final long serialVersionUID = 2128281409108551261L;

  @ApiModelProperty(value = "skuId")
  private String skuId;
  @ApiModelProperty(value = "ShopId")
  private String shopId;
}