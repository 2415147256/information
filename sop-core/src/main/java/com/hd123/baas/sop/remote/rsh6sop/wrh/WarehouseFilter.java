package com.hd123.baas.sop.remote.rsh6sop.wrh;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 仓位资料查询对象
 *
 * @author liuhaoxin
 * @date 2021-11-30
 */
@Data
public class WarehouseFilter {
  /** 查询条件定义 */
  public static final String ORG_GID_EQ = "orgGid:=";

  @ApiModelProperty(value = "组织GID等于")
  private Integer orgGidEquals;

}
