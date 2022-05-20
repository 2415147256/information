package com.hd123.baas.sop.remote.uas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
@ApiModel("组织")
public class BOrg {
  @ApiModelProperty(value = "id", example = "1000000")
  private String id;
  @ApiModelProperty(value = "code", example = "12330")
  private String code;
  @ApiModelProperty(value = "名称", example = "杭州分公司")
  private String name;
}
