/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 *
 * 项目名：	mas-cms-api
 * 文件名：	PlatformCategorySkuFilter.java
  * 模块说明：
 * 修改历史：

 * 2019年9月19日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.platshopcategory;

import com.hd123.baas.sop.service.api.basedata.Filter;
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
@ApiModel("门店平台类目商品查询条件")
public class PlatShopCategorySkuFilter extends Filter {
  private static final long serialVersionUID = 1477519009562007658L;

  @ApiModelProperty("平台门店类目ID等于")
  private String platShopCategoryIdEq;

  @ApiModelProperty(value = "关键字等于，sku的name/编码/条码 的模糊查询")
  private String keyword;

  @ApiModelProperty(value = "sku售卖状态")
  private String saleStatusEq;

  @ApiModelProperty("商品SKUID在之中")
  private List<String> skuIdIn;
  @ApiModelProperty("门店id在...范围")
  private List<String> shopIdIn;
}
