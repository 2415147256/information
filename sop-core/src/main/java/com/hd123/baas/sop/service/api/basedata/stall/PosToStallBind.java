/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-openapi-v2
 * 文件名：	PosToStallBind.java
  * 模块说明：	
 * 修改历史：

 * 2021年1月4日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.stall;

import java.io.Serializable;
import java.util.List;

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
@ApiModel("收银机与出品部门绑定关系")
public class PosToStallBind implements Serializable {
  private static final long serialVersionUID = 2266609325828248088L;

  @ApiModelProperty(value = "门店ID", required = true)
  private String storeId;
  @ApiModelProperty(value = "收银机ID", required = true)
  private String posId;
  @ApiModelProperty(value = "出品部门ID列表", required = true)
  private List<String> stallIds;

}
