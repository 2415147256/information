package com.hd123.baas.sop.service.api.promotion.execution;

import com.hd123.baas.sop.service.api.promotion.PomEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("赠品优惠")
public class GiftExecution {
  @ApiModelProperty("赠送方式")
  private GiftType giftType = GiftType.one;
  @ApiModelProperty("赠品列表")
  private List<GiftEntity> entities;

  public enum GiftType {
    one, all
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class GiftEntity extends PomEntity {
    @ApiModelProperty(value = "赠品数量", notes = "仅giftType=all时有效")
    private BigDecimal giftQty = BigDecimal.ONE;
    @ApiModelProperty("分摊比例")
    private BigDecimal apportionRatio;
  }
}
