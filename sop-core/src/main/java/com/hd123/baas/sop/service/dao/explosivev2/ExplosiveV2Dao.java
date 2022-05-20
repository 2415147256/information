package com.hd123.baas.sop.service.dao.explosivev2;

import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.*;
import com.hd123.rumba.commons.jdbc.sql.*;
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
public class ExplosiveV2Dao {
  public static final TEMapper<ExplosiveV2> MAPPER = TEMapperBuilder.of(ExplosiveV2.class, PExplosiveV2.class)
      .map("state", PExplosiveV2.STATE,
          EnumConverters.toString(ExplosiveV2.State.class), EnumConverters.toEnum(ExplosiveV2.State.class))
      .primaryKey(PExplosiveV2.UUID)
      .build();

  private QueryProcessor QUERY_PROCESSOR =
      new QueryProcessorBuilder(ExplosiveV2.class, PExplosiveV2.class)
          .addConditionProcessor(new MyConditionProcessor()).build();

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private JdbcPagingQueryExecutor<ExplosiveV2> executor;


  public void insert(String tenant, ExplosiveV2 explosiveV2) {
    Assert.notBlank(tenant);
    Assert.notNull(explosiveV2);
    explosiveV2.setTenant(tenant);
    InsertStatement insert = new InsertBuilder()
        .table(PExplosiveV2.TABLE_NAME)
        .addValues(MAPPER.forInsert(explosiveV2))
        .build();
    jdbcTemplate.update(insert);
  }

  public ExplosiveV2 get(String tenant, String uuid, boolean forUpdate) {
    Assert.notBlank(tenant);
    Assert.notBlank(uuid);
    SelectBuilder selectBuilder = new SelectBuilder()
        .from(PExplosiveV2.TABLE_NAME)
        .where(Predicates.equals(PExplosiveV2.TENANT, tenant))
        .where(Predicates.equals(PExplosiveV2.UUID, uuid));
    SelectStatement select = forUpdate ? selectBuilder.forUpdate().build() : selectBuilder.build();
    List<ExplosiveV2> list = jdbcTemplate.query(select, MAPPER);
    return CollectionUtils.isEmpty(list) ? null : list.get(0);
  }

  public void delete(String tenant, String uuid) {
    Assert.notBlank(tenant);
    Assert.notBlank(uuid);
    DeleteStatement delete = new DeleteBuilder()
        .table(PExplosiveV2.TABLE_NAME)
        .where(Predicates.equals(PExplosiveV2.TENANT, tenant))
        .where(Predicates.equals(PExplosiveV2.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

  public QueryResult<ExplosiveV2> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant);
    Assert.notNull(qd);

    qd.addByField(ExplosiveV2.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    return executor.query(selectStatement, MAPPER);
  }

  public void batchUpdate(String tenant, List<ExplosiveV2> explosiveV2s) {
    Assert.notBlank(tenant);
    Assert.notEmpty(explosiveV2s);
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveV2 explosiveV2 : explosiveV2s) {
      explosiveV2.setTenant(tenant);
      UpdateStatement update = new UpdateBuilder()
          .table(PExplosiveV2.TABLE_NAME)
          .where(Predicates.equals(PExplosiveV2.UUID, explosiveV2.getUuid()))
          .addValues(MAPPER.forUpdate(explosiveV2))
          .setValue(PExplosiveV2.LAST_MODIFY_INFO_TIME, new Date())
          .build();
      batchUpdater.add(update);
    }
    batchUpdater.update();
  }

  public void update(String tenant, ExplosiveV2 explosiveV2) {
    Assert.notBlank(tenant);
    Assert.notNull(explosiveV2);
    explosiveV2.setTenant(tenant);
    UpdateStatement update = new UpdateBuilder()
        .table(PExplosiveV2.TABLE_NAME)
        .where(Predicates.equals(PExplosiveV2.UUID, explosiveV2.getUuid()))
        .addValues(MAPPER.forUpdate(explosiveV2))
        .setValue(PExplosiveV2.LAST_MODIFY_INFO_TIME, new Date())
        .build();
    jdbcTemplate.update(update);
  }

  public void updateState(String tenant, String uuid, String state, OperateInfo operateInfo) {
    Assert.notBlank(tenant);
    Assert.notBlank(uuid);
    Assert.notBlank(state);
    UpdateBuilder update = new UpdateBuilder()
        .table(PExplosiveV2.TABLE_NAME)
        .where(Predicates.equals(PExplosiveV2.TENANT, tenant))
        .where(Predicates.equals(PExplosiveV2.UUID, uuid))
        .setValue(PExplosiveV2.STATE, state);
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    jdbcTemplate.update(update.build());
  }

  public List<ExplosiveV2> list(String tenant, List<String> uuids) {
    Assert.notBlank(tenant, "tenant");

    SelectBuilder selectBuilder = new SelectBuilder()
        .from(PExplosiveV2.TABLE_NAME)
        .where(Predicates.equals(PExplosiveV2.TENANT, tenant))
        .where(Predicates.in2(PExplosiveV2.UUID, uuids.toArray()));
    return jdbcTemplate.query(selectBuilder.build(), MAPPER);
  }

  public static class MyConditionProcessor implements QueryConditionProcessor {
    @Override
    public Predicate process(QueryCondition condition, QueryProcessContext context)
        throws IllegalArgumentException, QueryProcessException {
      if (context == null) {
        return null;
      }
      String alias = context.getPerzAlias();
      if (ExplosiveV2.Queries.KEYWORD_LIKE.equals(condition.getOperation())) {
        return or(like(alias, PExplosiveV2.NAME, condition.getParameter()),
            Predicates.equals(alias, PExplosiveV2.FLOW_NO, condition.getParameter()));
      }
      if (ExplosiveV2.Queries.EFFECTIVE_DATE_BTW.equals(condition.getOperation())) {
        List<Object> parameters = condition.getParameters();
        return Predicates.and(Predicates.greaterOrEquals(PExplosiveV2.START_DATE, parameters.get(0)),
            Predicates.lessOrEquals(PExplosiveV2.END_DATE, parameters.get(1)));
      }
      if (ExplosiveV2.Queries.SIGN_DATE_BTW.equals(condition.getOperation())) {
        List<Object> parameters = condition.getParameters();
        return Predicates.and(Predicates.greaterOrEquals(PExplosiveV2.SIGN_START_DATE, parameters.get(0)),
            Predicates.lessOrEquals(PExplosiveV2.SIGN_END_DATE, parameters.get(1)));
      }
      if (ExplosiveV2.Queries.ACTIVE_DATE_BTW.equals(condition.getOperation())) {
        List<Object> parameters = condition.getParameters();
        return Predicates.not(
            Predicates.or(
                Predicates.less(PExplosiveV2.END_DATE, parameters.get(0)),
                Predicates.greater(PExplosiveV2.START_DATE, parameters.get(1))
            )
        );
      }
      return null;
    }
  }
}
