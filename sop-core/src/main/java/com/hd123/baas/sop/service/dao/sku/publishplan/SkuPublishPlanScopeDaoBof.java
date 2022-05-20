package com.hd123.baas.sop.service.dao.sku.publishplan;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanScope;
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
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;

/**
 * 商品上下架方案范围(SkuPublishPlanScope)表数据库访问层
 *
 * @author liuhaoxin
 * @since 2021-11-25 11:51:08
 */
@Repository
public class SkuPublishPlanScopeDaoBof extends BofBaseDao {

  private static final TEMapper<SkuPublishPlanScope> MAPPER = TEMapperBuilder
      .of(SkuPublishPlanScope.class, SkuPublishPlanScope.Schema.class)
      .primaryKey(SkuPublishPlanScope.Schema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SkuPublishPlanScope.class,
      SkuPublishPlanScope.Schema.class).build();

  public void batchInsert(String tenant, List<SkuPublishPlanScope> skuPublishPlanScopes) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(skuPublishPlanScopes, "skuPublishPlanScopes");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (SkuPublishPlanScope scope : skuPublishPlanScopes) {
      scope.setTenant(tenant);
      InsertStatement insert = new InsertBuilder().table(SkuPublishPlanScope.Schema.TABLE_NAME)
          .addValues(MAPPER.forInsert(scope, true))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public void deleteByOwner(String tenant, String owner) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");

    DeleteStatement deleteStatement = new DeleteBuilder().table(SkuPublishPlanScope.Schema.TABLE_NAME)
        .where(Predicates.equals(SkuPublishPlanScope.Schema.TENANT, tenant))
        .where(Predicates.equals(SkuPublishPlanScope.Schema.OWNER, owner))
        .build();
    jdbcTemplate.update(deleteStatement);
  }

  public QueryResult<SkuPublishPlanScope> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(SkuPublishPlanScope.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, MAPPER);
  }

  public List<SkuPublishPlanScope> listByOwners(String tenant, List<String> owners) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(owners, "owners");

    SelectStatement select = new SelectBuilder().from(SkuPublishPlanScope.Schema.TABLE_NAME)
        .where(Predicates.equals(SkuPublishPlanScope.Schema.TENANT, tenant),
            Predicates.in2(SkuPublishPlanScope.Schema.OWNER, owners.toArray()))
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }
}
