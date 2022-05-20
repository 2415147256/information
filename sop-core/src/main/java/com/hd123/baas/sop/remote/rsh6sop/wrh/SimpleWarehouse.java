package com.hd123.baas.sop.remote.rsh6sop.wrh;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简单仓位对象
 *
 * @author liuhaoxin
 * @date 2021-11-30
 */
@Data
@ApiModel(description = "简单仓位对象")
public class SimpleWarehouse {
  @ApiModelProperty(value = " 代码")
  private String code;
  @ApiModelProperty(value = " GID")
  private Integer gid;
  @ApiModelProperty(value = " 名称")
  private String name;
}
