package com.hd123.baas.sop.service.api.promotion.execution;

import com.hd123.baas.sop.service.api.promotion.PomEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 组合商品价格
 *
 * @author wly
 */
@Data
@ApiModel("组合商品优惠")
public class MultiPriceExecution {

  @ApiModelProperty("商品列表")
  private List<MultiPriceEntity> items;

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class MultiPriceEntity extends PomEntity {
    @ApiModelProperty("促销价")
    private BigDecimal prmPrice;
  }
}
