/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop-parent 文件名：	RsShopSkuTasteGroup.java 模块说明： 修改历史： 2021/8/2 - XLT - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.shopsku;

import com.hd123.baas.sop.remote.rsmas.tastegroup.RsTasteGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author XLT
 */
@Getter
@Setter
@ApiModel("门店商品口味组")
public class RsShopSkuTasteGroup {
  @ApiModelProperty(value = "口味组")
  private RsTasteGroup tasteGroup;
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
}