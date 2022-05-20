package com.hd123.baas.sop.service.api.skumgr;

import com.hd123.rumba.commons.biz.entity.Entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class Directory extends Entity {
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "是默认目录")
  private boolean def = false;

  private String orgId;
}
