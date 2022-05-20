/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	mas-parent
 * 文件名：	Taste.java
 * 模块说明：
 * 修改历史：
 * 2020/12/22 - lzy - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.collocation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lzy
 */
@Getter
@Setter
@ApiModel("搭配")
public class RsCollocation implements Serializable {

  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "ID")
  private String id;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
  @ApiModelProperty(value = "关联商品id", required = true)
  private String skuId;
  @ApiModelProperty(value = "零售价")
  private BigDecimal price;
  @ApiModelProperty(value = "商品的规格名称")
  private String packingName;
}
