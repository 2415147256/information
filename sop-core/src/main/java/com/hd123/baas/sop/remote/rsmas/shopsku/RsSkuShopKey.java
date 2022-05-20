/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-openapi-v2
 * 文件名：	IdShopId.java
  * 模块说明：	
 * 修改历史：

 * 2021年1月6日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.shopsku;

import java.io.Serializable;

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
@ApiModel
public class RsSkuShopKey implements Serializable {

  @ApiModelProperty(value = "ID")
  private String id;
  @ApiModelProperty(value = "ShopId")
  private String shopId;

}
