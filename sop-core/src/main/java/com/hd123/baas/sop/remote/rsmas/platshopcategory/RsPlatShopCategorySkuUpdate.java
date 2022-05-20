/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop 文件名：	RsPlatShopCategorySkuUpdate.java 模块说明： 修改历史： 2021/11/28 - XLT - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.platshopcategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author XLT
 */
@Getter
@Setter
@ApiModel
public class RsPlatShopCategorySkuUpdate {

  @ApiModelProperty("门店id")
  private String shopId;
  @ApiModelProperty("平台id")
  private String platformId;
  @ApiModelProperty("平台门店类目ID")
  private String platShopCategoryId;
  @ApiModelProperty("商品SKUID")
  private String skuId;
  // 更新值
  @ApiModelProperty("排序值")
  private Integer sort;

}