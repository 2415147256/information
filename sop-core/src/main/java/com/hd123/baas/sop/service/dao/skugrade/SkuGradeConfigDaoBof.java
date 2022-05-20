package com.hd123.baas.sop.service.dao.skugrade;
/*
 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 
 项目名：	com.hd123.baas.sop.service.dao.skugrade
 文件名：	SkuGradeConfigDao.java
 模块说明：	
 修改历史：
 2021年02月26日 - wangdanhua - 创建。
 */

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.entity.SkuGradeConfig;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;

/**
 * @author wangdanhua
 */
@Repository
public class SkuGradeConfigDaoBof extends BofBaseDao {
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SkuGradeConfig.class, PSkuGradeConfig.class)
      .build();

  @Autowired
  private JdbcPagingQueryExecutor<SkuGradeConfig> executor;

  public SkuGradeConfig get(String tenant, String orgId, String skuId) {
    SelectStatement select = new SelectBuilder().from(PSkuGradeConfig.TABLE_NAME)
        .where(Predicates.equals(PSkuGradeConfig.SKU_ID, skuId))
        .where(Predicates.equals(PSkuGradeConfig.ORG_ID, orgId))
        .where(Predicates.equals(PSkuGradeConfig.TENANT, tenant))
        .build();
    return getFirst(jdbcTemplate.query(select, new SkuGradeConfigMapper()));
  }

  public List<SkuGradeConfig> list(String tenant, String orgId) {
    SelectStatement select = new SelectBuilder().from(PSkuGradeConfig.TABLE_NAME)
        .where(Predicates.equals(PSkuGradeConfig.TENANT, tenant))
        .where(Predicates.equals(PSkuGradeConfig.ORG_ID, orgId))
        .build();
    return jdbcTemplate.query(select, new SkuGradeConfigMapper());
  }

  public void insert(String tenant, SkuGradeConfig skuGradeConfig) {
    InsertStatement insert = new InsertBuilder().table(PSkuGradeConfig.TABLE_NAME)
        .addValues(PSkuGradeConfig.getBizMap(tenant, skuGradeConfig))
        .build();
    jdbcTemplate.update(insert);
  }

  public void batchInsert(String tenant, List<SkuGradeConfig> skuGradeConfigs) {
    List<AbstractStatement> statements = new ArrayList<>();
    for (SkuGradeConfig skuGradeConfig : skuGradeConfigs) {
      InsertStatement insert = new InsertBuilder().table(PSkuGradeConfig.TABLE_NAME)
          .addValues(PSkuGradeConfig.getBizMap(tenant, skuGradeConfig))
          .build();
      statements.add(insert);
    }
    batchUpdate(statements);
  }

  public void batchUpsert(String tenant, List<SkuGradeConfig> skuGradeConfigs) {
    List<AbstractStatement> statements = new ArrayList<>();
    for (SkuGradeConfig skuGradeConfig : skuGradeConfigs) {
      UpsertStatement statement = new UpsertBuilder().table(PSkuGradeConfig.TABLE_NAME)
          .addValues(PSkuGradeConfig.getBizMap(tenant, skuGradeConfig))
          .keys(PSkuGradeConfig.TENANT, PSkuGradeConfig.ORG_ID, PSkuGradeConfig.SKU_ID)
          .build();
      statements.add(statement);
    }
    batchUpdate(statements);
  }

  public void update(String tenant, SkuGradeConfig skuGradeConfig) {
    UpdateStatement update = new UpdateBuilder().table(PSkuGradeConfig.TABLE_NAME)
        .setValues(PSkuGradeConfig.getBizMap(tenant, skuGradeConfig))
        .where(Predicates.equals(PSkuGradeConfig.TENANT, tenant))
        .where(Predicates.equals(PSkuGradeConfig.UUID, skuGradeConfig.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public void updateByUniqueKey(String tenant, SkuGradeConfig config) {
    UpdateStatement update = new UpdateBuilder().table(PSkuGradeConfig.TABLE_NAME)
        .setValue(PSkuGradeConfig.PRICE_GRADE_JSON, config.getPriceGradeJson())
        .where(Predicates.equals(PSkuGradeConfig.TENANT, tenant))
        .where(Predicates.equals(PSkuGradeConfig.ORG_ID, config.getOrgId()))
        .where(Predicates.equals(PSkuGradeConfig.SKU_ID, config.getSkuId()))
        .build();
    jdbcTemplate.update(update);
  }

  public void updateByUniqueKey(String tenant, List<SkuGradeConfig> configs) {
    List<UpdateStatement> statements = new ArrayList<>();
    for (SkuGradeConfig config : configs) {
      UpdateStatement update = new UpdateBuilder().table(PSkuGradeConfig.TABLE_NAME)
          .setValue(PSkuGradeConfig.PRICE_GRADE_JSON, config.getPriceGradeJson())
          .where(Predicates.equals(PSkuGradeConfig.TENANT, tenant))
          .where(Predicates.equals(PSkuGradeConfig.ORG_ID, config.getOrgId()))
          .where(Predicates.equals(PSkuGradeConfig.SKU_ID, config.getSkuId()))
          .build();
      statements.add(update);
    }

    batchUpdate(statements);
  }

  public void remove(String tenant, Integer uuid) {
    DeleteStatement delete = new DeleteBuilder().table(PSkuGradeConfig.TABLE_NAME)
        .where(Predicates.equals(PSkuGradeConfig.TENANT, tenant))
        .where(Predicates.equals(PSkuGradeConfig.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

  public QueryResult<SkuGradeConfig> query(String tenant, QueryDefinition qd) {
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    select.where(Predicates.equals(PSkuGradeConfig.TENANT, tenant));
    return executor.query(select, new SkuGradeConfigMapper());
  }

}
