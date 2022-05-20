/* 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 *
 * 项目名：	com.hd123.baas.sop.biz.basedata.service.api.sku
 * 文件名：	SkuTag
 * 模块说明：
 * 修改历史：
 * 2020/12/8 - 老娜 - 创建。
 */

package com.hd123.baas.sop.service.api.basedata.sku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lina
 */
@Getter
@Setter
@ApiModel(description = "销售商品标签")
public class Tag {
  @ApiModelProperty(value = "代码")
  public String code;
  @ApiModelProperty(value = "名称")
  public String name;
}
