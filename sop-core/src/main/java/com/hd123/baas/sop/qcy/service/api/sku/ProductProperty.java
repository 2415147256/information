/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 *
 * 项目名：	mas-client-api
 * 文件名：	Property.java
 * 模块说明：
 * 修改历史：
 * 2019年4月7日 - __Silent - 创建。
 */
package com.hd123.baas.sop.qcy.service.api.sku;

import com.hd123.baas.sop.service.api.basedata.base.Parameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 属性
 *
 * @author sulin
 *
 */
@Getter
@Setter
@ApiModel("商品销售属性")
public class ProductProperty extends Parameter {
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
}
