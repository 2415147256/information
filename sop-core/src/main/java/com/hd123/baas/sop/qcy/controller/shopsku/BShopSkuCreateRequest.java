/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	HEADING SOP AS SERVICE
 * 文件名：	BShopSkuCreate.java
  * 模块说明：	
 * 修改历史：

 * 2021年1月6日 - lsz - 创建。
 */
package com.hd123.baas.sop.qcy.controller.shopsku;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lsz
 */
@Getter
@Setter
public class BShopSkuCreateRequest {

  @ApiModelProperty(value = "门店ID列表")
  private List<String> shopIds = new ArrayList<>();
  @ApiModelProperty(value = "商品ID列表")
  private List<String> skuIds = new ArrayList<>();
  @ApiModelProperty(value = "是否全部门店")
  private Boolean allShop = false;
  @ApiModelProperty(value = "是否全部商品")
  private Boolean allSku = false;
}
