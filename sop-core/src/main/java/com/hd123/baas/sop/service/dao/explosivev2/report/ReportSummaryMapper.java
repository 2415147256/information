package com.hd123.baas.sop.service.dao.explosivev2.report;

import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveReportSummary;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveSignV2DailyReport;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author guyahui
 * @date 2021/4/22 19:16
 */
public class ReportSummaryMapper implements RowMapper<ExplosiveReportSummary> {
  @Override
  public ExplosiveReportSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
    ExplosiveReportSummary reportSummary = new ExplosiveReportSummary();

    reportSummary.setUuid(rs.getString(ExplosiveSignV2DailyReport.Schema.UUID));
    reportSummary.setOrgId(rs.getString(ExplosiveSignV2DailyReport.Schema.ORG_ID));

    reportSummary.setShopId(rs.getString(ExplosiveSignV2DailyReport.Schema.SHOP_ID));
    reportSummary.setShopCode(rs.getString(ExplosiveSignV2DailyReport.Schema.SHOP_CODE));
    reportSummary.setShopName(rs.getString(ExplosiveSignV2DailyReport.Schema.SHOP_NAME));

    reportSummary.setSkuId(rs.getString(ExplosiveSignV2DailyReport.Schema.SKU_ID));
    reportSummary.setSkuGid(rs.getString(ExplosiveSignV2DailyReport.Schema.SKU_GID));
    reportSummary.setSkuCode(rs.getString(ExplosiveSignV2DailyReport.Schema.SKU_CODE));
    reportSummary.setSkuName(rs.getString(ExplosiveSignV2DailyReport.Schema.SKU_NAME));
    reportSummary.setSkuQpc(rs.getBigDecimal(ExplosiveSignV2DailyReport.Schema.SKU_QPC));
    reportSummary.setSkuUnit(rs.getString(ExplosiveSignV2DailyReport.Schema.SKU_UNIT));

    reportSummary.setBusinessDate(rs.getDate(ExplosiveSignV2DailyReport.Schema.BUSINESS_DATE));

    reportSummary.setLimitQty(rs.getBigDecimal(ExplosiveSignV2DailyReport.Schema.LIMIT_QTY));
    reportSummary.setMinQty(rs.getBigDecimal(ExplosiveSignV2DailyReport.Schema.MIN_QTY));
    reportSummary.setInPrice(rs.getBigDecimal(ExplosiveSignV2DailyReport.Schema.IN_PRICE));
    reportSummary.setRemark(rs.getString(ExplosiveSignV2DailyReport.Schema.REMARK));

    reportSummary.setExplosiveId(rs.getString(ExplosiveSignV2DailyReport.Schema.EXPLOSIVE_ID));
    reportSummary.setExplosiveName(rs.getString(ExplosiveSignV2DailyReport.Schema.EXPLOSIVE_NAME));

    reportSummary.setSignShopCount(rs.getBigDecimal("signShopCount"));
    reportSummary.setSignQtyTotal(rs.getBigDecimal("signQtyTotal"));
    reportSummary.setOrderQtyTotal(rs.getBigDecimal("orderQtyTotal"));
    reportSummary.setShippedQtyTotal(rs.getBigDecimal("shippedQtyTotal"));
    return reportSummary;
  }
}
