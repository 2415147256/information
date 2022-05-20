package com.hd123.baas.sop.jmzs.franchise.dao;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.jmzs.franchise.api.FranchiseShopAssignment;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FranchiseShopAssignmentDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(FranchiseShopAssignment.class,
      PFranchiseShopAssignment.class).build();


  public QueryResult<FranchiseShopAssignment> query (String tenant, QueryDefinition qd) {
    Assert.hasText(tenant);
    Assert.notNull(qd);

    if (!StringUtil.isNullOrBlank(tenant)) {
      qd.addByField(FranchiseShopAssignment.Queries.TENANT, Cop.EQUALS,tenant);
    }
    SelectStatement statement = QUERY_PROCESSOR.process(qd);
    return executor.query(statement, new FranchiseShopAssignmentMapper());
  }

  public List<FranchiseShopAssignment> listByFranchiseUuid(String tenant, String uuid) {

    Assert.hasText(tenant);
    Assert.hasText(uuid);

    SelectStatement statement = new SelectBuilder()
        .from(PFranchiseShopAssignment.TABLE_NAME, PFranchiseShopAssignment.TABLE_ALIAS)
        .where(Predicates.equals(PFranchiseShopAssignment.TENANT, tenant))
        .where(Predicates.equals(PFranchiseShopAssignment.FRANCHISE_UUID, uuid)).build();
    return jdbcTemplate.query(statement, new FranchiseShopAssignmentMapper());
  }
  public List<FranchiseShopAssignment> listByFranchiseUuids(String tenant, List<String> uuids) {

    Assert.hasText(tenant);
    Assert.notEmpty(uuids);

    SelectStatement statement = new SelectBuilder()
        .from(PFranchiseShopAssignment.TABLE_NAME, PFranchiseShopAssignment.TABLE_ALIAS)
        .where(Predicates.equals(PFranchiseShopAssignment.TENANT, tenant))
        .where(Predicates.in2(PFranchiseShopAssignment.FRANCHISE_UUID, uuids.toArray())).build();
    return jdbcTemplate.query(statement, new FranchiseShopAssignmentMapper());
  }

  public void deleteByFranchiseUuid(String tenant, String uuid) {

    Assert.hasText(tenant);
    Assert.hasText(uuid);

    DeleteStatement statement = new DeleteBuilder()
        .table(PFranchiseShopAssignment.TABLE_NAME)
        .where(Predicates.equals(PFranchiseShopAssignment.TENANT, tenant))
        .where(Predicates.equals(PFranchiseShopAssignment.FRANCHISE_UUID, uuid)).build();

     jdbcTemplate.update(statement);
  }

  public void deleteByFranchiseUuids(String tenant, List<String> uuids) {

    Assert.hasText(tenant);
    Assert.notEmpty(uuids);

    DeleteStatement statement = new DeleteBuilder()
        .table(PFranchiseShopAssignment.TABLE_NAME)
        .where(Predicates.equals(PFranchiseShopAssignment.TENANT, tenant))
        .where(Predicates.in2(PFranchiseShopAssignment.FRANCHISE_UUID, uuids.toArray())).build();

    jdbcTemplate.update(statement);
  }

  @Tx
  public void batchSave(List<FranchiseShopAssignment> entities) {
    Assert.notEmpty(entities);

    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);

    for (FranchiseShopAssignment entity : entities) {
      if (StringUtils.isEmpty(entity.getUuid())) {
        entity.setUuid(IdGenUtils.buildRdUuid());
      }
      InsertStatement insertStatement  = new InsertBuilder().table(PFranchiseShopAssignment.TABLE_NAME)
          .addValues(PFranchiseShopAssignment.toFieldValues(entity))
          .build();
      batchUpdater.add(insertStatement);
    }


    batchUpdater.update();

  }
}
