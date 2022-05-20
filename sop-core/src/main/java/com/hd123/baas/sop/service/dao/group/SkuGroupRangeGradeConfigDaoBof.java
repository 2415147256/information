package com.hd123.baas.sop.service.dao.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.entity.SkuGroupRangeGradeConfig;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

@Repository
public class SkuGroupRangeGradeConfigDaoBof extends BofBaseDao {
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SkuGroupRangeGradeConfig.class,
      PSkuGroupRangeGradeConfig.class).build();

  public QueryResult<SkuGroupRangeGradeConfig> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(SkuGroupRangeGradeConfig.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new SkuGroupRangeGradeConfigMapper());
  }

  public List<SkuGroupRangeGradeConfig> queryByGroupId(String tenant, String skuGroupId) {
    Assert.notNull(skuGroupId, "skuGroupId");
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuGroupRangeGradeConfig.Queries.SKU_GROUP_ID, Cop.EQUALS, skuGroupId);
    QueryResult<SkuGroupRangeGradeConfig> query = query(tenant, qd);
    return query.getRecords();
  }

  public void updateByUniqueKey(String tenant, SkuGroupRangeGradeConfig config) {
    UpdateStatement statement = new UpdateBuilder().table(PSkuGroupRangeGradeConfig.TABLE_NAME)
        .setValue(PSkuGroupRangeGradeConfig.PRICE_GRADE_JSON, config.getPriceGradeJson())
        .where(Predicates.equals(PSkuGroupRangeGradeConfig.TENANT, tenant))
        .where(Predicates.equals(PSkuGroupRangeGradeConfig.SKU_GROUP_ID, config.getSkuGroupId()))
        .where(Predicates.equals(PSkuGroupRangeGradeConfig.PRICE_RANGE_ID, config.getPriceRangeId()))
        .build();
    jdbcTemplate.update(statement);
  }

  public void updateByUniqueKey(String tenant, List<SkuGroupRangeGradeConfig> configs) {
    List<UpdateStatement> statements = new ArrayList<>();
    for (SkuGroupRangeGradeConfig config : configs) {
      UpdateStatement statement = new UpdateBuilder().table(PSkuGroupRangeGradeConfig.TABLE_NAME)
          .setValue(PSkuGroupRangeGradeConfig.PRICE_GRADE_JSON, config.getPriceGradeJson())
          .where(Predicates.equals(PSkuGroupRangeGradeConfig.TENANT, tenant))
          .where(Predicates.equals(PSkuGroupRangeGradeConfig.SKU_GROUP_ID, config.getSkuGroupId()))
          .where(Predicates.equals(PSkuGroupRangeGradeConfig.PRICE_RANGE_ID, config.getPriceRangeId()))
          .build();
      statements.add(statement);
    }

    batchUpdate(statements);
  }

  public void delete(String tenant, String skuGroupId) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(skuGroupId, "notNull");
    DeleteStatement delete = new DeleteBuilder().table(PSkuGroupRangeGradeConfig.TABLE_NAME)
        .where(Predicates.equals(PSkuGroupRangeGradeConfig.TENANT, tenant))
        .where(Predicates.equals(PSkuGroupRangeGradeConfig.SKU_GROUP_ID, skuGroupId))
        .build();
    jdbcTemplate.update(delete);
  }

  public void batchDelete(String tenant, Collection<Integer> skuGroupIds) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(skuGroupIds, "skuGroupIds");
    DeleteStatement delete = new DeleteBuilder().table(PSkuGroupRangeGradeConfig.TABLE_NAME)
        .where(Predicates.equals(PSkuGroupRangeGradeConfig.TENANT, tenant))
        .where(Predicates.in2(PSkuGroupRangeGradeConfig.SKU_GROUP_ID, skuGroupIds.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  public void batchInsert(String tenant, List<SkuGroupRangeGradeConfig> configs) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(configs);
    Assert.notEmpty(configs);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (SkuGroupRangeGradeConfig config : configs) {
      InsertStatement insert = new InsertBuilder().table(PSkuGroupRangeGradeConfig.TABLE_NAME)
          .addValue(PSkuGroupRangeGradeConfig.TENANT, tenant)
          .addValue(PSkuGroupRangeGradeConfig.ORG_ID, config.getOrgId())
          .addValue(PSkuGroupRangeGradeConfig.UUID, config.getUuid())
          .addValue(PSkuGroupRangeGradeConfig.SKU_GROUP_ID, config.getSkuGroupId())
          .addValue(PSkuGroupRangeGradeConfig.PRICE_RANGE_ID, config.getPriceRangeId())
          .addValue(PSkuGroupRangeGradeConfig.PRICE_GRADE_JSON, config.getPriceGradeJson())
          .build();
      updater.add(insert);
    }
    updater.update();
  }

}
