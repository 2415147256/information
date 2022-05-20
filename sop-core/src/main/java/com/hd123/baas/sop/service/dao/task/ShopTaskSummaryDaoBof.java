package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.task.PlanSummary;
import com.hd123.baas.sop.service.api.task.ShopTaskState;
import com.hd123.baas.sop.service.api.task.ShopTaskSummary;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.*;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class ShopTaskSummaryDaoBof extends BofBaseDao {

  public static final TEMapper<ShopTaskSummary> MAPPER = TEMapperBuilder
      .of(ShopTaskSummary.class, PShopTaskSummary.class)
      .primaryKey(PShopTaskSummary.TENANT, PShopTaskSummary.UUID)
      .map("state", PShopTaskSummary.STATE, EnumConverters.toString(ShopTaskState.class),
          EnumConverters.toEnum(ShopTaskState.class))
      .build();

  public static final TEMapper<ShopTaskSummary> VIEW_MAPPER = TEMapperBuilder
      .of(ShopTaskSummary.class, PVShopTaskSummary.class)
      .primaryKey(PVShopTaskSummary.TENANT, PVShopTaskSummary.UUID)
      .map("state", PVShopTaskSummary.STATE, EnumConverters.toString(ShopTaskState.class),
          EnumConverters.toEnum(ShopTaskState.class))
      .build();

  public static final PlanSummaryMapper PLAN_SUMMARY_MAPPER = new PlanSummaryMapper();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopTaskSummary.class,
      PShopTaskSummary.class).addConditionProcessor(new MyConditionProcessor()).build();
  private final QueryProcessor VIEW_QUERY_PROCESSOR = new QueryProcessorBuilder(ShopTaskSummary.class,
      PVShopTaskSummary.class).addConditionProcessor((condition, context) -> {
    String alias = context.getPerzAlias();
    // shopKeyword查询
    if (StringUtils.equalsIgnoreCase(ShopTaskSummary.Queries.SHOP_KEYWORD_LIKE, condition.getOperation())) {
      String value = (String) condition.getParameter();
      return Predicates.or(Predicates.like(PShopTaskSummary.SHOP, value),
          Predicates.like(PShopTaskSummary.SHOP_CODE, value),
          Predicates.like(PShopTaskSummary.SHOP_NAME, value));
    }
    return null;
  }).build();

  public QueryResult<ShopTaskSummary> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(ShopTaskSummary.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    select.orderBy(PShopTaskSummary.FINISH_TIME);
    return executor.query(select, MAPPER);
  }

  public QueryResult<ShopTaskSummary> mobileQuery(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(ShopTaskSummary.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = VIEW_QUERY_PROCESSOR.process(qd);
    select.orderBy(PVShopTaskSummary.PLAN_END_TIME);
    return executor.query(select, VIEW_MAPPER);
  }

  public List<ShopTaskSummary> list(String tenant, String plan, String planPeriodCode) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(plan, "plan");
    Assert.notNull(planPeriodCode, "planPeriodCode");
    SelectStatement select = new SelectBuilder().select()
        .from(PShopTaskSummary.TABLE_NAME)
        .where(Predicates.equals(PShopTaskSummary.TENANT, tenant))
        .where(Predicates.equals(PShopTaskSummary.PLAN, plan))
        .where(Predicates.equals(PShopTaskSummary.PLAN_PERIOD_CODE, planPeriodCode))
        .orderBy(PShopTaskSummary.SCORE, false)
        .build();
    List<ShopTaskSummary> result = jdbcTemplate.query(select, MAPPER);
    return result;
  }

  public ShopTaskSummary get(String tenant, String uuid) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    SelectStatement select = new SelectBuilder().select()
        .from(PShopTaskSummary.TABLE_NAME)
        .where(Predicates.equals(PShopTaskSummary.TENANT, tenant))
        .where(Predicates.equals(PShopTaskSummary.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, MAPPER));
  }

  public ShopTaskSummary mobileGet(String tenant, String uuid) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    SelectStatement select = new SelectBuilder().select()
        .from(PVShopTaskSummary.TABLE_NAME)
        .where(Predicates.equals(PShopTaskSummary.TENANT, tenant))
        .where(Predicates.equals(PShopTaskSummary.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, VIEW_MAPPER));
  }

  public QueryResult<PlanSummary> queryPlanSummary(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(ShopTaskSummary.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    select.getSelectClause().getFields().clear();
    select.select(PlanSummary.PPlanSummary.allColumns());
    select.groupBy(PShopTaskSummary.PLAN_CODE, PShopTaskSummary.PLAN_PERIOD_CODE);
    select.orderBy(PShopTaskSummary.PLAN_END_TIME, false);
    return executor.query(select, PLAN_SUMMARY_MAPPER);
  }

  public PlanSummary getPlanSummary(String tenant, String plan, String periodCode) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(plan, "plan");
    Assert.notNull(periodCode, "periodCode");
    SelectStatement select = new SelectBuilder().from(PShopTaskSummary.TABLE_NAME)
        .where(Predicates.equals(PShopTaskSummary.TENANT, tenant))
        .where(Predicates.equals(PShopTaskSummary.PLAN, plan))
        .where(Predicates.equals(PShopTaskSummary.PLAN_PERIOD_CODE, periodCode))
        .build();
    select.getSelectClause().getFields().clear();
    select.select(PlanSummary.PPlanSummary.allColumns());
    select.groupBy(PlanSummary.PPlanSummary.PLAN, PShopTaskSummary.PLAN_PERIOD);
    select.orderBy(PShopTaskSummary.FINISH_TIME, false);
    return getFirst(jdbcTemplate.query(select, PLAN_SUMMARY_MAPPER));
  }

  public void updateScore(String tenant, String uuid, BigDecimal score) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(score, "score");
    UpdateBuilder update = new UpdateBuilder().table(PShopTaskSummary.TABLE_NAME)
        .where(Predicates.equals(PShopTaskSummary.TENANT, tenant))
        .where(Predicates.equals(PShopTaskSummary.UUID, uuid))
        .setValue(PShopTaskLog.SCORE, score);
    jdbcTemplate.update(update.build());
  }

  public void finish(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    UpdateBuilder update = new UpdateBuilder().table(PShopTaskSummary.TABLE_NAME)
        .where(Predicates.equals(PShopTaskSummary.TENANT, tenant))
        .where(Predicates.equals(PShopTaskSummary.UUID, uuid))
        .setValue(PShopTaskSummary.STATE, ShopTaskState.FINISHED.name())
        .setValue(PShopTaskSummary.FINISH_TIME, new Date());
    jdbcTemplate.update(update.build());
  }

  public void insert(String tenant, ShopTaskSummary planTask) {
    Assert.notNull(tenant, "tenant");
    planTask.setTenant(tenant);
    if (planTask.getUuid() == null) {
      planTask.setUuid(UUID.randomUUID().toString());
    }
    InsertStatement insert = new InsertBuilder().table(PShopTaskSummary.TABLE_NAME)
        .addValues(MAPPER.forInsert(planTask, PShopTaskSummary.RANK))
        .build();
    jdbcTemplate.update(insert);
  }

  public void updateRank(String tenant, List<ShopTaskSummary> tasks) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(tasks);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopTaskSummary task : tasks) {
      UpdateStatement update = new UpdateBuilder().table(PShopTaskSummary.TABLE_NAME)
          .setValue(PShopTaskSummary.TABLE_NAME + "." + PShopTaskSummary.RANK, task.getRank())
          .where(Predicates.equals(PShopTaskSummary.TENANT, tenant))
          .where(Predicates.equals(PShopTaskSummary.UUID, task.getUuid()))
          .build();
      updater.add(update);
    }
    updater.update();
  }

  public void update(String tenant, ShopTaskSummary summary) {
    Assert.notNull(tenant);
    Assert.notNull(summary);
    Assert.notNull(summary.getUuid());
    UpdateStatement update = new UpdateBuilder().table(PShopTaskSummary.TABLE_NAME)
        .setValues(MAPPER.forUpdate(summary, PShopTaskSummary.RANK))
        .where(Predicates.equals(PShopTaskSummary.TENANT, tenant))
        .where(Predicates.equals(PShopTaskSummary.UUID, summary.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public List<PlanSummary> listBeforeEndTime(String tenant, String state, Date date) {
    Assert.notNull(tenant, "tenant");
    SelectStatement select = new SelectBuilder().select(PlanSummary.PPlanSummary.allColumns())
        .from(PShopTaskSummary.TABLE_NAME)
        .where(Predicates.equals(PShopTaskSummary.TENANT, tenant))
        .where(Predicates.equals(PShopTaskSummary.STATE, state))
        .where(Predicates.lessOrEquals(PShopTaskSummary.PLAN_END_TIME, date))
        .groupBy(PShopTaskSummary.PLAN_CODE, PShopTaskSummary.PLAN_PERIOD_CODE)
        .build();
    return jdbcTemplate.query(select, PLAN_SUMMARY_MAPPER);
  }

  public static class MyConditionProcessor implements QueryConditionProcessor {
    @Override
    public Predicate process(QueryCondition condition, QueryProcessContext context)
        throws IllegalArgumentException, QueryProcessException {
      if (condition == null) {
        return null;
      }
      String alias = context.getPerzAlias();
      if (StringUtils.equals(condition.getOperation(), ShopTaskSummary.Queries.SHOP_KEYWORD_LIKE)) {
        return Predicates.or(like(PShopTaskSummary.SHOP_CODE, condition.getParameter()),
            like(PShopTask.SHOP_NAME, condition.getParameter()));
      }
      if (StringUtils.equals(condition.getOperation(), ShopTaskSummary.Queries.PLAN_KEYWORD_LIKE)) {
        return Predicates.or(like(PShopTaskSummary.PLAN_NAME, condition.getParameter()),
            like(PShopTaskSummary.PLAN_CODE, condition.getParameter()));
      }
      if (StringUtils.equals(condition.getOperation(), ShopTaskSummary.Queries.HANDLE_IN)) {
        SelectStatement selectStatement = new SelectBuilder().select("shopTask.*")
            .from(PShopTask.TABLE_NAME, "shopTask")
            .innerJoin(PShopTaskLog.TABLE_NAME, "log",
                Predicates.and(Predicates.equals("shopTask", PShopTask.UUID, "log", PShopTaskLog.OWNER),
                    Predicates.equals("shopTask", PShopTask.TENANT, "log", PShopTaskLog.TENANT)))
            .where(Predicates.in("log", PShopTaskLog.OPERATOR_ID, condition.getParameter()))
            .build();
        SelectStatement select1 = new SelectBuilder().select("1")
            .from(selectStatement, "shopTask")
            .where(Predicates.and(Predicates.equals("shopTask", PShopTask.OWNER, alias, PShopTaskSummary.UUID),
                Predicates.equals("shopTask", PShopTask.TENANT, alias, PShopTaskSummary.TENANT)))
            .build();
        return Predicates.exists(select1);
      }
      if (StringUtils.equals(condition.getOperation(), ShopTaskSummary.Queries.PLAN_START_TIME_BTW)) {
        List<Object> parameters = condition.getParameters();
        if (parameters.size() < 2) {
          return null;
        }
        Object low = parameters.get(0);
        Object high = parameters.get(1);
        return Predicates.between(PShopTaskSummary.PLAN_START_TIME, low, high);
      }
      return null;
    }
  }
}
