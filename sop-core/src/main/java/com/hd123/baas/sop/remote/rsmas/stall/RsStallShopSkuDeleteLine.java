/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * 
 * 项目名：	mas-company-api
 * 文件名：	Stall.java
 * 模块说明：	
 * 修改历史：
 * 2020年12月23日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.stall;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 移除出品部门与门店商品关系请求明细
 * 
 * @author lina
 */
@Getter
@Setter
@ApiModel("移除出品部门与门店商品关系请求明细")
public class RsStallShopSkuDeleteLine implements Serializable {
  private static final long serialVersionUID = 4950902637441645880L;

  @ApiModelProperty(value = "出品部门ID", required = true)
  private String stallId;
  @ApiModelProperty(value = "SKU的ID", required = true)
  private String skuId;
}
