package com.hd123.baas.sop.remote.uas;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增岗位的请求实体
 */
@Setter
@Getter
public class BSaveNewPositionReq implements Serializable {

  @ApiModelProperty(value = "岗位代码", required = true, example = "102223")
  private String code;
  @ApiModelProperty(value = "岗位名称", example = "技术岗")
  private String name;
}
