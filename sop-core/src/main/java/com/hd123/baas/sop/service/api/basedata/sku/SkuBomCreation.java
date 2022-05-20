/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * 
 * 项目名：	sop-commons
 * 文件名：	SkuBomCreation.java
 * 模块说明：	
 * 修改历史：
 * 2020年11月22日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.sku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
@ApiModel(description = "销售商品转换关系新建信息")
public class SkuBomCreation {
  @ApiModelProperty(value = "ERP商品GID")
  public String goodsGid;
  @ApiModelProperty(value = "SKUID")
  public String skuId;
  @ApiModelProperty(value = "转换关系")
  public String bom;
}
