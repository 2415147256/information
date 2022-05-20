package com.hd123.baas.sop.service.dao.group;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.entity.SkuGroupPositionGradeConfig;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

@Repository
public class SkuGroupPositionGradeConfigDaoBof extends BofBaseDao {
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SkuGroupPositionGradeConfig.class,
      PSkuGroupPositionGradeConfig.class).build();

  public void batchInsert(String tenant, List<SkuGroupPositionGradeConfig> configs) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(configs);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (SkuGroupPositionGradeConfig config : configs) {
      InsertStatement insert = new InsertBuilder().table(PSkuGroupPositionGradeConfig.TABLE_NAME)
          .addValue(PSkuGroupPositionGradeConfig.TENANT, tenant)
          .addValue(PSkuGroupPositionGradeConfig.ORG_ID, config.getOrgId())
          .addValue(PSkuGroupPositionGradeConfig.UUID, config.getUuid())
          .addValue(PSkuGroupPositionGradeConfig.SKU_GROUP_ID, config.getSkuGroupId())
          .addValue(PSkuGroupPositionGradeConfig.PRICE_POSITION_ID, config.getPricePositionId())
          .addValue(PSkuGroupPositionGradeConfig.PRICE_GRADE_JSON, config.getPriceGradeJson())
          .build();
      updater.add(insert);
    }
    updater.update();

  }

  public QueryResult<SkuGroupPositionGradeConfig> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(SkuGroupPositionGradeConfig.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new SkuGroupPositionGradeConfigMapper());
  }

  public List<SkuGroupPositionGradeConfig> queryBySkuGroupId(String tenant, String skuGroupId) {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuGroupPositionGradeConfig.Queries.SKU_GROUP_ID, Cop.EQUALS, skuGroupId);
    QueryResult<SkuGroupPositionGradeConfig> query = query(tenant, qd);
    return query.getRecords();
  }

  public List<SkuGroupPositionGradeConfig> queryBySkuGroupIds(String tenant, Collection<Integer> skuGroupIds) {
    QueryDefinition qd = new QueryDefinition();
    if(CollectionUtils.isNotEmpty(skuGroupIds)){
      qd.addByField(SkuGroupPositionGradeConfig.Queries.SKU_GROUP_ID, Cop.IN, skuGroupIds);
    }
    QueryResult<SkuGroupPositionGradeConfig> query = query(tenant, qd);
    return query.getRecords();
  }

  public void delete(String tenant, String skuGroupId) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(skuGroupId, "notNull");
    DeleteStatement delete = new DeleteBuilder().table(PSkuGroupPositionGradeConfig.TABLE_NAME)
            .where(Predicates.equals(PSkuGroupPositionGradeConfig.TENANT, tenant))
            .where(Predicates.equals(PSkuGroupPositionGradeConfig.SKU_GROUP_ID, skuGroupId))
            .build();
    jdbcTemplate.update(delete);
  }

  public void batchDelete(String tenant, Collection<Integer> skuGroupIds) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(skuGroupIds, "skuGroupIds");
    DeleteStatement delete = new DeleteBuilder().table(PSkuGroupPositionGradeConfig.TABLE_NAME)
            .where(Predicates.equals(PSkuGroupPositionGradeConfig.TENANT, tenant))
            .where(Predicates.in2(PSkuGroupPositionGradeConfig.SKU_GROUP_ID, skuGroupIds.toArray()))
            .build();
    jdbcTemplate.update(delete);
  }

}
