/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop-parent 文件名：	RsShopSKUTasteGroupLine.java 模块说明： 修改历史： 2021/8/1 - XLT - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.shopsku;

import com.hd123.baas.sop.qcy.controller.shopsku.ShopSkuTasteGroupSave;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XLT
 */
@Getter
@Setter
@ApiModel("修改SKU口味组明细")
public class RsShopSkuTasteGroupLine {

  @ApiModelProperty(value = "门店的id")
  private String shopId;
  @ApiModelProperty(value = "SKUID", required = true)
  private String skuId;
  @ApiModelProperty(value = "口味组id列表")
  private List<ShopSkuTasteGroupSave> tasteGroups = new ArrayList<>();
}