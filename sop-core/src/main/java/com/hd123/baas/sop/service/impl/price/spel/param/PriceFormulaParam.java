package com.hd123.baas.sop.service.impl.price.spel.param;

import java.math.BigDecimal;

import com.hd123.baas.sop.service.impl.price.spel.FormulaValue;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Getter
@Setter
public class PriceFormulaParam {

  @FormulaValue("到店价")
  private BigDecimal shopPrice;
  @FormulaValue("目标采购价")
  private BigDecimal inPrice;
  @FormulaValue("商品定位加价率")
  private BigDecimal skuPositionIncreaseRate;
  @FormulaValue("价格带加价率")
  private BigDecimal priceRangeIncreaseRate;
  @FormulaValue("商品加价率")
  private BigDecimal skuIncreaseRate;

}
