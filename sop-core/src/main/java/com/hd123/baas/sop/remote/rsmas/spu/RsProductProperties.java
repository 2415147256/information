/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * <p>
 * 项目名：	mas-product-api
 * 文件名：	ProductProperties.java
 * 模块说明：
 * 修改历史：
 * 2019年9月24日 - sulin - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.spu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sulin
 *
 */
@Getter
@Setter
@ApiModel("spu商品销售属性")
public class RsProductProperties {
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "值")
  private List<String> values = new ArrayList<String>();
}
