package com.hd123.baas.sop.service.api.price.pricepromotion;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class PricePromotionGroupRule {
  PricePromotionLineType type;
  private String rule;
  private String skuGroup;
  private String skuGroupName;
}
