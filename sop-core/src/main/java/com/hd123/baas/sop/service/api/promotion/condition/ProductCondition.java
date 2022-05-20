package com.hd123.baas.sop.service.api.promotion.condition;

import com.hd123.baas.sop.service.api.promotion.EntityType;
import com.hd123.baas.sop.service.api.promotion.PomEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@ApiModel("促销商品")
public class ProductCondition {
  @ApiModelProperty("商品类型，为空表示全部商品")
  private EntityType entityType;
  @ApiModelProperty("排除促销商品")
  private boolean excludePrm;
  @ApiModelProperty("促销商品冲撞")
  private boolean conflictPrm;
  @ApiModelProperty("商品列表")
  private List<Item> items = new ArrayList<>();
  @ApiModelProperty("排除商品")
  private List<PomEntity> excludeItems = new ArrayList<>();

  @Data
  public static class Item extends PomEntity {
    @ApiModelProperty("促销价")
    private BigDecimal prmPrice;
    @ApiModelProperty("阶梯特价")
    private List<StepPrice> stepPrices;
    @ApiModelProperty("分摊比例")
    private BigDecimal apportionRatio;
    @ApiModelProperty("数量")
    private BigDecimal qty;

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (!(o instanceof Item))
        return false;
      Item item = (Item) o;
      return Objects.equals(getUuid(), item.getUuid()) && Objects.equals(getQpc(), item.getQpc());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getUuid(), getQpc());
    }
  }

  @Data
  public static class StepPrice {
    private BigDecimal stepValue;
    private BigDecimal prmPrice;
  }
}
