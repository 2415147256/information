package com.hd123.baas.sop.evcall.exector.goodsprm;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.service.api.promotion.condition.ProductCondition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsPrmPriceGeneralEvent extends AbstractTenantEvCallMessage {

  /** 指定待计算的日期 */
  @NotNull
  @ApiModelProperty("生成日期")
  private Date executeDate;
  @NotNull
  @ApiModelProperty("组织ID")
  private String orgId;

  private PromRule targetRule;
  private ProductCondition productCondition;
  private PromotionJoinUnits joinUnits;
}
