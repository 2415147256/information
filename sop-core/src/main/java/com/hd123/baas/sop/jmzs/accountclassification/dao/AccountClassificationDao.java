package com.hd123.baas.sop.jmzs.accountclassification.dao;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.jmzs.accountclassification.api.AccountClassification;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
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
public class AccountClassificationDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(AccountClassification.class,
      PAccountClassification.class).addConditionProcessor(new MyConditionProcessor()).build();


  public QueryResult<AccountClassification> query (String tenant, QueryDefinition qd) {
    Assert.hasText(tenant);
    Assert.notNull(qd);

    if (!StringUtil.isNullOrBlank(tenant)) {
      qd.addByField(AccountClassification.Queries.TENANT, Cop.EQUALS,tenant);
    }
    SelectStatement statement = QUERY_PROCESSOR.process(qd);
    return executor.query(statement, new AccountClassificationMapper());
  }

  public static class MyConditionProcessor implements QueryConditionProcessor {
    @Override
    public Predicate process(QueryCondition condition, QueryProcessContext context)
        throws IllegalArgumentException, QueryProcessException {
      if (context == null) {
        return null;
      }
      String alias = context.getPerzAlias();
      if (AccountClassification.Queries.KEYWORD_LIKE.equals(condition.getOperation())) {
        return or(like(alias, PAccountClassification.CODE, condition.getParameter()),
            like(alias, PAccountClassification.NAME, condition.getParameter()));
      }

      return null;
    }
  }

  public AccountClassification getByName(String tenant, String orgId, String name) {
    Assert.hasText(tenant);
    Assert.hasText(name);

    SelectBuilder select = new SelectBuilder()
        .from(PAccountClassification.TABLE_NAME, PAccountClassification.TABLE_ALIAS)
        .where(Predicates.equals(PAccountClassification.TENANT, tenant))
        .where(Predicates.equals(PAccountClassification.ORG_ID, orgId))
        .where(Predicates.equals(PAccountClassification.NAME, name));

    List<AccountClassification> list = jdbcTemplate.query(select.build(), new AccountClassificationMapper());
    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);
  }

  public AccountClassification get(String tenant, String uuid) {
    return get(tenant, uuid, false);
  }

  public AccountClassification get(String tenant, String uuid,boolean forUpdate) {

    Assert.hasText(tenant);
    Assert.hasText(uuid);

    SelectBuilder select = new SelectBuilder()
        .from(PAccountClassification.TABLE_NAME, PAccountClassification.TABLE_ALIAS)
        .where(Predicates.equals(PAccountClassification.TENANT, tenant))
        .where(Predicates.equals(PAccountClassification.UUID, uuid));

    if (forUpdate) {
      select.forUpdate();
    }

    List<AccountClassification> list = jdbcTemplate.query(select.build(), new AccountClassificationMapper());

    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);

  }

  @Tx
  public void saveNew(AccountClassification entity) {
    Assert.notNull(entity);

    if (StringUtils.isEmpty(entity.getUuid())) {
      entity.setUuid(IdGenUtils.buildRdUuid());
    }

    InsertStatement insertStatement = new InsertBuilder().table(PAccountClassification.TABLE_NAME)
        .addValues(PAccountClassification.toFieldValues(entity))
        .build();

    jdbcTemplate.update(insertStatement);

  }

  @Tx
  public void update(AccountClassification entity) {
    Assert.notNull(entity);
    Assert.hasText(entity.getUuid());

    UpdateStatement updateStatement = new UpdateBuilder().table(PAccountClassification.TABLE_NAME)
        .setValues(PAccountClassification.toFieldValues(entity))
        .where(Predicates.equals(PAccountClassification.UUID, entity.getUuid()))
        .where(Predicates.equals(PAccountClassification.TENANT, entity.getTenant()))
        .build();

    jdbcTemplate.update(updateStatement);
  }

  public void delete(String tenant, String uuid) {
    Assert.hasText(tenant);
    Assert.hasText(uuid);

    DeleteStatement delete = new DeleteBuilder()
        .table(PAccountClassification.TABLE_NAME)
        .where(Predicates.equals(PAccountClassification.TENANT, tenant))
        .where(Predicates.equals(PAccountClassification.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

}
