package com.hd123.baas.sop.service.api.task;

import java.math.BigDecimal;

import com.hd123.baas.sop.service.api.TenantStandardEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignableShopTaskSummary extends TenantStandardEntity {

  private String shop;
  private String shopCode;
  private String shopName;
  private Long finished;
  private Long total;
  private BigDecimal point;
  private BigDecimal score;
  private BigDecimal rate;
  private Long rank;
  private Long rowNum;
  private BigDecimal PreRate;
}
