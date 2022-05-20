package com.hd123.baas.sop.remote.rsIwms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Getter
@Setter
public class RsQueryStoreOutContainerItem implements Serializable {

  @ApiModelProperty(value = "容器类型")
  private RsContainerType containerType;
  @ApiModelProperty(value = "对应⽇期发货的数量", example = "1.00")
  private BigDecimal returnQty;
  @ApiModelProperty(value = "⻔店总⽋的待还数量", example = "2.00")
  private BigDecimal unReturnQty;
}
