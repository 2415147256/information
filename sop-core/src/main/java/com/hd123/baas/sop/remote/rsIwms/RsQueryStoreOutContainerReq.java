package com.hd123.baas.sop.remote.rsIwms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Getter
@Setter
public class RsQueryStoreOutContainerReq extends RsIwmsBaseReq {
  @ApiModelProperty(value = "⻔店查询的发货⽇期，按照 'yyyy-MM-dd' 格式化的⽇期", example = "2020-02-02", required = true)
  private String sendDate;

}
