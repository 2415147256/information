package com.hd123.baas.sop.service.api.promotion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PromotionType {
  price("促销价"),
  priceDiscount("促销折扣"),
  segmentPrice("阶梯特价"),
  fullReduce("普通满减"),
  preReduce("每满减"),
  stepReduce("阶梯满减"),
  gdDiscount("单品折扣"),
  discount("普通折扣"),
  clearDiscount("清仓促销"),
  groupDiscount("组合折扣"),
  groupPrice("组合特价"),
  groupGift("组合满赠"),
  stepDiscount("阶梯折扣"),
  gdGift("单品买赠"),
  gift("整单满赠"),
  gdSpecialPrice("单品换购"),
  specialPrice("满额换购");

  String caption;
}
