package com.hd123.baas.sop.remote.rsIwms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class RsIwmsBaseRes<T> {
  @ApiModelProperty(value = "模板值")
  private T data;
  @ApiModelProperty(value = "是否成功")
  private Boolean success;
}
