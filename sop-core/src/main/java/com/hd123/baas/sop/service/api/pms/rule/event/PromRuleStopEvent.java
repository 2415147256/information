package com.hd123.baas.sop.service.api.pms.rule.event;

import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromRuleStopEvent {
  private PromRule rule;

}
