/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	HEADING SOP AS SERVICE
 * 文件名：	RsShopSkuFilter.java
  * 模块说明：	
 * 修改历史：

 * 2021年1月6日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.shopsku;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 
 * @author lsz
 */
@Getter
@Setter
@ApiModel("门店商品查询条件")
public class RsShopSkuFilter extends RsMasFilter {
  private static final long serialVersionUID = -5087305915068790565L;

  @ApiModelProperty(value = "关键字等于，sku的名称/ID/编码/条码 的模糊查询")
  private String keyword;

  @ApiModelProperty(value = "门店ID等于")
  private String shopIdEq;
  @ApiModelProperty(value = "shopId在...范围")
  private List<String> shopIdIn;
  @ApiModelProperty(value = "商品SKU的Id等于")
  private String skuIdEq;
  @ApiModelProperty(value = "商品SKU的ID在...范围内")
  private List<String> skuIdIn;

  @ApiModelProperty(value = "是否上架")
  private Boolean enabledEq;

  @ApiModelProperty(value = "商品条码等于")
  private String skuInputCodeCodeEq;
  @ApiModelProperty(value = "商品条码在...范围内")
  private List<String> skuInputCodeCodeIn;

  @ApiModelProperty(value = "sku类型等于")
  private String skuTypeEq;
  @ApiModelProperty(value = "sku销售方式等于")
  private String skuSaleTypeEq;
  @ApiModelProperty(value = "经营方式等于")
  private String skuBusinessTypeEq;
  @ApiModelProperty(value = "配料组id等于")
  private String collocationGroupIdEq;
  @ApiModelProperty(value = "pk在..范围内")
  private List<String> pkIn;
  @ApiModelProperty(value = "组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;
}
