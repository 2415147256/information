package com.hd123.baas.sop.service.api.pms.explosive;

import com.hd123.baas.sop.service.api.promotion.PomEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("爆品活动明细")
public class ExplosiveActivityLine {

  @ApiModelProperty("商品")
  private PomEntity entity;
  @ApiModelProperty("促销价")
  private BigDecimal prmPrice;
  @ApiModelProperty("商品到店价")
  private BigDecimal basePrice;
  @ApiModelProperty("每店最大订货量")
  private BigDecimal maxSignQty;
  @ApiModelProperty("每店最小订货量")
  private BigDecimal minSignQty;
  @ApiModelProperty("建议量")
  private BigDecimal suggestQty;
}
