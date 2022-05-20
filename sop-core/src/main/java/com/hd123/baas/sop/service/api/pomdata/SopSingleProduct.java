package com.hd123.baas.sop.service.api.pomdata;

import com.hd123.spms.service.bill.SingleProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SopSingleProduct extends SingleProduct {
  @ApiModelProperty("周期促销标识")
  private boolean timeCycle;
}
