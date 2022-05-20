package com.hd123.baas.sop.service.dao.skutag;

import com.alibaba.druid.util.StringUtils;
import com.hd123.baas.sop.service.dao.basedata.PSku;
import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.skutag.SkuTagSummary;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryConditionProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessContext;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessException;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicate;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class SkuTagSummaryDaoBof extends BofBaseDao {
  private static final SkuTagSummaryForQueryMapper MAPPER = new SkuTagSummaryForQueryMapper();
  private static final SkuTagSummaryMapper SKU_TAG_SUMMARY_MAPPER = new SkuTagSummaryMapper();

  public static final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SkuTagSummary.class,
      SkuTagSummary.Schema.class).addConditionProcessor(new MyConditionProcessor()).build();

  public QueryResult<SkuTagSummary> query(String tenant, List<String> orgIds, QueryDefinition qd) {
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    select.from(SkuTagSummary.Schema.TABLE_NAME, "s_");
    select.rightJoin(PSku.TABLE_NAME, "PSku",
        Predicates.and(Predicates.equals("PSku", PSku.TENANT, "s_", SkuTagSummary.Schema.TENANT),
            Predicates.equals("PSku", PSku.ORG_ID, "s_", SkuTagSummary.Schema.ORG_ID),
            Predicates.equals("PSku", PSku.ID, "s_", SkuTagSummary.Schema.SKU_ID)));
    select.where(Predicates.equals("PSku", PSku.TENANT, tenant));
    select.where(Predicates.equals("PSku", PSku.QPC, new BigDecimal("1"))); //默认qpc为1
    if (CollectionUtils.isNotEmpty(orgIds)) {
      select.where(Predicates.in("PSku", PSku.ORG_ID, orgIds.toArray()));
    }
    select.getSelectClause().getFields().clear();
    select.select(
        "s_.uuid as uuid,PSku.tenant as tenant, PSku.orgId as org_id,PSku.id as sku_id,PSku.code as sku_code,PSku.name as sku_name,s_.shop_num as shop_num,PSku.qpc as sku_qpc");
    return executor.query(select, MAPPER);
  }

  public int insert(String tenant, SkuTagSummary summary) {
    Assert.notNull(summary);
    InsertStatement insert = buildInsert(tenant, summary);
    return jdbcTemplate.update(insert);
  }

  private InsertStatement buildInsert(String tenant, SkuTagSummary summary) {
    Assert.notNull(summary.getOrgId(), "组织");
    Assert.notNull(summary.getSkuId(), "商品ID");
    Assert.notNull(summary.getShopNum(), "shopNum");
    if (summary.getUuid() == null) {
      summary.setUuid(UUID.randomUUID().toString());
    }
    return new InsertBuilder().table(SkuTagSummary.Schema.TABLE_NAME)
        .addValue(SkuTagSummary.Schema.TENANT, tenant)
        .addValue(SkuTagSummary.Schema.UUID, summary.getUuid())
        .addValue(SkuTagSummary.Schema.ORG_ID, summary.getOrgId())
        .addValue(SkuTagSummary.Schema.SKU_ID, summary.getSkuId())
        .addValue(SkuTagSummary.Schema.SHOP_NUM, summary.getShopNum())
        .build();
  }

  public void batchInsert(String tenant, List<SkuTagSummary> summaries) {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(summaries);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (SkuTagSummary sum : summaries) {
      InsertStatement insertStatement = buildInsert(tenant, sum);
      updater.add(insertStatement);
    }
    updater.update();
  }

  public int update(String tenant, SkuTagSummary summary) {
    Assert.notNull(summary);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(summary.getUuid(), "uuid");
    Assert.notNull(summary.getSkuId(), "skuId");
    UpdateStatement update = buildUpdate(tenant, summary);
    return jdbcTemplate.update(update);
  }

  public SkuTagSummary get(String tenant, String orgId, String skuId) {
    SelectStatement statement = new SelectBuilder().from(SkuTagSummary.Schema.TABLE_NAME)
        .where(Predicates.equals(SkuTagSummary.Schema.TENANT, tenant))
        .where(Predicates.equals(SkuTagSummary.Schema.ORG_ID, orgId))
        .where(Predicates.equals(SkuTagSummary.Schema.SKU_ID, skuId))
        .build();
    return getFirst(jdbcTemplate.query(statement, SKU_TAG_SUMMARY_MAPPER));
  }

  private UpdateStatement buildUpdate(String tenant, SkuTagSummary summary) {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(summary.getUuid(), "uuid");
    return new UpdateBuilder().table(SkuTagSummary.Schema.TABLE_NAME)
        .setValue(SkuTagSummary.Schema.SHOP_NUM, summary.getShopNum())
        .where(Predicates.equals(SkuTagSummary.Schema.TENANT, tenant))
        .where(Predicates.equals(SkuTagSummary.Schema.UUID, summary.getUuid()))
        .build();
  }

  public void batchUpdate(String tenant, List<SkuTagSummary> summaries) {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(summaries);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (SkuTagSummary summary : summaries) {
      Assert.notNull(summary.getUuid(), "uuid");
      Assert.notNull(summary.getShopNum(), "名称");
      UpdateStatement update = buildUpdate(tenant, summary);
      updater.add(update);
    }
    updater.update();
  }

  public static class MyConditionProcessor implements QueryConditionProcessor {
    @Override
    public Predicate process(QueryCondition condition, QueryProcessContext context)
        throws IllegalArgumentException, QueryProcessException {
      if (condition == null) {
        return null;
      }
      if (StringUtils.equals(condition.getOperation(), SkuTagSummary.Queries.SKU_KEYWORD_LIKE)) {
        return Predicates.or(like("PSku", PSku.CODE, condition.getParameter()),
            like("PSku", PSku.NAME, condition.getParameter()));
      }
      if (StringUtils.equals(condition.getOperation(), SkuTagSummary.Queries.SHOP_NUM_NOT_NULL)) {
        if (condition.getParameter().equals("0")) {
          return Predicates.or(Predicates.isNull("s_." + SkuTagSummary.Schema.SHOP_NUM),
              Predicates.equals("s_." + SkuTagSummary.Schema.SHOP_NUM, 0));
        } else {
          return Predicates.greater("s_." + SkuTagSummary.Schema.SHOP_NUM, 0);
        }
      }
      return null;
    }
  }
}
