/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-user-api
 * 文件名：	EmployeeDepartment.java
  * 模块说明：	
 * 修改历史：

 * 2019年9月3日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.employee;

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
public class RsEmployeeDepartment {

  @ApiModelProperty(value = "组织类型", required = true)
  private String orgType;
  @ApiModelProperty(value = "组织ID", required = true)
  private String orgId;
  @ApiModelProperty(value = "ID", required = true)
  private String id;
  @ApiModelProperty(value = "代码")
  private String code;
  @ApiModelProperty(value = "名称")
  private String name;

}
