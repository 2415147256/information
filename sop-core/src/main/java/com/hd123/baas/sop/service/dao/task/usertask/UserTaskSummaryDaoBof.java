package com.hd123.baas.sop.service.dao.task.usertask;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.task.usertask.UserTaskSummary;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.*;
import com.hd123.rumba.commons.jdbc.sql.Predicate;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;

/**
 * @author W.J.H.7
 */
@Repository
public class UserTaskSummaryDaoBof extends BofBaseDao {

  public static final TEMapper<UserTaskSummary> MAPPER = TEMapperBuilder
      .of(UserTaskSummary.class, PUserTaskSummary.class)
      // .map("state", PUserTaskSummary.STATE,
      // EnumConverters.toString(ShopTaskState.class),
      // EnumConverters.toEnum(ShopTaskState.class))
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(UserTaskSummary.class,
      PUserTaskSummary.class).addConditionProcessor(new MyConditionProcessor()).build();

  public QueryResult<UserTaskSummary> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(UserTaskSummary.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    select.orderBy(PUserTaskSummary.PLAN_START_TIME);
    return executor.query(select, MAPPER);
  }

  public static class MyConditionProcessor implements QueryConditionProcessor {
    @Override
    public Predicate process(QueryCondition condition, QueryProcessContext context)
        throws IllegalArgumentException, QueryProcessException {
      if (condition == null) {
        return null;
      }
      String alias = context.getPerzAlias();
      if (StringUtils.equals(condition.getOperation(), UserTaskSummary.Queries.PLAN_KEYWORD_LIKE)) {
        return Predicates.or(like(PUserTaskSummary.PLAN_NAME, condition.getParameter()),
            like(PUserTaskSummary.PLAN_CODE, condition.getParameter()));
      }
      return null;
    }
  }

  public UserTaskSummary getByUK(String tenant, String plan, String planPeriodCode, String operatorId) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(plan, "plan");
    Assert.notNull(planPeriodCode, "planPeriodCode");
    Assert.notNull(operatorId, "operatorId");
    SelectStatement select = new SelectBuilder().select()
        .from(PUserTaskSummary.TABLE_NAME)
        .where(Predicates.equals(PUserTaskSummary.TENANT, tenant))
        .where(Predicates.equals(PUserTaskSummary.PLAN, plan))
        .where(Predicates.equals(PUserTaskSummary.PLAN_PERIOD_CODE, planPeriodCode))
        .where(Predicates.equals(PUserTaskSummary.OPERATOR_ID, operatorId))
        .build();
    return getFirst(jdbcTemplate.query(select, MAPPER));
  }
}
