package com.hd123.baas.sop.service.impl.price.spel.param;

import com.hd123.baas.sop.service.impl.price.spel.FormulaValue;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
public class PriceSkuBasePriceFormulaParam {
  @FormulaValue("商品到店价")
  private BigDecimal skuBasePrice;
}
