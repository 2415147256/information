package com.hd123.baas.sop.service.api.pomdata;

import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.service.api.promotion.condition.ProductCondition;

import java.util.List;

public interface PromSingleProductService {
  String generalZip(String tenant, int spanDays) throws Exception;

  String generalZip(PromRule target, ProductCondition productCondition, PromotionJoinUnits joinUnits, int spanDays) throws Exception;

  List<PrmPriceQueryRequest.ProductLine> queryPrmPrice(String tenant, PrmPriceQueryRequest request) throws Exception;
}
