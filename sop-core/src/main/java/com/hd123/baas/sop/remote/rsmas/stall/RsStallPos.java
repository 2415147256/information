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

/**
 * 出品部门与收银机关联关系
 *
 * @author hezhenhui
 */
@Getter
@Setter
@ApiModel("出品部门与收银机关联关系")
public class RsStallPos implements Serializable {
  private static final long serialVersionUID = 9023192267850330138L;

  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "门店ID", required = true)
  private String storeId;
  @ApiModelProperty(value = "收银机ID", required = true)
  private String posId;
  @ApiModelProperty(value = "出品部门ID", required = true)
  private String stallId;

}
