package com.hd123.baas.sop.service.api.promotion.execution;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("δΌζ ιε")
public class ExecutionSet {
  private GeneralExecution generalExecution;
  private GiftExecution giftExecution;
  private SpecialPriceExecution specialPriceExecution;
  private TimePeriodExecution timePeriodExecution;
  private MultiPriceExecution multiPriceExecution;
}
