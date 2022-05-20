package com.hd123.baas.sop.service.impl.price.priceadjustment.calculate;

import java.util.List;

import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseRate;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Getter
@Setter
public class SkuIncreaseRate {

  private String skuId;
  private List<PriceIncreaseRate> increaseRates;

}
