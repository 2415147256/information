package com.hd123.baas.sop.service.dao.taskplan;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.taskplan.TaskPlan;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanState;
import com.hd123.baas.sop.service.dao.taskgroup.PTaskGroup;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.qd.QueryConditionProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessContext;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessException;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.Predicate;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Repository
public class TaskPlanDaoBof extends BofBaseDao {

  public static final TaskPlanMapper TASK_PLAN_MAPPER = new TaskPlanMapper();
  public static final TaskPlanTenantMapper TASK_PLAN_TENANT_MAPPER = new TaskPlanTenantMapper();

  public static final TEMapper<TaskPlan> MAPPER = TEMapperBuilder.of(TaskPlan.class, PTaskPlan.class)
      .primaryKey(PTaskPlan.UUID)
      // 增加枚举转换支持
      .map("state", PTaskPlan.STATE, //
          EnumConverters.toString(TaskPlanState.class), EnumConverters.toEnum(TaskPlanState.class))
      .build();

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TaskPlan.class, PTaskPlan.class)
      .addConditionProcessor(new QueryConditionProcessor() {
        @Override
        public Predicate process(QueryCondition condition, QueryProcessContext queryProcessContext)
            throws IllegalArgumentException, QueryProcessException {
          String alias = queryProcessContext.getPerzAlias();
          if (StringUtils.equals(condition.getOperation(), TaskPlan.Queries.TYPE_EQUALS)) {
            String type = (String) condition.getParameter();
            SelectBuilder select = new SelectBuilder().select(PTaskGroup.UUID)
                .from(PTaskGroup.TABLE_NAME, PTaskGroup.TABLE_ALIAS)
                .where(Predicates.equals(PTaskGroup.TABLE_ALIAS, PTaskGroup.TYPE, type));
            return Predicates.in(queryProcessContext.getPerzAlias(), PTaskPlan.TASK_GROUP, select.build());
          }
          if (org.apache.commons.lang.StringUtils.equals(condition.getOperation(), TaskPlan.Queries.KEYWORD_LIKE)) {
            if (condition.getParameter() != null) {
              return Predicates.or(Predicates.like(PTaskPlan.CODE, condition.getParameter()),
                  Predicates.like(PTaskPlan.NAME, condition.getParameter()));
            }
          }
          // 处理生效时间范围
          if (TaskPlan.Queries.EFFECT_TIME_BTW.equals(condition.getOperation()) && condition.getParameter() != null) {
            List<Object> parameter = condition.getParameters();
            return Predicates.and(Predicates.greaterOrEquals(PTaskPlan.START_DATE, parameter.get(0)),
                Predicates.lessOrEquals(PTaskPlan.START_DATE, parameter.get(1)));
          }
          // 处理门店查询信息
          if (TaskPlan.Queries.SHOP_KEYWORD_LIKE.equals(condition.getOperation()) && condition.getParameter() != null) {
            SelectStatement selectStatement = new SelectBuilder().select(PTaskPlanItem.TABLE_ALIAS + ".owner")
                .from(PTaskPlanItem.TABLE_NAME, PTaskPlanItem.TABLE_ALIAS)
                .where(Predicates.equals(alias, PTaskPlan.UUID, PTaskPlanItem.TABLE_ALIAS, PTaskPlanItem.OWNER))
                .where(Predicates.equals(alias, PTaskPlan.TENANT, PTaskPlanItem.TABLE_ALIAS, PTaskPlanItem.TENANT))
                .where(Predicates.like(PTaskPlanItem.SHOPS, condition.getParameter()))
                .build();
            return Predicates.in2(PTaskPlan.UUID, selectStatement);
          }
          return null;
        }
      })
      .build();

  public int insert(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskPlan, "任务");
    if (taskPlan.getUuid() == null) {
      taskPlan.setUuid(UUID.randomUUID().toString());
    }
    if (operateInfo != null) {
      taskPlan.setCreateInfo(operateInfo);
      taskPlan.setLastModifyInfo(operateInfo);
    }
    if (taskPlan.getState() == null) {
      taskPlan.setState(TaskPlanState.UN_EFFECTIVE);
    }
    InsertBuilder insert = new InsertBuilder().table(PTaskPlan.TABLE_NAME).addValues(MAPPER.forInsert(taskPlan));
    return jdbcTemplate.update(insert.build());
  }

  public int update(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskPlan, "任务");
    Assert.notNull(taskPlan.getUuid(), "uuid");
    UpdateBuilder update = new UpdateBuilder().table(PTaskPlan.TABLE_NAME)
        .setValue(PTaskPlan.NAME, taskPlan.getName())
        .setValue(PTaskPlan.WORD_NEEDED, taskPlan.isWordNeeded())
        .setValue(PTaskPlan.IMAGE_NEEDED, taskPlan.isImageNeeded())
        .setValue(PTaskPlan.REMIND_TIME, taskPlan.getRemindTime())
        .setValue(PTaskPlan.DESCRIPTION, taskPlan.getDescription())
        .setValue(PTaskPlan.PLAN_TIME, taskPlan.getPlanTime())
        .setValue(PTaskPlan.STATE, TaskPlanState.UN_EFFECTIVE.name())
        .setValue(PTaskPlan.START_DATE, taskPlan.getStartDate())
        .setValue(PTaskPlan.END_DATE, taskPlan.getEndDate())
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.UUID, taskPlan.getUuid()));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    return jdbcTemplate.update(update.build());
  }

  public TaskPlan get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    SelectBuilder select = new SelectBuilder().from(PTaskPlan.TABLE_NAME)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.UUID, uuid));
    List<TaskPlan> query = jdbcTemplate.query(select.build(), MAPPER);
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    return query.get(0);
  }

  public TaskPlan getByGroupIdAndName(String tenant, String groupId, String name) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "groupId");
    Assert.notNull(name, "名称");
    SelectBuilder select = new SelectBuilder().from(PTaskPlan.TABLE_NAME)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.TASK_GROUP, groupId))
        .where(Predicates.equals(PTaskPlan.NAME, name));
    List<TaskPlan> query = jdbcTemplate.query(select.build(), TASK_PLAN_MAPPER);
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    return query.get(0);
  }

  public List<TaskPlan> list(String tenant, String groupId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "groupId");
    SelectBuilder select = new SelectBuilder().from(PTaskPlan.TABLE_NAME)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.TASK_GROUP, groupId))
        .orderBy(PTaskPlan.SORT, true);
    List<TaskPlan> query = jdbcTemplate.query(select.build(), TASK_PLAN_MAPPER);
    return query;
  }

  public void setTaskPlanEnforced(String tenant, String groupId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "任务组id");
    UpdateBuilder update = new UpdateBuilder().table(PTaskPlan.TABLE_NAME)
        .setValue(PTaskPlan.STATE, TaskPlanState.EFFECTIVE.name())
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.TASK_GROUP, groupId))
        .where(Predicates.equals(PTaskPlan.STATE, TaskPlanState.UN_EFFECTIVE.name()));
    jdbcTemplate.update(update.build());
  }

  public void delete(String tenant, String group, String uuid) {
    Assert.notNull(uuid, "uuid");
    Assert.notNull(group, "group");
    Assert.notNull(tenant, "tenant");
    DeleteBuilder delete = new DeleteBuilder().table(PTaskPlan.TABLE_NAME)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.TASK_GROUP, group))
        .where(Predicates.equals(PTaskPlan.UUID, uuid));
    jdbcTemplate.update(delete.build());
  }

  public void deleteByUuid(String tenant, String uuid) {
    Assert.notNull(uuid, "uuid");
    Assert.notNull(tenant, "tenant");
    DeleteBuilder delete = new DeleteBuilder().table(PTaskPlan.TABLE_NAME)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.UUID, uuid));
    jdbcTemplate.update(delete.build());
  }

  public void deleteByGroupId(String tenant, String groupId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "任务组");
    DeleteBuilder delete = new DeleteBuilder().table(PTaskPlan.TABLE_NAME)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.TASK_GROUP, groupId));
    jdbcTemplate.update(delete.build());
  }

  public TaskPlan maxSort(String tenant, String groupId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "任务组");
    SelectBuilder select = new SelectBuilder().from(PTaskPlan.TABLE_NAME)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.TASK_GROUP, groupId))
        .orderBy(PTaskPlan.SORT, false)
        .limit(1);
    List<TaskPlan> query = jdbcTemplate.query(select.build(), TASK_PLAN_MAPPER);
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    return query.get(0);
  }

  public void setSort(String tenant, String uuid, int i) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    UpdateBuilder update = new UpdateBuilder().table(PTaskPlan.TABLE_NAME)
        .setValue(PTaskPlan.SORT, i)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.UUID, uuid));
    jdbcTemplate.update(update.build());
  }

  public QueryResult<TaskPlan> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "qd");
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    return executor.query(selectStatement, MAPPER);
  }

  public void overdue(String tenant, String group, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(group, "任务组");
    Assert.notNull(uuid, "uuid");
    UpdateBuilder update = new UpdateBuilder().table(PTaskPlan.TABLE_NAME, PTaskPlan.TABLE_ALIAS)
        .setValue(PTaskPlan.STATE, TaskPlanState.OVERDUE.name())
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.TASK_GROUP, group))
        .where(Predicates.equals(PTaskPlan.UUID, uuid));
    jdbcTemplate.update(update.build());
  }

  public void updateSingleRemindDate(String tenant, String uuid, Date remindDate, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(remindDate, "remindDate");

    UpdateBuilder update = new UpdateBuilder().table(PTaskPlan.TABLE_NAME, PTaskPlan.TABLE_ALIAS)
        .setValue(PTaskPlan.REMIND_DATE, remindDate)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.UUID, uuid));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    jdbcTemplate.update(update.build());
  }

  public int updateTaskPlan(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskPlan, "任务");
    Assert.notNull(taskPlan.getUuid(), "uuid");

    if (operateInfo != null) {
      taskPlan.setLastModifyInfo(operateInfo);
    }

    UpdateBuilder update = new UpdateBuilder().table(PTaskPlan.TABLE_NAME)
        .addValues(MAPPER.forUpdate(taskPlan))
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.UUID, taskPlan.getUuid()));
    return jdbcTemplate.update(update.build());
  }

  public void updateState(String tenant, String uuid, String state, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(state, "state");

    UpdateBuilder update = new UpdateBuilder().table(PTaskPlan.TABLE_NAME, PTaskPlan.TABLE_ALIAS)
        .setValue(PTaskPlan.STATE, state)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.UUID, uuid));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    jdbcTemplate.update(update.build());
  }

  public void updatePublishTaskDateCollect(String tenant, String uuid, String publishTaskDateCollect) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(publishTaskDateCollect, "publishTaskDateCollect");

    UpdateBuilder update = new UpdateBuilder().table(PTaskPlan.TABLE_NAME, PTaskPlan.TABLE_ALIAS)
        .setValue(PTaskPlan.PUBLISH_TASK_DATE_COLLECT, publishTaskDateCollect)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.UUID, uuid));
    jdbcTemplate.update(update.build());
  }

  public List<TaskPlan> listTenant() {
    SelectStatement selectStatement = new SelectBuilder().from(PTaskPlan.TABLE_NAME)
        .select(PTaskPlan.TENANT)
        .distinct()
        .build();
    return jdbcTemplate.query(selectStatement, TASK_PLAN_TENANT_MAPPER);
  }

  public void updateWeeklyOrMonthlyRemindDate(String tenant, String uuid, Integer delayDay, String remindDateTime,
                                              OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(delayDay, "delayDay");
    Assert.notNull(remindDateTime, "remindDateTime");

    UpdateBuilder update = new UpdateBuilder().table(PTaskPlan.TABLE_NAME, PTaskPlan.TABLE_ALIAS)
        .setValue(PTaskPlan.DELAY_DAY, delayDay)
        .setValue(PTaskPlan.REMIND_DETAIL_TIME, remindDateTime)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.UUID, uuid));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    jdbcTemplate.update(update.build());
  }

  public List<TaskPlan> listByUuids(String tenant, List<String> planIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(planIds, "planIds");

    SelectStatement selectStatement = new SelectBuilder().from(PTaskPlan.TABLE_NAME)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.in2(PTaskPlan.UUID, planIds.toArray())).build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public void updateAdvanceEndDate(String tenant, String uuid, int advanceEndDay, int advanceEndHour, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText("uuid", "uuid");
    Assert.notNull(advanceEndDay, "advanceEndDay");
    Assert.notNull(advanceEndHour, "advanceEndHour");

    UpdateBuilder updateBuilder = new UpdateBuilder().table(PTaskPlan.TABLE_NAME)
        .setValue(PTaskPlan.ADVANCE_END_DAY, advanceEndDay)
        .setValue(PTaskPlan.ADVANCE_END_HOUR, advanceEndHour)
        .where(Predicates.equals(PTaskPlan.TENANT, tenant))
        .where(Predicates.equals(PTaskPlan.UUID, uuid));
    if (operateInfo != null) {
      updateBuilder.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    jdbcTemplate.update(updateBuilder.build());
  }
}
