package com.hd123.baas.sop.service.api.price.priceadjustment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/10.
 */
@Getter
@Setter
public class PriceGradeSalePrice {

  private PriceGrade grade;
  private BigDecimal price;

  public static PriceGradeSalePrice findPriceGrade(String gradeId, List<PriceGradeSalePrice> priceGrades) {
    if (priceGrades == null) {
      return null;
    }
    for (PriceGradeSalePrice priceGrade : priceGrades) {
      if (priceGrade.getGrade() != null && Objects.equals(priceGrade.getGrade().getId(), gradeId)) {
        return priceGrade;
      }
    }
    return null;
  }

}
