/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 *
 * 项目名：	mas-company-api
 * 文件名：	StallPos.java
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
import java.util.List;

/**
 * 收银机与出品部门绑定关系
 *
 * @author hezhenhui
 */
@Getter
@Setter
@ApiModel("收银机与出品部门绑定关系")
public class RsPosToStallBind implements Serializable {
  private static final long serialVersionUID = 2493563186829725676L;

  @ApiModelProperty(value = "门店ID", required = true)
  private String storeId;
  @ApiModelProperty(value = "收银机ID", required = true)
  private String posId;
  @ApiModelProperty(value = "出品部门ID", required = true)
  private List<String> stallIds;
}
