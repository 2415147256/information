package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录客户查询条件定义")
public class CatCustomerFilter extends Filter {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;

  @ApiModelProperty("客户ID等于")
  private String customerIdEq;
  @ApiModelProperty("客户ID在列表")
  private List<String> customerIdIn;

  public CatCustomerFilter() {
  }

  public String getCustomerIdEq() {
    return this.customerIdEq;
  }

  public List<String> getCustomerIdIn() {
    return this.customerIdIn;
  }

  public void setCustomerIdEq(String customerIdEq) {
    this.customerIdEq = customerIdEq;
  }

  public void setCustomerIdIn(List<String> customerIdIn) {
    this.customerIdIn = customerIdIn;
  }
}
