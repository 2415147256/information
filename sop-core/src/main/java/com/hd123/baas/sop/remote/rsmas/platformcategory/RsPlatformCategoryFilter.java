/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsPlatformCategoryFilter.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月6日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.platformcategory;

import java.util.List;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
@ApiModel("平台类目查询条件")
public class RsPlatformCategoryFilter extends RsMasFilter {
  private static final long serialVersionUID = 1654230374215178518L;

  @ApiModelProperty("组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty("组织id等于")
  private String orgIdEq;
  @ApiModelProperty("类型等于")
  private String typeEq;
  @ApiModelProperty("平台id等于")
  private String platformIdEq;
  @ApiModelProperty("名称等于")
  private String nameEq;
  @ApiModelProperty("名称在...范围内")
  private List<String> nameIn;
  @ApiModelProperty(value = "上级类目等于")
  private String upperIdEq;
  @ApiModelProperty(value = "路径起始于")
  private String pathStartWith;
  @ApiModelProperty(value = "code在...范围内")
  private List<String> codeIn;
  @ApiModelProperty("是否启用")
  private Boolean enabledEq;
  @ApiModelProperty("是否显示")
  private Boolean isShowEq;
}
