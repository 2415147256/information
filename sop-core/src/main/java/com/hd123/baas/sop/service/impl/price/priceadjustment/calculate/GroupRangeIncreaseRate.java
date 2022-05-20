package com.hd123.baas.sop.service.impl.price.priceadjustment.calculate;

import java.math.BigDecimal;
import java.util.List;

import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseRate;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Getter
@Setter
public class GroupRangeIncreaseRate {

  private String priceRange;
  private BigDecimal amount;
  private List<PriceIncreaseRate> increaseRates;

}
