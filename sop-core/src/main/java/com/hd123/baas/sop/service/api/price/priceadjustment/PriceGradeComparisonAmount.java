package com.hd123.baas.sop.service.api.price.priceadjustment;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/10.
 */
@Getter
@Setter
public class PriceGradeComparisonAmount {

  private PriceGrade grade;
  private BigDecimal amount;
  private BigDecimal comparisonAmount;

}
