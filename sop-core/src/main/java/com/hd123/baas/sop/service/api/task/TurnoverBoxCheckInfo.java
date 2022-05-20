package com.hd123.baas.sop.service.api.task;

import com.hd123.baas.sop.remote.rsIwms.RsContainerType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author zhengzewang on 2020/11/5.
 */
@Getter
@Setter
public class TurnoverBoxCheckInfo {

  @ApiModelProperty(value = "容器类型", required = true, example = "{}")
  private RsContainerType containerType;
  private BigDecimal qty;
  private BigDecimal total;
}
