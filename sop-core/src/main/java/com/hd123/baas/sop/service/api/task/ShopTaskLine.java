package com.hd123.baas.sop.service.api.task;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 导出明细和单店单表时使用
 */
@Getter
@Setter
public class ShopTaskLine extends TenantStandardEntity {

  private String shop;
  private String shopCode;
  private String shopName;
  private String planCode;
  private String planName;
  private String planPeriod;
  private String groupName;
  private String operatorName;
  private String operatorId;
  private String itemName;
  private String note;
  private BigDecimal cutPoint;
  private BigDecimal point;
  private BigDecimal score;
  private String feedback;

}
