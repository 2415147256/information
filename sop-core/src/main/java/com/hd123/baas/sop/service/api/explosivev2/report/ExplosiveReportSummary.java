package com.hd123.baas.sop.service.api.explosivev2.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author liuhaoxin
 * @date 2021-12-13
 */
@Getter
@Setter
public class ExplosiveReportSummary extends ExplosiveSignV2DailyReport {
  /** 报名门店数 */
  private BigDecimal signShopCount;
  /** 报名数 */
  private BigDecimal signQtyTotal;
  /** 订货数 */
  private BigDecimal orderQtyTotal;
  /** 配货数 */
  private BigDecimal shippedQtyTotal;
}
