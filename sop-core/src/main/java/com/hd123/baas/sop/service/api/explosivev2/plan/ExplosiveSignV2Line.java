package com.hd123.baas.sop.service.api.explosivev2.plan;

import com.hd123.baas.sop.service.api.promotion.PomEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("爆品活动明细")
public class ExplosiveSignV2Line {

  @ApiModelProperty("商品")
  private PomEntity entity;
  @ApiModelProperty("订货价")
  private BigDecimal ordPrice;
  @ApiModelProperty("报名数")
  private BigDecimal signUpQty;

}
