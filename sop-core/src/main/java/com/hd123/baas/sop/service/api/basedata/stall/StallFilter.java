/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	sop-commons
 * 文件名：	StallFilter.java
  * 模块说明：	
 * 修改历史：

 * 2021年1月4日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.stall;

import com.hd123.baas.sop.service.api.basedata.Filter;

import io.swagger.annotations.ApiModel;
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
@ApiModel(description = "出品部门查询定义")
public class StallFilter extends Filter {
  private static final long serialVersionUID = -20348749960238689L;


  @ApiModelProperty(value = "所属组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "所属组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "出品部门状态等于")
  private Boolean enabledEq;
  @ApiModelProperty(value = "名称、代码类似于")
  private String keyword;
  @ApiModelProperty(value = "ID在...范围内")
  private List<String> idIn;
  @ApiModelProperty(value = "门店id等于")
  private String storeIdEq;

  @ApiModelProperty(value = "门店ID在...范围")
  private List<String> storeIdIn;
}
