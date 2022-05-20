package com.hd123.baas.sop.service.api.basedata.department;

import com.hd123.baas.sop.service.api.basedata.Filter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@ApiModel(description = "部门查询定义")
public class DepartmentFilter extends Filter {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "代码名称类似于")
  private String keyword;
  @ApiModelProperty(value = "名称类等于")
  private String nameEq;
  @ApiModelProperty("id在...范围之内")
  private List<String> idIn;
}
