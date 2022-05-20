/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	sop-commons
 * 文件名：	SkuInputCode.java
  * 模块说明：	
 * 修改历史：

 * 2021年1月4日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.sku;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author lsz
 */
@Getter
@Setter
@ApiModel("Sku输入码")
public class RsSkuInputCode {
  @ApiModelProperty(value = "类型", required = true)
  private String type;
  @ApiModelProperty(value = "代码", required = true)
  private String code;
  @ApiModelProperty(value = "规格", required = false)
  private BigDecimal qpc;
  @ApiModelProperty(value = "规格说明", required = false)
  private String qpcStr;
  @ApiModelProperty(value = "单位", required = false)
  private String unit;
  @ApiModelProperty(value = "重量")
  private BigDecimal weight;
}
