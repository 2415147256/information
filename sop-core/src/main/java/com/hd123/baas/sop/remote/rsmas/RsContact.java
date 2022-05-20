/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * 
 * 项目名：	mas-company-api
 * 文件名：	RsContact.java
 * 模块说明：	
 * 修改历史：
 * 2019年9月3日 - sulin - 创建。
 */
package com.hd123.baas.sop.remote.rsmas;

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
@ApiModel("联系信息")
public class RsContact {
  @ApiModelProperty("联系人名称")
  private String name;
  @ApiModelProperty("联系人电话")
  private String telephone;
  @ApiModelProperty("联系人手机")
  private String mobile;
  @ApiModelProperty("联系人邮箱")
  private String email;
  @ApiModelProperty("联系人传真")
  private String fax;
}
