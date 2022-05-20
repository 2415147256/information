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

/**
 * @author lzy
 */
@Getter
@Setter
@ApiModel("搭配")
public class RsCollocationGroupShopSkuBindLine implements Serializable {

  private static final long serialVersionUID = 704322217709482700L;
  @ApiModelProperty(value = "门店ID")
  private String shopId;
  @ApiModelProperty(value = "SKUID")
  private String skuId;
}
