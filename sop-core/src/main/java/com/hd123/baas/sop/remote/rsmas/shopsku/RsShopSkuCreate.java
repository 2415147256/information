/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	HEADING SOP AS SERVICE
 * 文件名：	BShopSkuCreate.java
  * 模块说明：	
 * 修改历史：

 * 2021年1月6日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.shopsku;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author lsz
 */
@Getter
@Setter
public class RsShopSkuCreate {
  @ApiModelProperty(value = "skuId")
  private String skuId;
  @ApiModelProperty(value = "ShopId")
  private String shopId;
}
