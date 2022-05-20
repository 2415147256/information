/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-user-api
 * 文件名：	EmployeeFilter.java
  * 模块说明：	
 * 修改历史：

 * 2019年9月3日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.employee;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 
 * @author lsz
 */
@Setter
@Getter
public class RsEmployeeFilter extends RsMasFilter {
  private static final long serialVersionUID = -5291312211240069688L;

  @ApiModelProperty(value = "组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "用户ID等于")
  private String userIdEq;
  @ApiModelProperty(value = "代码类似于")
  private String codeLike;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;
  @ApiModelProperty(value = "上级员工ID等于")
  private String upperIdEq;
  @ApiModelProperty(value = "上级员工在之中")
  private List<String> upperIdIn;
  @ApiModelProperty(value = "ID在...范围内")
  private List<String> idIn;
  @ApiModelProperty(value = "ID不在...范围内")
  private List<String> idNotIn;
  @ApiModelProperty(value = "是否已启用等于")
  private Boolean enableEq;
  @ApiModelProperty(value = "名称账户类似于")
  private String accountOrNameLike;

  @ApiModelProperty(value = "门店ID等于")
  private String storeIdEq;
}
