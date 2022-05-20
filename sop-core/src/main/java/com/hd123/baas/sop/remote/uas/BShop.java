package com.hd123.baas.sop.remote.uas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BShop {
  @ApiModelProperty(value = "门店id", example = "1233222")
  private String id;
  @ApiModelProperty(value = "门店代码", required = true, example = "102223")
  private String code;
  @ApiModelProperty(value = "门店名称", example = "技术岗")
  private String name;
}
