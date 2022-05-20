/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： H6GoodsAlcQpc.java
 * 模块说明：
 * 修改历史：
 * 2021年02月01日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.goods;

/**
 * @author huangjunxian
 * @since 1.0
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(description = "商品配货规格")
public class H6GoodsAlcQpc {
  @ApiModelProperty(value = "商品标识", example = "1000000", required = true)
  private String goodsId;
  @ApiModelProperty(value = "默认配货规格", example = "1.0000", required = true)
  private BigDecimal qpc;
  @ApiModelProperty(value = "规格说明", example = "1*1", required = true)
  private String qpcStr;
  @ApiModelProperty(value = "单位")
  private String munit;
  @ApiModelProperty(value = "最小规格单位", example = "盒", required = true)
  private String minMunit;
}
