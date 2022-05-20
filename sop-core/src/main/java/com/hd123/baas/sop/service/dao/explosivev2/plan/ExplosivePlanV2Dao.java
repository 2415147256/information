package com.hd123.baas.sop.service.dao.explosivev2.plan;

import com.hd123.baas.sop.service.api.explosivev2.plan.ExplosivePlanV2;
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
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;
import static com.hd123.rumba.commons.jdbc.sql.Predicates.or;

/**
 * @author shenmin
 */
@Repository
public class ExplosivePlanV2Dao {
  public static final TEMapper<ExplosivePlanV2> MAPPER = TEMapperBuilder.of(ExplosivePlanV2.class, PExplosivePlanV2.class)
      .map("state", PExplosivePlanV2.STATE,
          EnumConverters.toString(ExplosivePlanV2.State.class), EnumConverters.toEnum(ExplosivePlanV2.State.class))
      .primaryKey(PExplosivePlanV2.UUID)
      .build();
  private QueryProcessor QUERY_PROCESSOR =
      new QueryProcessorBuilder(ExplosivePlanV2.class, PExplosivePlanV2.class)
          .addConditionProcessor(new MyConditionProcessor()).build();

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private JdbcPagingQueryExecutor<ExplosivePlanV2> executor;


  public void insert(String tenant, ExplosivePlanV2 explosivePlanV2) {
    Assert.notBlank(tenant);
    Assert.notNull(explosivePlanV2);
    explosivePlanV2.setTenant(tenant);
    InsertStatement insert = new InsertBuilder()
        .table(PExplosivePlanV2.TABLE_NAME)
        .addValues(MAPPER.forInsert(explosivePlanV2))
        .build();
    jdbcTemplate.update(insert);
  }

  public ExplosivePlanV2 get(String tenant, String uuid, boolean forUpdate) {
    Assert.notBlank(tenant);
    Assert.notBlank(uuid);
    SelectBuilder selectBuilder = new SelectBuilder()
        .from(PExplosivePlanV2.TABLE_NAME)
        .where(Predicates.equals(PExplosivePlanV2.TENANT, tenant))
        .where(Predicates.equals(PExplosivePlanV2.UUID, uuid));
    SelectStatement select = forUpdate ? selectBuilder.forUpdate().build() : selectBuilder.build();
    List<ExplosivePlanV2> list = jdbcTemplate.query(select, MAPPER);
    return CollectionUtils.isEmpty(list) ? null : list.get(0);
  }


  public void batchUpdate(String tenant, List<ExplosivePlanV2> explosivePlanV2s) {
    Assert.notBlank(tenant);
    Assert.notEmpty(explosivePlanV2s);
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (ExplosivePlanV2 explosivePlanV2 : explosivePlanV2s) {
      explosivePlanV2.setTenant(tenant);
      UpdateStatement update = new UpdateBuilder()
          .table(PExplosivePlanV2.TABLE_NAME)
          .where(Predicates.equals(PExplosivePlanV2.UUID, explosivePlanV2.getUuid()))
          .addValues(MAPPER.forUpdate(explosivePlanV2))
          .setValue(PExplosivePlanV2.LAST_MODIFY_INFO_TIME, new Date())
          .build();
      batchUpdater.add(update);
    }
    batchUpdater.update();
  }

  public void update(String tenant, ExplosivePlanV2 explosivePlanV2) {
    Assert.notBlank(tenant);
    Assert.notNull(explosivePlanV2);
    explosivePlanV2.setTenant(tenant);
    UpdateStatement update = new UpdateBuilder()
        .table(PExplosivePlanV2.TABLE_NAME)
        .where(Predicates.equals(PExplosivePlanV2.UUID, explosivePlanV2.getUuid()))
        .addValues(MAPPER.forUpdate(explosivePlanV2))
        .setValue(PExplosivePlanV2.LAST_MODIFY_INFO_TIME, new Date())
        .build();

    jdbcTemplate.update(update);
  }

  public void delete(String tenant, String uuid) {
    Assert.notBlank(tenant);
    Assert.notBlank(uuid);
    DeleteStatement delete = new DeleteBuilder()
        .table(PExplosivePlanV2.TABLE_NAME)
        .where(Predicates.equals(PExplosivePlanV2.TENANT, tenant))
        .where(Predicates.equals(PExplosivePlanV2.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

  public QueryResult<ExplosivePlanV2> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant);
    Assert.notNull(qd);

    qd.addByField(ExplosivePlanV2.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    return executor.query(selectStatement, MAPPER);
  }

  public class MyConditionProcessor implements QueryConditionProcessor {
    @Override
    public Predicate process(QueryCondition condition, QueryProcessContext context)
        throws IllegalArgumentException, QueryProcessException {
      if (context == null) {
        return null;
      }
      String alias = context.getPerzAlias();
      if (ExplosivePlanV2.Queries.KEYWORD_LIKE.equals(condition.getOperation())) {
        return or(like(alias, PExplosivePlanV2.NAME, condition.getParameter()),
            Predicates.equals(alias, PExplosivePlanV2.FLOW_NO, condition.getParameter()));
      }
      if (ExplosivePlanV2.Queries.EFFECTIVE_DATE_BTW.equals(condition.getOperation())) {
        List<Object> parameters = condition.getParameters();
        return Predicates.and(Predicates.greaterOrEquals(PExplosivePlanV2.START_DATE, parameters.get(0)),
            Predicates.lessOrEquals(PExplosivePlanV2.END_DATE, parameters.get(1)));
      }
      if (ExplosivePlanV2.Queries.SIGN_DATE_BTW.equals(condition.getOperation())) {
        List<Object> parameters = condition.getParameters();
        return Predicates.and(Predicates.greaterOrEquals(PExplosivePlanV2.SIGN_START_DATE, parameters.get(0)),
            Predicates.lessOrEquals(PExplosivePlanV2.SIGN_END_DATE, parameters.get(1)));
      }
      if (ExplosivePlanV2.Queries.ORG_ID_EQ.equals(condition.getOperation())) {
        List<Object> parameters = condition.getParameters();
        return or(Predicates.equals(PExplosivePlanV2.ORG_ID, parameters.get(0)),
            Predicates.equals(PExplosivePlanV2.ORG_ID, parameters.get(1)));
      }
      return null;
    }
  }
}
