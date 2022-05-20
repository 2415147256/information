package com.hd123.baas.sop.service.dao.explosivev2.report;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveReportSummary;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveSignV2DailyReport;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryConditionProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.Expr;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.and;

/**
 * 爆品活动(ExplosiveSignV2DailyReport)表数据库访问层
 *
 * @author liuhaoxin
 * @since 2021-12-07 18:13:10
 */
@Repository
public class ExplosiveSignV2DailyReportDaoBof extends BofBaseDao {

  private static final TEMapper<ExplosiveSignV2DailyReport> MAPPER = TEMapperBuilder
      .of(ExplosiveSignV2DailyReport.class, ExplosiveSignV2DailyReport.Schema.class)
      .primaryKey(ExplosiveSignV2DailyReport.Schema.UUID)
      .build();

  public static final ReportSummaryMapper REPORT_SUMMARY_MAPPER = new ReportSummaryMapper();

  private QueryConditionProcessor getKeywordConditionProcessor() {
    return (condition, context) -> {
      if (condition == null) {
        return null;
      }
      if (!StringUtils.equalsIgnoreCase(ExplosiveSignV2DailyReport.Queries.SHOP_KEYWORD_LIKE, condition.getOperation())) {
        return null;
      }
      String alias = context.getPerzAlias();
      String keyword = (String) condition.getParameter();

      SelectStatement selectStatement = new SelectBuilder().select(ExplosiveSignV2DailyReport.Schema.UUID)
          .from(context.getDatabase(), ExplosiveSignV2DailyReport.Schema.TABLE_NAME, ExplosiveSignV2DailyReport.Schema.TABLE_ALIAS)
          .where(Predicates.equals(getTableField(ExplosiveSignV2DailyReport.Schema.TABLE_ALIAS, ExplosiveSignV2DailyReport.Schema.UUID),
              Expr.valueOf(getTableField(alias, ExplosiveSignV2DailyReport.Schema.UUID))))
          .where(Predicates.or(Predicates.like(ExplosiveSignV2DailyReport.Schema.TABLE_ALIAS, ExplosiveSignV2DailyReport.Schema.SHOP_NAME, keyword),
              Predicates.like(ExplosiveSignV2DailyReport.Schema.TABLE_ALIAS, ExplosiveSignV2DailyReport.Schema.SHOP_CODE, keyword)))
          .build();
      return and(Predicates.in(alias, ExplosiveSignV2DailyReport.Schema.UUID, selectStatement));
    };
  }

  private QueryConditionProcessor getBusinessDateConditionProcessor() {
    return (condition, context) -> {
      if (condition == null) {
        return null;
      }
      if (!StringUtils.equalsIgnoreCase(ExplosiveSignV2DailyReport.Queries.BUSINESS_DATE_BTW, condition.getOperation())) {
        return null;
      }
      List<Object> parameters = condition.getParameters();
      return Predicates.and(Predicates.greaterOrEquals(ExplosiveSignV2DailyReport.Schema.BUSINESS_DATE, parameters.get(0)),
          Predicates.lessOrEquals(ExplosiveSignV2DailyReport.Schema.BUSINESS_DATE, parameters.get(1)));
    };
  }

  private String getTableField(String tableAlias, String field) {
    return tableAlias + "." + field;
  }

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ExplosiveSignV2DailyReport.class,
      ExplosiveSignV2DailyReport.Schema.class)
      .addConditionProcessor(getKeywordConditionProcessor())
      .addConditionProcessor(getBusinessDateConditionProcessor()).build();

  public void insert(String tenant, ExplosiveSignV2DailyReport report, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(report, "report");

    report.setTenant(tenant);
    report.setCreated(operateInfo.getTime());
    report.setLastmodified(operateInfo.getTime());
    InsertStatement insert = new InsertBuilder().table(ExplosiveSignV2DailyReport.Schema.TABLE_NAME)
        .values(MAPPER.forInsert(report))
        .build();
    jdbcTemplate.update(insert);
  }

  public void batchInsert(String tenant, List<ExplosiveSignV2DailyReport> dailyReports, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(dailyReports, "report");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveSignV2DailyReport dailyReport : dailyReports) {
      dailyReport.setTenant(tenant);
      dailyReport.setCreated(operateInfo.getTime());
      dailyReport.setLastmodified(operateInfo.getTime());
      InsertStatement insert = new InsertBuilder().table(ExplosiveSignV2DailyReport.Schema.TABLE_NAME)
          .values(MAPPER.forInsert(dailyReport))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public void batchUpdate(String tenant, List<ExplosiveSignV2DailyReport> dailyReports, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(dailyReports, "report");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveSignV2DailyReport dailyReport : dailyReports) {
      dailyReport.setTenant(tenant);
      dailyReport.setLastmodified(operateInfo.getTime());
      UpdateStatement update = new UpdateBuilder().table(ExplosiveSignV2DailyReport.Schema.TABLE_NAME)
          .setValues(MAPPER.forUpdate(dailyReport, true))
          .where(Predicates.equals(ExplosiveSignV2DailyReport.Schema.UUID, dailyReport.getUuid()))
          .where(Predicates.equals(ExplosiveSignV2DailyReport.Schema.TENANT, tenant))
          .build();
      updater.add(update);
    }
    updater.update();
  }

  public void batchUpdateQty(String tenant, List<ExplosiveSignV2DailyReport> dailyReports, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(dailyReports, "report");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveSignV2DailyReport dailyReport : dailyReports) {
      dailyReport.setTenant(tenant);
      UpdateStatement update = new UpdateBuilder().table(ExplosiveSignV2DailyReport.Schema.TABLE_NAME)
          .setValue(ExplosiveSignV2DailyReport.Schema.LASTMODIFIED, operateInfo.getTime())
          .setValue(ExplosiveSignV2DailyReport.Schema.SIGN_QTY, dailyReport.getSignQty())
          .setValue(ExplosiveSignV2DailyReport.Schema.ORDER_QTY,
              Expr.valueOf(
                  ExplosiveSignV2DailyReport.Schema.ORDER_QTY.concat("+").concat(dailyReport.getOrderQty().toString())))
          .setValue(ExplosiveSignV2DailyReport.Schema.SHIPPED_QTY,
              Expr.valueOf(ExplosiveSignV2DailyReport.Schema.SHIPPED_QTY.concat("+")
                  .concat(dailyReport.getShippedQty().toString())))
          .where(Predicates.equals(ExplosiveSignV2DailyReport.Schema.UUID, dailyReport.getUuid()))
          .where(Predicates.equals(ExplosiveSignV2DailyReport.Schema.TENANT, tenant))
          .build();
      updater.add(update);
    }
    updater.update();
  }

  public QueryResult<ExplosiveSignV2DailyReport> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(ExplosiveSignV2DailyReport.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, MAPPER);
  }

  public List<ExplosiveSignV2DailyReport> list(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(ExplosiveSignV2DailyReport.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return jdbcTemplate.query(select, MAPPER);
  }

  public QueryResult<ExplosiveReportSummary> queryGroupByDate(String tenant, QueryDefinition qd) {

    /*
    SELECT *,COUNT(shop_id)signShopCount,SUM(sign_qty) signQtyTotal,
    SUM(order_qty) orderQtyTotal,SUM(shipped_qty)shippedQtyTotal)
    FROM explosive_sign_v2_daily_report
    WHERE explosive_id = '?'
    and sku_id = '?'
    GROUP BY business_date;
     */
    // 商品进行分组
    qd.addByField(ExplosiveSignV2DailyReport.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    select.select("COUNT(shop_id)signShopCount," +
        "SUM(sign_qty) signQtyTotal," +
        "SUM(order_qty) orderQtyTotal," +
        "SUM(shipped_qty)shippedQtyTotal");
    select.groupBy(ExplosiveSignV2DailyReport.Schema.BUSINESS_DATE);
    return executor.query(select, REPORT_SUMMARY_MAPPER);
  }

  public QueryResult<ExplosiveReportSummary> queryGroupBySku(String tenant, QueryDefinition qd) {

    /*
    SELECT *,COUNT(DISTINCT shop_id)signShopCount,SUM(sign_qty) signQtyTotal,
    SUM(order_qty) orderQtyTotal,SUM(shipped_qty)shippedQtyTotal)
    FROM explosive_sign_v2_daily_report
    WHERE explosive_id = '?'
    GROUP BY sku_id;
     */
    // 商品进行分组
    qd.addByField(ExplosiveSignV2DailyReport.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    select.select("COUNT(DISTINCT shop_id)signShopCount," +
        "SUM(sign_qty) signQtyTotal," +
        "SUM(order_qty) orderQtyTotal," +
        "SUM(shipped_qty)shippedQtyTotal");
    select.groupBy(ExplosiveSignV2DailyReport.Schema.SKU_ID);
    return executor.query(select, REPORT_SUMMARY_MAPPER);
  }

  public QueryResult<ExplosiveReportSummary> queryGroupByShop(String tenant, QueryDefinition qd) {
      /*
    SELECT *,SUM(sign_qty),SUM(order_qty),SUM(shipped_qty)
    FROM explosive_sign_v2_daily_report
    WHERE explosive_id = '?'
    GROUP BY shop_id, sku_id;
     */
    // 商品进行分组
    qd.addByField(ExplosiveSignV2DailyReport.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    select.select("COUNT(shop_id)signShopCount," +
        "SUM(sign_qty) signQtyTotal," +
        "SUM(order_qty) orderQtyTotal," +
        "SUM(shipped_qty)shippedQtyTotal");
    select.groupBy(ExplosiveSignV2DailyReport.Schema.SHOP_ID, ExplosiveSignV2DailyReport.Schema.SKU_ID);
    return executor.query(select, REPORT_SUMMARY_MAPPER);
  }
}

