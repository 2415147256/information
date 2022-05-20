package com.hd123.baas.sop.jmzs.franchise.dao;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.jmzs.franchise.api.Franchise;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryConditionProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessContext;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessException;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicate;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;
import static com.hd123.rumba.commons.jdbc.sql.Predicates.or;

@Repository
public class FranchiseDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(Franchise.class,
      PFranchise.class).addConditionProcessor(new MyConditionProcessor()).build();


  public QueryResult<Franchise> query (String tenant, QueryDefinition qd) {
    Assert.hasText(tenant);
    Assert.notNull(qd);

    if (!StringUtil.isNullOrBlank(tenant)) {
      qd.addByField(Franchise.Queries.TENANT, Cop.EQUALS,tenant);
    }
    SelectStatement statement = QUERY_PROCESSOR.process(qd);
    return executor.query(statement, new FranchiseMapper());
  }

  public static class MyConditionProcessor implements QueryConditionProcessor {
    @Override
    public Predicate process(QueryCondition condition, QueryProcessContext context)
        throws IllegalArgumentException, QueryProcessException {
      if (context == null) {
        return null;
      }
      String alias = context.getPerzAlias();
      if (Franchise.Queries.KEYWORD_LIKE.equals(condition.getOperation())) {
        return or(like(alias, PFranchise.CODE, condition.getParameter()),
            like(alias, PFranchise.NAME, condition.getParameter()));
      }

      return null;
    }
  }

  public List<Franchise> listById(String tenant, List<String> ids) {

    Assert.hasText(tenant);
    Assert.notEmpty(ids);

    SelectBuilder select = new SelectBuilder()
        .from(PFranchise.TABLE_NAME, PFranchise.TABLE_ALIAS)
        .where(Predicates.equals(PFranchise.TENANT, tenant))
        .where(Predicates.in2(PFranchise.ID, ids.toArray()));

    return jdbcTemplate.query(select.build(), new FranchiseMapper());

  }

  public Franchise getByName(String tenant, String orgId, String name) {
    Assert.hasText(tenant);
    Assert.hasText(name);

    SelectBuilder select = new SelectBuilder()
        .from(PFranchise.TABLE_NAME, PFranchise.TABLE_ALIAS)
        .where(Predicates.equals(PFranchise.TENANT, tenant))
        .where(Predicates.equals(PFranchise.ORG_ID, orgId))
        .where(Predicates.equals(PFranchise.NAME, name));

    List<Franchise> list = jdbcTemplate.query(select.build(), new FranchiseMapper());
    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);
  }


  public Franchise get(String tenant, String uuid) {
    return get(tenant, uuid, false);
  }

  public Franchise get(String tenant, String uuid,boolean forUpdate) {

    Assert.hasText(tenant);
    Assert.hasText(uuid);

    SelectBuilder select = new SelectBuilder()
        .from(PFranchise.TABLE_NAME, PFranchise.TABLE_ALIAS)
        .where(Predicates.equals(PFranchise.TENANT, tenant))
        .where(Predicates.equals(PFranchise.UUID, uuid))
        .where(Predicates.equals(PFranchise.DELETED, Boolean.FALSE));

    if (forUpdate) {
      select.forUpdate();
    }

    List<Franchise> list = jdbcTemplate.query(select.build(), new FranchiseMapper());

    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);

  }

  public Franchise get(String tenant, String orgId,String id) {

    Assert.hasText(tenant);
    Assert.hasText(orgId);
    Assert.hasText(id);

    SelectBuilder select = new SelectBuilder()
        .from(PFranchise.TABLE_NAME, PFranchise.TABLE_ALIAS)
        .where(Predicates.equals(PFranchise.TENANT, tenant))
        .where(Predicates.equals(PFranchise.ORG_ID, orgId))
        .where(Predicates.equals(PFranchise.ID, id))
        .where(Predicates.equals(PFranchise.DELETED, Boolean.FALSE));

    List<Franchise> list = jdbcTemplate.query(select.build(), new FranchiseMapper());

    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);

  }

  @Tx
  public void saveNew(Franchise entity) {
    Assert.notNull(entity);

    if (StringUtils.isEmpty(entity.getUuid())) {
      entity.setUuid(IdGenUtils.buildRdUuid());
    }
    if (StringUtils.isEmpty(entity.getId())) {
      entity.setId(IdGenUtils.buildRdUuid());
    }

    InsertStatement insertStatement = new InsertBuilder().table(PFranchise.TABLE_NAME)
        .addValues(PFranchise.toFieldValues(entity))
        .build();

    jdbcTemplate.update(insertStatement);

  }

  @Tx
  public void batchSaveNew(List<Franchise> entities) {
    Assert.notEmpty(entities);

    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);

    for (Franchise entity : entities) {
      if (StringUtils.isEmpty(entity.getUuid())) {
        entity.setUuid(IdGenUtils.buildRdUuid());
      }

      InsertStatement insertStatement  = new InsertBuilder().table(PFranchise.TABLE_NAME)
          .addValues(PFranchise.toFieldValues(entity))
          .build();
      batchUpdater.add(insertStatement);
    }


    batchUpdater.update();

  }

  @Tx
  public void batchUpdate(List<Franchise> entities) {
    Assert.notEmpty(entities);

    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);

    for (Franchise entity : entities) {
      if (StringUtils.isEmpty(entity.getUuid())) {
        entity.setUuid(IdGenUtils.buildRdUuid());
      }
      UpdateStatement updateStatement  = new UpdateBuilder().table(PFranchise.TABLE_NAME)
          .setValues(PFranchise.toFieldValues(entity))
          .where(Predicates.equals(PFranchise.UUID, entity.getUuid()))
          .where(Predicates.equals(PFranchise.TENANT, entity.getTenant()))
          .build();
      batchUpdater.add(updateStatement);
    }


    batchUpdater.update();

  }

  @Tx
  public void update(Franchise entity) {
    Assert.notNull(entity);
    Assert.hasText(entity.getUuid());

    UpdateStatement updateStatement = new UpdateBuilder().table(PFranchise.TABLE_NAME)
        .setValues(PFranchise.toFieldValues(entity))
        .where(Predicates.equals(PFranchise.UUID, entity.getUuid()))
        .where(Predicates.equals(PFranchise.TENANT, entity.getTenant()))
        .build();

    jdbcTemplate.update(updateStatement);
  }

  public void delete(String tenant, String uuid) {
    Assert.hasText(tenant);
    Assert.hasText(uuid);

    DeleteStatement delete = new DeleteBuilder()
        .table(PFranchise.TABLE_NAME)
        .where(Predicates.equals(PFranchise.TENANT, tenant))
        .where(Predicates.equals(PFranchise.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

}
