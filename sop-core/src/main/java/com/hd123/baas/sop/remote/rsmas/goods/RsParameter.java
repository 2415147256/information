/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * 
 * 项目名：	mas-product-api
 * 文件名：	RsParameter.java
 * 模块说明：	
 * 修改历史：
 * Sep 17, 2019 - sulin - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sulin
 *
 */
@Getter
@Setter
@ApiModel("商品参数")
public class RsParameter {
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "值")
  private String value;

  public RsParameter(String name, String value) {
    this.name = name;
    this.value = value;
  }
}
