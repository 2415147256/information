package com.hd123.baas.sop.service.impl.pomdata.event;

import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.service.api.promotion.condition.ProductCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromRuleGeneralBillEvent {
  private PromRule rule;
  private ProductCondition productCondition;
  private PromotionJoinUnits joinUnits;
}
