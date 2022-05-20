package com.hd123.baas.sop.service.dao.sku.publishplan;

import java.util.List;

import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlan;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.jdbc.qd.QueryConditionProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessContext;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessException;
import com.hd123.rumba.commons.jdbc.sql.Predicate;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanLine;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
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

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;
import static com.hd123.rumba.commons.jdbc.sql.Predicates.or;

/**
 * 商品上下架方案行(SkuPublishPlanLine)表数据库访问层
 *
 * @author liuhaoxin
 * @since 2021-11-24 18:38:33
 */
@Repository
public class SkuPublishPlanLineDaoBof extends BofBaseDao {

  private static final TEMapper<SkuPublishPlanLine> MAPPER = TEMapperBuilder
      .of(SkuPublishPlanLine.class, SkuPublishPlanLine.Schema.class)
      .map("ext", SkuPublishPlan.Schema.EXT, new ObjectNodeToExtConver(), new ExtToObjectNodeConver())
      .primaryKey(SkuPublishPlanLine.Schema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SkuPublishPlanLine.class,
      SkuPublishPlanLine.Schema.class).addConditionProcessor(new MyConditionProcessor()).build();

  public class MyConditionProcessor implements QueryConditionProcessor {
    @Override
    public Predicate process(QueryCondition condition, QueryProcessContext context) throws IllegalArgumentException, QueryProcessException {
      if (context == null) {
        return null;
      }
      String alias = context.getPerzAlias();
      if (SkuPublishPlanLine.Queries.KEYWORD_LIKE.equals(condition.getOperation())) {
        return or(like(alias, SkuPublishPlanLine.Schema.SKU_NAME, condition.getParameter()),
            Predicates.like(alias, SkuPublishPlanLine.Schema.SKU_CODE, condition.getParameter()),
            Predicates.like(alias, SkuPublishPlanLine.Schema.SKU_PY_CODE, condition.getParameter()));
      }
      return null;
    }
  }

  public void batchInsert(String tenant, List<SkuPublishPlanLine> skuPublishPlanLines) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(skuPublishPlanLines, "skuPublishPlanLines");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (SkuPublishPlanLine line : skuPublishPlanLines) {
      line.setTenant(tenant);
      InsertStatement insert = new InsertBuilder().table(SkuPublishPlanLine.Schema.TABLE_NAME)
          .addValues(MAPPER.forInsert(line, true))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public void deleteByOwner(String tenant, String owner) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");

    DeleteStatement deleteStatement = new DeleteBuilder().table(SkuPublishPlanLine.Schema.TABLE_NAME)
        .where(Predicates.equals(SkuPublishPlanLine.Schema.TENANT, tenant))
        .where(Predicates.equals(SkuPublishPlanLine.Schema.OWNER, owner))
        .build();
    jdbcTemplate.update(deleteStatement);
  }

  public void update(String tenant, SkuPublishPlanLine skuPublishPlanLine) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(skuPublishPlanLine, "skuPublishPlanLine");

    UpdateStatement update = new UpdateBuilder().table(SkuPublishPlanLine.Schema.TABLE_NAME)
        .setValues(MAPPER.forInsert(skuPublishPlanLine, true))
        .build();
    jdbcTemplate.update(update);
  }


  public QueryResult<SkuPublishPlanLine> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(SkuPublishPlanLine.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, MAPPER);
  }

  public List<SkuPublishPlanLine> listByOwner(String tenant, String owner) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(owner, "owner");

    SelectStatement select = new SelectBuilder().from(SkuPublishPlanLine.Schema.TABLE_NAME)
        .where(Predicates.equals(SkuPublishPlanLine.Schema.TENANT, tenant),
            Predicates.equals(SkuPublishPlanLine.Schema.OWNER, owner))
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }
}
