package com.hd123.baas.sop.service.api.basedata.employee;

import com.hd123.baas.sop.service.api.basedata.Filter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lina
 */
@Setter
@Getter
@ApiModel(description = "员工查询定义")
public class EmployeeFilter extends Filter {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "门店ID等于")
  private String storeIdEq;
}
