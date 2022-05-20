package com.hd123.baas.sop.remote.rsh6sop.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 门店资料查询对象
 * 
 * @author liuhaoxin
 * @date 2021-11-30
 */
@Data
@ApiModel(description = "门店资料查询对象")
public class StoreFilter {
  @ApiModelProperty(value = "组织GID等于")
  private Integer orgGidEquals;
  @ApiModelProperty(value = "类型等于，0-普通门店，1-组织，默认查询门店")
  private Integer typeEquals;
}
