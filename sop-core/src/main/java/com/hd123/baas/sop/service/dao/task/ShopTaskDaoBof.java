package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.task.*;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanType;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;

@Repository
public class ShopTaskDaoBof extends BofBaseDao {
  public static final TEMapper<ShopTask> MAPPER = TEMapperBuilder.of(ShopTask.class, PShopTask.class)
      .primaryKey(PShopTask.TENANT, PShopTask.UUID)
      .map("state", PShopTask.STATE, EnumConverters.toString(ShopTaskState.class),
          EnumConverters.toEnum(ShopTaskState.class))
      .build();
  public static final TEMapper<AssignableShopTaskSummary> NEW_SUMMARY_MAPPER = TEMapperBuilder
      .of(AssignableShopTaskSummary.class, PAssignableShopTaskSummary.class)
      .build();
  public static final TEMapper<ShopTaskLine> LINE_MAPPER = TEMapperBuilder.of(ShopTaskLine.class, PShopTaskLine.class)
      .build();
  public static final AssignableShopTaskCountMapper COUNT_MAPPER = new AssignableShopTaskCountMapper();
  public static final AssignableShopTaskSummaryMapper SUMMARY_MAPPER = new AssignableShopTaskSummaryMapper();

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopTask.class, PShopTask.class)
      .addConditionProcessor((condition, queryProcessContext) -> {
        if (condition == null) {
          return null;
        }
        String alias = queryProcessContext.getPerzAlias();
        if (StringUtils.equals(condition.getOperation(), ShopTask.Queries.SHOP_KEYWORD_LIKE)) {
          return Predicates.or(like(PShopTask.SHOP, condition.getParameter()),
              like(PShopTask.SHOP_CODE, condition.getParameter()), like(PShopTask.SHOP_NAME, condition.getParameter()));
        }
        if (StringUtils.equals(condition.getOperation(), ShopTask.Queries.TYPE_EQUALS)) {
          String value = (String) condition.getParameter();
          SelectBuilder select = new SelectBuilder().select("1")
              .from(PShopTaskGroup.TABLE_NAME, PShopTaskGroup.TABLE_ALIAS)
              .where(Predicates.equals(getTableField(PShopTaskGroup.TABLE_ALIAS, PShopTaskGroup.UUID),
                  Expr.valueOf(getTableField(alias, PShopTask.SHOP_TASK_GROUP))))
              .where(Predicates.equals(PShopTaskGroup.TABLE_ALIAS, PShopTaskGroup.TYPE, value));
          return Predicates.exists(select.build());
        }
        if (StringUtils.equals(condition.getOperation(), ShopTask.Queries.PLAN_KEYWORD_LIKE)) {
          return Predicates.or(like(PShopTask.PLAN_CODE, condition.getParameter()),
              like(PShopTask.PLAN_NAME, condition.getParameter()));
        }
        if (StringUtils.equals(condition.getOperation(), ShopTask.Queries.EFFECT_TIME_BTW)) {
          List<Object> parameter = condition.getParameters();
          return Predicates.and(Predicates.greaterOrEquals(PShopTask.PLAN_START_TIME, parameter.get(0)),
              Predicates.lessOrEquals(PShopTask.PLAN_START_TIME, parameter.get(1)));
        }
        if (StringUtils.equals(condition.getOperation(), ShopTask.Queries.TRANSFERSTATE)) {
          List<Object> parameters = condition.getParameters();
          SelectBuilder select = new SelectBuilder().select("1")
              .from(PShopTaskTransfer.TABLE_NAME, PShopTaskTransfer.TABLE_ALIAS)
              .where(
                  Predicates.equals(PShopTaskTransfer.TABLE_ALIAS, PShopTaskTransfer.TENANT, alias, PShopTask.TENANT))
              .where(Predicates.equals(PShopTaskTransfer.TABLE_ALIAS, PShopTaskTransfer.SHOP_TASK_ID, alias,
                  PShopTask.UUID))
              .where(Predicates.equals(PShopTaskTransfer.STATE, ShopTaskTransferState.TRANSFER.name()));
          if (CollectionUtils.isNotEmpty(parameters)) {
            return Predicates.or(Predicates.exists(select.build()),
                Predicates.in2(PShopTask.STATE, parameters.toArray()));
          } else {
            return Predicates.exists(select.build());
          }

        }
        if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(ShopTask.Queries.OWNERSHIP,
            condition.getOperation())) {
          List<Object> parameters = condition.getParameters();
          if (parameters.get(0) == null && parameters.get(1) == null) {
            return null;
          } else if (parameters.get(0) != null && parameters.get(1) != null) {
            return Predicates.or(Predicates.equals(alias, PShopTask.CREATE_INFO_OPERATOR_ID, parameters.get(0)),
                Predicates.equals(alias, PShopTask.OPERATOR_ID, parameters.get(1)));
          } else if (parameters.get(0) == null && parameters.get(1) != null) {
            return Predicates.equals(alias, PShopTask.OPERATOR_ID, parameters.get(1));
          } else if (parameters.get(0) != null && parameters.get(1) == null) {
            return Predicates.equals(alias, PShopTask.CREATE_INFO_OPERATOR_ID, parameters.get(0));
          } else {
            return null;
          }
        }
        if (StringUtils.equals(condition.getOperation(), ShopTask.Queries.KEYWORD_LIKE)) {
          Object parameter = condition.getParameter();
          return Predicates.or(Predicates.like(alias, PShopTask.NAME, parameter),
              Predicates.like(alias, PShopTask.OPERATOR_POSITION_NAME, parameter),
              Predicates.like(alias, PShopTask.OPERATOR_NAME, parameter));
        }
        return null;
      })
      .build();

  private QueryProcessor QUERY_PROCESSOR_SUMMARY = new QueryProcessorBuilder(ShopTask.class, PShopTask.class)
      .addConditionProcessor((condition, queryProcessContext) -> {
        if (condition == null) {
          return null;
        }
        String alias = queryProcessContext.getPerzAlias();
        if (StringUtils.equals(condition.getOperation(), ShopTask.Queries.SHOP_KEYWORD_LIKE)) {
          return Predicates.or(like(PShopTask.SHOP, condition.getParameter()),
              like(PShopTask.SHOP_CODE, condition.getParameter()), like(PShopTask.SHOP_NAME, condition.getParameter()));
        }
        if (StringUtils.equals(condition.getOperation(), ShopTask.Queries.EFFECT_TIME_BTW)) {
          List<Object> parameter = condition.getParameters();
          return Predicates.and(Predicates.greaterOrEquals(PShopTask.PLAN_START_TIME, parameter.get(0)),
              Predicates.lessOrEquals(PShopTask.PLAN_START_TIME, parameter.get(1)));
        }
        return null;
      })
      .build();

  public List<ShopTask> getByShopTaskGroupId(String tenant, String shopTaskGroupId, String... sorts) {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(shopTaskGroupId, "门店任务组ID");

    SelectBuilder select = new SelectBuilder().from(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.SHOP_TASK_GROUP, shopTaskGroupId));
    if (sorts != null) {
      for (String sort : sorts) {
        if (sort.equals(PShopTask.REMIND_TIME)) {
          select.orderBy(PShopTask.REMIND_TIME, true);
        }
        if (sort.equals(PShopTask.SORT)) {
          select.orderBy(PShopTask.SORT, true);
        }
      }
    }
    return jdbcTemplate.query(select.build(), MAPPER);
  }

  public void finish(String tenant, String uuid, String finishAppId, String feedback, OperateInfo finishInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(finishInfo, "finishInfo");
    UpdateBuilder update = new UpdateBuilder().table(PShopTask.TABLE_NAME)
        .setValue(PShopTask.STATE, ShopTaskState.FINISHED.name())
        .setValue(PShopTask.FINISH_APPID, finishAppId)
        .setValue(PShopTask.FEEDBACK, feedback);

    update.setValue(PShopTask.FINISH_INFO_OPERATOR_FULL_NAME, finishInfo.getOperator().getFullName())
        .setValue(PShopTask.FINISH_INFO_OPERATOR_ID, finishInfo.getOperator().getId())
        .setValue(PShopTask.FINISH_INFO_OPERATOR_NAMESPACE, finishInfo.getOperator().getNamespace())
        .setValue(PShopTask.FINISH_INFO_TIME, finishInfo.getTime())
        .setValues(PStandardEntity.toLastModifyInfoFieldValues(finishInfo));
    update.where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, uuid))
        .where(Predicates.equals(PShopTask.STATE, ShopTaskState.UNFINISHED.name()));
    jdbcTemplate.update(update.build());
  }

  public void batchInsert(String tenant, List<ShopTask> shopTasks) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(shopTasks);
    List<InsertStatement> list = new ArrayList<>();
    for (ShopTask shopTask : shopTasks) {
      shopTask.setTenant(tenant);
      InsertBuilder insert = new InsertBuilder().table(PShopTask.TABLE_NAME).addValues(MAPPER.forInsert(shopTask));
      list.add(insert.build());
    }
    batchUpdate(list);
  }

  public void batchUpdate(String tenant, List<ShopTask> shopTasks, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(shopTasks);
    List<UpdateStatement> list = new ArrayList<>();
    for (ShopTask shopTask : shopTasks) {
      shopTask.setLastModifyInfo(operateInfo);
      UpdateBuilder update = new UpdateBuilder().table(PShopTask.TABLE_NAME)
          .addValues(MAPPER.forInsert(shopTask))
          .where(Predicates.equals(PShopTask.TENANT, tenant))
          .where(Predicates.equals(PShopTask.UUID, shopTask.getUuid()));
      list.add(update.build());
    }
    batchUpdate(list);
  }

  public void delete(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    DeleteBuilder delete = new DeleteBuilder().table(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, uuid));
    jdbcTemplate.update(delete.build());
  }

  public ShopTask getByShopTaskGroupIdAndTaskPlanId(String tenant, String shopTaskGroupId, String taskPlanId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shopTaskGroupId, "shopTaskGroupId");
    Assert.notNull(taskPlanId, "taskPlanId");
    SelectBuilder select = new SelectBuilder().from(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.SHOP_TASK_GROUP, shopTaskGroupId))
        .where(Predicates.equals(PShopTask.PLAN, taskPlanId));
    List<ShopTask> query = jdbcTemplate.query(select.build(), MAPPER);
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    return query.get(0);
  }

  public void insert(String tenant, ShopTask shopTask) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shopTask, "shopTask");
    InsertBuilder insert = new InsertBuilder().table(PShopTask.TABLE_NAME).addValues(MAPPER.forInsert(shopTask));
    jdbcTemplate.update(insert.build());
  }

  public void setExpired(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    UpdateBuilder update = new UpdateBuilder().table(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, uuid))
        .setValue(PShopTask.STATE, ShopTaskState.EXPIRED.name());
    jdbcTemplate.update(update.build());
  }

  public void addScore(String tenant, String uuid, BigDecimal score) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(score, "score");
    UpdateBuilder update = new UpdateBuilder().table(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, uuid))
        .setValue(PShopTask.SCORE, score);
    jdbcTemplate.update(update.build());
  }

  public void finish(String tenant, String uuid, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    UpdateBuilder update = new UpdateBuilder().table(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, uuid))
        .setValue(PShopTask.STATE, ShopTaskState.FINISHED.name())
        .setValue(PShopTask.FINISH_INFO_TIME, operateInfo.getTime())
        .setValue(PShopTask.FINISH_INFO_OPERATOR_FULL_NAME, operateInfo.getOperator().getFullName())
        .setValue(PShopTask.FINISH_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
        .setValue(PShopTask.FINISH_INFO_OPERATOR_NAMESPACE, operateInfo.getOperator().getNamespace());
    jdbcTemplate.update(update.build());
  }

  public ShopTask get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    SelectBuilder select = new SelectBuilder().from(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, uuid));
    List<ShopTask> result = jdbcTemplate.query(select.build(), MAPPER);
    if (CollectionUtils.isEmpty(result)) {
      return null;
    }
    return result.get(0);
  }

  public QueryResult<ShopTask> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(qd, "qd");
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, MAPPER);
  }

  public List<ShopTask> list(String tenant, String owner) {
    SelectBuilder build = new SelectBuilder().from(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.OWNER, owner));
    return executor.query(build.build(), MAPPER).getRecords();
  }

  public List<ShopTask> listByLoginId(String tenant, String owner, String loginId) {
    SelectStatement selectStatement = new SelectBuilder().select("log.owner")
        .from(PShopTaskLog.TABLE_NAME, "log")
        .where(Predicates.equals("log", PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals("log", PShopTaskLog.OPERATOR_ID, loginId))
        .build();
    SelectBuilder build = new SelectBuilder().from(PShopTask.TABLE_NAME, PShopTask.TABLE_ALIAS)
        .where(Predicates.in(PShopTask.TABLE_ALIAS, PShopTask.UUID, selectStatement))
        .where(Predicates.equals(PShopTask.TABLE_ALIAS, PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.TABLE_ALIAS, PShopTask.OWNER, owner));
    return executor.query(build.build(), MAPPER).getRecords();
  }

  public List<ShopTask> list(String tenant, Date remindTime) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(remindTime, "remindTime");
    SelectStatement select = new SelectBuilder().from(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.REMIND_TIME, remindTime))
        .where(Predicates.equals(PShopTask.STATE, ShopTaskState.UNFINISHED.name()))
        .where(Predicates.equals(PShopTask.PLAN_TYPE, TaskPlanType.INSPECTION.name()))
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }

  public List<ShopTask> listByStartTime(String tenant, Date remindTime) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(remindTime, "remindTime");
    SelectStatement select = new SelectBuilder().from(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.lessOrEquals(PShopTask.PLAN_START_TIME, remindTime))
        .where(Predicates.equals(PShopTask.STATE, ShopTaskState.NOT_STARTED.name()))
        .where(Predicates.equals(PShopTask.PLAN_TYPE, TaskPlanType.INSPECTION.name()))
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }

  private String getTableField(String tableAlias, String field) {
    return tableAlias + "." + field;
  }

  public List<ShopTask> list(String tenant, String shop, List<String> taskGroupId, Date start, Date end) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shop, "门店");
    Assert.notNull(taskGroupId, "任务组");

    SelectBuilder select = new SelectBuilder().from(PShopTask.TABLE_NAME, PShopTask.TABLE_ALIAS)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.SHOP, shop))
        .where(Predicates.in2(PShopTask.TASK_GROUP, taskGroupId.toArray()))
        .where(Predicates.between(PShopTask.CREATE_INFO_TIME, start, end))
        .orderBy(PShopTask.REMIND_TIME, true);
    return jdbcTemplate.query(select.build(), MAPPER);
  }

  /**
   * 修改人：zhuyuntao
   * 修改时间：2021-06-29
   * 修改内容: 添加排序规则： name 升序排序
   *
   * @param tenant
   * @param plan
   * @param planPeriod
   * @param shop
   * @param operatorId
   * @return
   */
  public List<ShopTask> listByUK(String tenant, String plan, String planPeriod, String shop, String operatorId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(plan, "计划ID");
    Assert.notNull(planPeriod, "计划周期");
    Assert.notNull(shop, "门店ID");
    Assert.notNull(operatorId, "执行人");

    SelectStatement select1 = new SelectBuilder().select("1")
        .from(PShopTaskLog.TABLE_NAME, "stl")
        .where(Predicates.and(Predicates.equals("stl", PShopTaskLog.OWNER, "st", PShopTask.UUID),
            Predicates.equals("stl", PShopTaskLog.TENANT, "st", PShopTask.TENANT),
            Predicates.equals("stl." + PShopTaskLog.OPERATOR_ID, operatorId)))
        .build();

    SelectStatement select = new SelectBuilder().from(PShopTask.TABLE_NAME, "st")
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.PLAN, plan))
        .where(Predicates.equals(PShopTask.PLAN_PERIOD, planPeriod))
        .where(Predicates.equals(PShopTask.SHOP, shop))
        .where(Predicates.exists(select1))
        .orderBy(PShopTask.REMIND_TIME, true)
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }

  public void terminate(String tenant, String uuid, OperateInfo finishInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    UpdateBuilder update = new UpdateBuilder().table(PShopTask.TABLE_NAME)
        .setValue(PShopTask.STATE, ShopTaskState.TERMINATE.name())
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, uuid));
    update.setValue(PShopTask.FINISH_INFO_OPERATOR_FULL_NAME, finishInfo.getOperator().getFullName())
        .setValue(PShopTask.FINISH_INFO_OPERATOR_ID, finishInfo.getOperator().getId())
        .setValue(PShopTask.FINISH_INFO_OPERATOR_NAMESPACE, finishInfo.getOperator().getNamespace())
        .setValue(PShopTask.FINISH_INFO_TIME, finishInfo.getTime())
        .setValues(PStandardEntity.toLastModifyInfoFieldValues(finishInfo));
    jdbcTemplate.update(update.build());
  }

  public void terminateByPlan(String tenant, String plan, OperateInfo finishInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(plan, "plan");

    UpdateBuilder update = new UpdateBuilder().table(PShopTask.TABLE_NAME)
        .setValue(PShopTask.STATE, ShopTaskState.TERMINATE.name())
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.PLAN, plan))
        .where(Predicates.equals(PShopTask.STATE, ShopTaskState.UNFINISHED.name()));
    update.setValue(PShopTask.FINISH_INFO_OPERATOR_FULL_NAME, finishInfo.getOperator().getFullName())
        .setValue(PShopTask.FINISH_INFO_OPERATOR_ID, finishInfo.getOperator().getId())
        .setValue(PShopTask.FINISH_INFO_OPERATOR_NAMESPACE, finishInfo.getOperator().getNamespace())
        .setValue(PShopTask.FINISH_INFO_TIME, finishInfo.getTime())
        .setValues(PStandardEntity.toLastModifyInfoFieldValues(finishInfo));
    jdbcTemplate.update(update.build());
  }

  public List<ShopTask> listShop(String tenant, String loginId) {
    Assert.hasText(tenant, "tenant");

    SelectBuilder selectBuilder = new SelectBuilder().from(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.PLAN_TYPE, TaskPlanType.ASSIGNABLE.name()))
        .where(Predicates.isNotNull(PShopTask.OPERATOR_ID))
        .where(Predicates.isNotNull(PShopTask.SHOP))
        .where(Predicates.isNotNull(PShopTask.SHOP_NAME))
        .where(Predicates.isNotNull(PShopTask.SHOP_CODE))
        .groupBy(PShopTask.SHOP, PShopTask.SHOP_CODE, PShopTask.SHOP_NAME);
    if (StringUtils.isNotEmpty(loginId)) {
      selectBuilder.where(Predicates.equals(PShopTask.CREATE_INFO_OPERATOR_ID, loginId));
    }
    return jdbcTemplate.query(selectBuilder.build(), MAPPER);
  }

  public void changeShopTaskState(String tenant, String uuid, BigDecimal score, ShopTaskState state,
                                  OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(score);
    Assert.notNull(operateInfo);
    Assert.notNull(state);

    UpdateBuilder update = new UpdateBuilder().table(PShopTask.TABLE_NAME)
        .setValue(PShopTask.STATE, state.name())
        .setValue(PShopTask.SCORE, score)
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, uuid));
    if (ShopTaskState.FINISHED == state) {
      update.setValue(PShopTask.FINISH_INFO_TIME, operateInfo.getTime())
          .setValue(PShopTask.FINISH_INFO_OPERATOR_FULL_NAME, operateInfo.getOperator().getFullName())
          .setValue(PShopTask.FINISH_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
          .setValue(PShopTask.FINISH_INFO_OPERATOR_NAMESPACE, operateInfo.getOperator().getNamespace());
    }
    jdbcTemplate.update(update.build());
  }

  public QueryResult<ShopTask> querySummary(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(qd, "qd");
    SelectStatement select = QUERY_PROCESSOR_SUMMARY.process(qd);
    select.where(Predicates.notEquals(PShopTask.STATE, ShopTaskState.TERMINATE.name()));
    select.groupBy(PShopTask.SHOP, PShopTask.SHOP_CODE, PShopTask.SHOP_NAME);
    return executor.query(select, MAPPER);
  }

  /**
   * 方法实现的sql
   * <p>
   * SELECT a.tenant, a.shop, a.shop_code, a.shop_name, a.finished, a.total,
   * a.score, a.point, a.rate,
   *
   * @param tenant      租户
   * @param startDate   开始日期
   * @param endDate     截至日期
   * @param shopKeyWord 门店关键词
   * @param page        page*pageSize
   * @param pageSize    页码大小
   * @return 查询结果
   * @j := @j + 1 `rownum`,
   * @k := ( CASE WHEN @pre_rate = a.rate THEN @k ELSE @j END ) AS 'rank',
   * @pre_rate := a.rate AS pre_rate FROM ( SELECT st.tenant, st.shop,
   * st.shop_code, st.shop_name, sum( CASE WHEN st.state = 'FINISHED'
   * THEN 1 ELSE 0 END ) finished, sum( CASE WHEN st.state IN (
   * 'UNFINISHED', 'SUBMITTED', 'FINISHED', 'EXPIRED' ) THEN 1 ELSE 0
   * END ) total, sum( CASE WHEN st.state = 'FINISHED' THEN st.score
   * ELSE 0 END ) score, sum( CASE WHEN st.state IN ( 'UNFINISHED',
   * 'SUBMITTED', 'FINISHED', 'EXPIRED' ) THEN st.point ELSE 0 END )
   * point, CASE
   * <p>
   * WHEN sum( CASE WHEN st.state IN ( 'UNFINISHED', 'SUBMITTED',
   * 'FINISHED', 'EXPIRED' ) THEN 1 ELSE 0 END ) = 0 THEN 0 ELSE IFNULL(
   * round( sum( CASE WHEN st.state = 'FINISHED' THEN st.score ELSE 0
   * END ) / sum( CASE WHEN st.state IN ( 'UNFINISHED', 'SUBMITTED',
   * 'FINISHED', 'EXPIRED' ) THEN st.point ELSE 0 END ) * 100, 2 ), 0 )
   * END rate FROM shop_task st WHERE st.plan_type = 'ASSIGNABLE' AND
   * st.shop IS NOT NULL
   * <p>
   * AND st.plan_start_time BETWEEN '2021-01-28 15:38:00' AND
   * '2022-05-28 23:00:00' GROUP BY st.tenant, st.shop, st.shop_code,
   * st.shop_name ) a CROSS JOIN ( SELECT @j := 0, @k := 0, @pre_rate :=
   * 0 ) AS t ORDER BY a.rate DESC;
   */
  public List<AssignableShopTaskSummary> querySummary(String tenant, Date startDate, Date endDate, String shopKeyWord,
                                                      Integer page, Integer pageSize) {
    Assert.notNull(tenant, "tenant");
    SelectStatement build = new SelectBuilder()
        .select("st.tenant", "st.shop", "st.shop_code", "st.shop_name",
            "sum(case when st.state='" + ShopTaskState.FINISHED.name() + "' then 1 else 0 end) as finished",
            "sum(case when st.state in ('" + ShopTaskState.UNFINISHED.name() + "'," + "'"
                + ShopTaskState.SUBMITTED.name() + "'" + ",'" + ShopTaskState.FINISHED.name() + "','"
                + ShopTaskState.EXPIRED.name() + "') then 1 else 0 end) as total",
            "sum(case when st.state = '" + ShopTaskState.FINISHED.name() + "' then st.score else 0 end ) as score",
            "sum(case when st.state in ('" + ShopTaskState.UNFINISHED.name() + "','" + ShopTaskState.SUBMITTED.name()
                + "','" + ShopTaskState.FINISHED.name() + "','" + ShopTaskState.EXPIRED.name()
                + "') then st.point else 0 end) as point",
            "case when sum(case when st.state in ('" + ShopTaskState.UNFINISHED.name() + "','"
                + ShopTaskState.SUBMITTED.name() + "','" + ShopTaskState.FINISHED.name() + "','"
                + ShopTaskState.EXPIRED.name() + "') then 1 else 0 end) =0 then 0 else "
                + "IFNULL(round(sum(case when st.state='" + ShopTaskState.FINISHED.name()
                + "' then st.score else 0 end)/sum(case when st.state in ('" + ShopTaskState.UNFINISHED.name() + "','"
                + ShopTaskState.SUBMITTED.name() + "','" + ShopTaskState.FINISHED.name() + "','"
                + ShopTaskState.EXPIRED.name() + "') then st.point else 0 end)*100, 2), 0) end rate")
        .from(PShopTask.TABLE_NAME, "st")
        .where(Predicates.equals("st", PShopTask.PLAN_TYPE, TaskPlanType.ASSIGNABLE.name()))
        .where(Predicates.isNotNull("st", PShopTask.SHOP))
        .where(Predicates.when(startDate != null && endDate != null,
            Predicates.between("st", PShopTask.PLAN_START_TIME, startDate, endDate)))
        .where(
            Predicates.when(StringUtils.isNotEmpty(shopKeyWord), Predicates.equals("st", PShopTask.SHOP, shopKeyWord)))
        .groupBy("st.tenant", "st.shop", "st.shop_code", "st.shop_name")
        .build();
    SelectStatement selectStatement = new SelectBuilder()
        .select("a.tenant", "a.shop", "a.shop_code", "a.shop_name", "a.finished", "a.total", "a.score", "a.point",
            "a.rate", "(@j:=@j+1) as rowNum", "@k:=(case when @pre_rate = a.rate then @k else @j end) as 'rank'",
            " @pre_rate:=a.rate as preRate")
        .from(build, "a")
        .crossJoin("(SELECT @j:=0,@k:=0,@pre_rate:=0)", "t", null)
        .orderBy("a.rate", false)
        .limit(page, pageSize)
        .build();
    return jdbcTemplate.query(selectStatement, NEW_SUMMARY_MAPPER);
  }

  public List<ShopTaskLine> query(String tenant, String shopKeyword, Date startDate, Date endDate, String planKeyword) {
    if (StringUtils.isEmpty(tenant)) {
      return null;
    }
    SelectStatement selectStatement = new SelectBuilder()
        .select("st.shop", "st.shop_code", "st.shop_name", "st.plan_code", "st.plan_name", "st.plan_period",
            "st.group_name", "stl.operator_name", "stl.operator_id", "stl.name as item_name", "stl.note",
            "stl.`point` - stl.score as cut_point", "stl.score", "stl.`point`", "stl.feedback")
        .from(PShopTaskLine.TABLE_NAME, "st")
        .crossJoin("shop_task_log", "stl",
            Predicates.and(Predicates.equals("st", "tenant", "stl", PShopTaskLog.TENANT),
                Predicates.equals("st", "uuid", "stl", PShopTaskLog.OWNER)))
        .where(Predicates.equals("st", "tenant", tenant))
        .where(Predicates.equals("st", "plan_type", TaskPlanType.INSPECTION.name()))
        .where(Predicates.when(StringUtils.isNotEmpty(planKeyword),
            Predicates.or(Predicates.like("st", PShopTaskLine.PLAN_CODE, planKeyword),
                Predicates.like("st", PShopTask.PLAN_NAME, planKeyword))))
        .where(Predicates.when(StringUtils.isNotEmpty(shopKeyword),
            Predicates.or(Predicates.like(PShopTaskLine.SHOP_NAME, shopKeyword),
                Predicates.like(PShopTaskLine.SHOP_CODE, shopKeyword))))
        .where(Predicates.when(startDate != null && endDate != null,
            Predicates.between("st", "plan_start_time", startDate, endDate)))
        .orderBy("st.shop_code", "st.plan_code", "st.plan_period", "st.task_group", "stl.owner", "stl.name")
        .build();

    return jdbcTemplate.query(selectStatement, LINE_MAPPER);
  }

  public List<ShopTask> listByShopIds(String tenant, ShopTaskState state, List<String> shopIdLists) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopIdLists);

    SelectBuilder selectBuilder = new SelectBuilder().from(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.in2(PShopTask.SHOP, shopIdLists.toArray()))
        .where(Predicates.equals(PShopTask.PLAN_TYPE, TaskPlanType.ASSIGNABLE.name()))
        .orderBy(PShopTask.SHOP, false);
    if (state == ShopTaskState.FINISHED) {
      selectBuilder.where(Predicates.equals(PShopTask.STATE, ShopTaskState.FINISHED.name()));
    } else if (state == null) {
      selectBuilder.where(Predicates.notEquals(PShopTask.STATE, ShopTaskState.TERMINATE.name()));
    } else {
    }
    return jdbcTemplate.query(selectBuilder.build(), MAPPER);
  }

  public List<AssignableShopTaskCount> getCountByState(String tenant, List<String> operators, ShopTaskState state) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(operators);
    Assert.notNull(state);
    SelectStatement selectStatement = new SelectBuilder()
        .select(PShopTask.OPERATOR_ID, "count(_shop_task.operator_id) as count")
        .from(PShopTask.TABLE_NAME, PShopTask.TABLE_ALIAS)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.PLAN_TYPE, TaskPlanType.ASSIGNABLE.name()))
        .where(Predicates.in2(PShopTask.OPERATOR_ID, operators.toArray()))
        .groupBy(PShopTask.OPERATOR_ID)
        .build();
    if (state == ShopTaskState.ALL) {
      selectStatement.where(Predicates.notEquals(PShopTask.STATE, ShopTaskState.TERMINATE.name()));
    } else {
      selectStatement.where(Predicates.equals(PShopTask.STATE, state.name()));
    }
    return jdbcTemplate.query(selectStatement, COUNT_MAPPER);
  }

  public void changeShopTaskOperator(String tenant, ShopTask shopTask, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTask);

    UpdateBuilder update = new UpdateBuilder().table(PShopTask.TABLE_NAME)
        .setValue(PShopTask.OPERATOR_ID, operateInfo.getOperator().getId())
        .setValue(PShopTask.OPERATOR_NAME, operateInfo.getOperator().getFullName())
        .setValue(PShopTask.OPERATOR_POSITION_CODE, shopTask.getOperatorPositionCode())
        .setValue(PShopTask.OPERATOR_POSITION_NAME, shopTask.getOperatorPositionName())
        .setValue(PShopTask.STARTED, new Date())
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, shopTask.getUuid()));
    if (operateInfo != null) {
      shopTask.setLastModifyInfo(operateInfo);
    }
    jdbcTemplate.update(update.build());
  }

  public ShopTask getWithLock(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");

    SelectStatement selectStatement = new SelectBuilder().from(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, uuid))
        .forUpdate()
        .build();
    return getFirst(jdbcTemplate.query(selectStatement, MAPPER));
  }

  public void grabOrder(String tenant, ShopTask shopTaskReq, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskReq.getUuid(), "uuid");
    Assert.hasText(shopTaskReq.getShop(), "shop");
    Assert.hasText(shopTaskReq.getShopCode(), "shopCode");
    Assert.hasText(shopTaskReq.getShopName(), "shopName");
    Assert.notNull(operateInfo);

    UpdateBuilder update = new UpdateBuilder().table(PShopTask.TABLE_NAME)
        .setValue(PShopTask.OPERATOR_ID, operateInfo.getOperator().getId())
        .setValue(PShopTask.OPERATOR_NAME, operateInfo.getOperator().getFullName())
        .setValue(PShopTask.GRAB_ORDER_TIME, new Date())
        .setValue(PShopTask.SHOP, shopTaskReq.getShop())
        .setValue(PShopTask.SHOP_CODE, shopTaskReq.getShopCode())
        .setValue(PShopTask.SHOP_NAME, shopTaskReq.getShopName())
        .setValue(PShopTask.STARTED, new Date())
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, shopTaskReq.getUuid()));
    jdbcTemplate.update(update.build());
  }

  public long querySummaryCount(String tenant, Date startDate, Date endDate, String shopKeyWord) {
    Assert.notNull(tenant, "tenant");
    SelectStatement build = new SelectBuilder()
        .select("st.tenant", "st.shop", "st.shop_code", "st.shop_name",
            "sum(case when st.state='" + ShopTaskState.FINISHED.name() + "' then 1 else 0 end) as finished",
            "sum(case when st.state in ('" + ShopTaskState.UNFINISHED.name() + "'," + "'"
                + ShopTaskState.SUBMITTED.name() + "'" + ",'" + ShopTaskState.FINISHED.name() + "','"
                + ShopTaskState.EXPIRED.name() + "') then 1 else 0 end) as total",
            "sum(case when st.state = '" + ShopTaskState.FINISHED.name() + "' then st.score else 0 end ) as score",
            "sum(case when st.state in ('" + ShopTaskState.UNFINISHED.name() + "','" + ShopTaskState.SUBMITTED.name()
                + "','" + ShopTaskState.FINISHED.name() + "','" + ShopTaskState.EXPIRED.name()
                + "') then st.point else 0 end) as point",
            "case when sum(case when st.state in ('" + ShopTaskState.UNFINISHED.name() + "','"
                + ShopTaskState.SUBMITTED.name() + "','" + ShopTaskState.FINISHED.name() + "','"
                + ShopTaskState.EXPIRED.name() + "') then 1 else 0 end) =0 then 0 else "
                + "IFNULL(round(sum(case when st.state='" + ShopTaskState.FINISHED.name()
                + "' then st.score else 0 end)/sum(case when st.state in ('" + ShopTaskState.UNFINISHED.name() + "','"
                + ShopTaskState.SUBMITTED.name() + "','" + ShopTaskState.FINISHED.name() + "','"
                + ShopTaskState.EXPIRED.name() + "') then st.point else 0 end)*100, 2), 0) end rate")
        .from(PShopTask.TABLE_NAME, "st")
        .where(Predicates.equals("st", PShopTask.PLAN_TYPE, TaskPlanType.ASSIGNABLE.name()))
        .where(Predicates.isNotNull("st", PShopTask.SHOP))
        .where(Predicates.when(startDate != null && endDate != null,
            Predicates.between("st", PShopTask.PLAN_START_TIME, startDate, endDate)))
        .where(
            Predicates.when(StringUtils.isNotEmpty(shopKeyWord), Predicates.equals("st", PShopTask.SHOP, shopKeyWord)))
        .groupBy("st.tenant", "st.shop", "st.shop_code", "st.shop_name")
        .build();
    SelectStatement selectStatement = new SelectBuilder().select("count(a.shop) as count", "1 as operator_id")
        .from(build, "a")
        .orderBy("a.rate", false)
        .build();
    AssignableShopTaskCount first = getFirst(jdbcTemplate.query(selectStatement, COUNT_MAPPER));
    return first != null ? first.getCount() : 0;
  }

  public void expireShopTask(String tenant) {
    Assert.notNull(tenant, "tenant");
    UpdateBuilder updateBuilder = new UpdateBuilder().table(PShopTask.TABLE_NAME)
        .setValue(PShopTask.STATE, ShopTaskState.EXPIRED.name())
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.STATE, ShopTaskState.UNFINISHED.name()))
        .where(Predicates.lessOrEquals(PShopTask.PLAN_END_TIME, new Date()))
        .where(Predicates.equals(PShopTask.PLAN_TYPE, TaskPlanType.ASSIGNABLE.name()));
    jdbcTemplate.update(updateBuilder.build());
  }

  public List<ShopTask> listAssignable(String tenant, Date remindTime) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(remindTime, "remindTime");
    SelectStatement select = new SelectBuilder().from(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.REMIND_TIME, remindTime))
        .where(Predicates.equals(PShopTask.STATE, ShopTaskState.UNFINISHED.name()))
        .where(Predicates.equals(PShopTask.PLAN_TYPE, TaskPlanType.ASSIGNABLE.name()))
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }

  /**
   * 方法实现的sql SELECT count( a.rate ) AS count FROM ( SELECT CASE
   * <p>
   * WHEN sum( CASE WHEN st.state IN ( 'UNFINISHED', 'SUBMITTED', 'FINISHED',
   * 'EXPIRED' ) THEN 1 ELSE 0 END ) = 0 THEN 0 ELSE IFNULL( round( sum( CASE WHEN
   * st.state = 'FINISHED' THEN st.score ELSE 0 END ) / sum( CASE WHEN st.state IN
   * ( 'UNFINISHED', 'SUBMITTED', 'FINISHED', 'EXPIRED' ) THEN st.point ELSE 0 END
   * ) * 100, 2 ), 0 ) END rate FROM shop_task st WHERE st.plan_type =
   * 'ASSIGNABLE' AND st.shop IS NOT NULL
   * <p>
   * AND st.plan_start_time BETWEEN '2021-01-28 15:38:00' AND '2022-05-28
   * 23:00:00'
   * <p>
   * GROUP BY st.tenant, st.shop, st.shop_code, st.shop_name ) a WHERE a.rate >
   * '2.15' ORDER BY a.rate DESC;
   *
   * @param tenant    租户
   * @param startDate 开始日期
   * @param endDate   结束日期
   * @param rate      指定数据的达成率
   * @return 符合条件的总的数量即排名
   */
  public long queryCountGreaterRate(String tenant, Date startDate, Date endDate, BigDecimal rate) {
    Assert.notNull(tenant, "tenant");
    SelectStatement build = new SelectBuilder()
        .select("case when sum(case when st.state in ('" + ShopTaskState.UNFINISHED.name() + "','"
            + ShopTaskState.SUBMITTED.name() + "','" + ShopTaskState.FINISHED.name() + "','"
            + ShopTaskState.EXPIRED.name() + "') then 1 else 0 end) =0 then 0 else "
            + "IFNULL(round(sum(case when st.state='" + ShopTaskState.FINISHED.name()
            + "' then st.score else 0 end)/sum(case when st.state in ('" + ShopTaskState.UNFINISHED.name() + "','"
            + ShopTaskState.SUBMITTED.name() + "','" + ShopTaskState.FINISHED.name() + "','"
            + ShopTaskState.EXPIRED.name() + "') then st.point else 0 end)*100, 2), 0) end rate")
        .from(PShopTask.TABLE_NAME, "st")
        .where(Predicates.equals("st", PShopTask.PLAN_TYPE, TaskPlanType.ASSIGNABLE.name()))
        .where(Predicates.isNotNull("st", PShopTask.SHOP))
        .where(Predicates.when(startDate != null && endDate != null,
            Predicates.between("st", PShopTask.PLAN_START_TIME, startDate, endDate)))
        .groupBy("st.tenant", "st.shop", "st.shop_code", "st.shop_name")
        .build();
    SelectStatement selectStatement = new SelectBuilder().select("count(a.rate) as count", "1 as operator_id")
        .from(build, "a")
        .where(Predicates.greater("a", "rate", rate))
        .orderBy("a.rate", false)
        .build();
    AssignableShopTaskCount first = getFirst(jdbcTemplate.query(selectStatement, COUNT_MAPPER));
    return first != null ? first.getCount() : 0;
  }

  public List<ShopTask> listByUUIDs(String tenant, List<String> shopTaskIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(shopTaskIds, "shopTaskIds");

    SelectStatement selectStatement = new SelectBuilder()
        .from(PShopTask.TABLE_NAME)
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.in2(PShopTask.UUID, shopTaskIds.toArray())).build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public void changeShopTaskState(String tenant, String uuid, ShopTaskState state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(operateInfo);
    Assert.notNull(state);

    UpdateBuilder update = new UpdateBuilder().table(PShopTask.TABLE_NAME)
        .setValue(PShopTask.STATE, state.name())
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.UUID, uuid));
    if (ShopTaskState.FINISHED == state) {
      update.setValue(PShopTask.FINISH_INFO_TIME, operateInfo.getTime())
          .setValue(PShopTask.FINISH_INFO_OPERATOR_FULL_NAME, operateInfo.getOperator().getFullName())
          .setValue(PShopTask.FINISH_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
          .setValue(PShopTask.FINISH_INFO_OPERATOR_NAMESPACE, operateInfo.getOperator().getNamespace());
    }
    jdbcTemplate.update(update.build());
  }
}
