package com.hd123.baas.sop.remote.rsh6sop.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简单门店对象
 *
 * @author liuhaoxin
 * @date 2021-11-30
 */
@Data
@ApiModel(description = "简单门店对象")
public class SimpleStore {

  @ApiModelProperty(value = "代码", required = true)
  private String code;
  @ApiModelProperty(value = "GID", required = true)
  private Integer gid;
  @ApiModelProperty(value = " 是否存在虚拟仓字段", required = true)
  private Boolean hasVirtualWrh;
  @ApiModelProperty(value = "名称", required = true)
  private String name;

}
