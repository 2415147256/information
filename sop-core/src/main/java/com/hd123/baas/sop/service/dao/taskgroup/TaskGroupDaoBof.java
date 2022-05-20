package com.hd123.baas.sop.service.dao.taskgroup;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroup;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupState;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupType;
import com.hd123.baas.sop.service.api.taskgroup.TaskTemplate;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.*;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class TaskGroupDaoBof extends BofBaseDao {

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TaskGroup.class, PTaskGroup.class)
      .addConditionProcessor(new QueryConditionProcessor() {
        @Override
        public Predicate process(QueryCondition condition, QueryProcessContext queryProcessContext)
            throws IllegalArgumentException, QueryProcessException {
          if (condition == null) {
            return null;
          }
          String operation = condition.getOperation();
          String shop = TaskGroup.Queries.SHOP_EQUALS;
          String alias = queryProcessContext.getPerzAlias();
          if (StringUtils.equals(condition.getOperation(), TaskGroup.Queries.SHOP_EQUALS)) {
            List<String> value = (List<String>) condition.getParameter();
            SelectBuilder select = new SelectBuilder().select("1")
                .from(PTaskGroupShop.TABLE_NAME, PTaskGroupShop.TABLE_ALIAS)
                .where(Predicates.in2(PTaskGroupShop.SHOP, value.toArray()))
                .where(Predicates.equals(getTableField(PTaskGroupShop.TABLE_ALIAS, PTaskGroupShop.TASK_GROUP),
                    Expr.valueOf(getTableField(alias, PTaskGroup.UUID))));
            return Predicates.exists(select.build());
          }
          if (StringUtils.equals(condition.getOperation(), TaskGroup.Queries.KEYWORD_LIKE)) {
            if (condition.getParameter() != null) {
              return Predicates.or(Predicates.like(PTaskGroup.CODE, condition.getParameter()),
                  Predicates.like(PTaskGroup.NAME, condition.getParameter()));
            }
          }
          return null;
        }
      })
      .build();

  public static final TEMapper<TaskGroup> TASK_GROUP_MAPPER = TEMapperBuilder.of(TaskGroup.class, PTaskGroup.class)
      .primaryKey(PTaskGroup.UUID)
      // 增加枚举转换支持
      .map("type", PTaskGroup.TYPE, EnumConverters.toString(TaskGroupType.class),
          EnumConverters.toEnum(TaskGroupType.class))
      .build();

  public static final TEMapper<TaskTemplate> MAPPER = TEMapperBuilder.of(TaskTemplate.class, PTaskTemplate.class)
      .primaryKey(PTaskTemplate.UUID)
      .build();

  @Autowired
  protected JdbcPagingQueryExecutor executor;

  public int insert(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskGroup, "任务组");
    Assert.notNull(taskGroup.getName(), "任务组名");
    Assert.notNull(taskGroup.getUuid(), "uuid");
    taskGroup.setTenant(tenant);
    InsertBuilder insert = new InsertBuilder().table(PTaskGroup.TABLE_NAME)
        .addValues(PEntity.forSaveNew(taskGroup))
        .addValue(PTaskGroup.NAME, taskGroup.getName())
        .addValue(PTaskGroup.REMIND_TIME, taskGroup.getRemindTime())
        .addValue(PTaskGroup.TENANT, tenant)
        .addValue(PTaskGroup.DESCRIPTION, taskGroup.getDescription())
        .addValue(PTaskGroup.ORG_ID, taskGroup.getOrgId())
        .addValue(PTaskGroup.TYPE, taskGroup.getType().name());
    if (operateInfo != null) {
      insert.addValues(PStandardEntity.toCreateInfoFieldValues(operateInfo));
      insert.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    return jdbcTemplate.update(insert.build());
  }

  public int insertNew(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskGroup, "任务组");
    Assert.notNull(taskGroup.getName(), "任务组名");
    Assert.notNull(taskGroup.getUuid(), "uuid");
    taskGroup.setTenant(tenant);
    InsertStatement insert = buildInsertStatement(tenant, taskGroup, operateInfo);

    return jdbcTemplate.update(insert);
  }

  public int update(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskGroup, "任务组");
    Assert.notNull(taskGroup.getName(), "任务组名");
    Assert.notNull(taskGroup.getUuid(), "uuid");

    UpdateBuilder update = new UpdateBuilder().table(PTaskGroup.TABLE_NAME)
        .setValue(PTaskGroup.NAME, taskGroup.getName())
        .setValue(PTaskGroup.REMIND_TIME, taskGroup.getRemindTime())
        .setValue(PTaskGroup.DESCRIPTION, taskGroup.getDescription())
        .setValue(PTaskGroup.TYPE, taskGroup.getType().name())
        .where(Predicates.equals(PTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PTaskGroup.UUID, taskGroup.getUuid()));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    return jdbcTemplate.update(update.build());
  }

  public TaskGroup get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");

    SelectBuilder select = new SelectBuilder().from(PTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PTaskGroup.UUID, uuid))
        .limit(1);
    List<TaskGroup> query = jdbcTemplate.query(select.build(), TASK_GROUP_MAPPER);
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    return query.get(0);
  }

  public QueryResult<TaskGroup> query(QueryDefinition qd) {
    Assert.notNull(qd, "qd");

    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    return executor.query(selectStatement, TASK_GROUP_MAPPER);
  }

  public int delete(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuids");
    DeleteBuilder deleteBuilder = new DeleteBuilder().table(PTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PTaskGroup.UUID, uuid));
    return jdbcTemplate.update(deleteBuilder.build());
  }

  public TaskGroup getByName(String tenant, String name) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(name, "任务名称");
    SelectBuilder select = new SelectBuilder().from(PTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PTaskGroup.NAME, name));
    List<TaskGroup> query = jdbcTemplate.query(select.build(), TASK_GROUP_MAPPER);
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    return query.get(0);
  }

  public List<TaskGroup> getByShops(String tenant, List<String> shops) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shops, "门店id");
    SelectBuilder select = new SelectBuilder().from(PTaskGroup.TABLE_NAME, PTaskGroup.TABLE_ALIAS)
        .innerJoin(PTaskGroupShop.TABLE_NAME, PTaskGroupShop.TABLE_ALIAS,
            Predicates.equals(PTaskGroupShop.TABLE_ALIAS, PTaskGroupShop.TASK_GROUP, PTaskGroup.TABLE_ALIAS,
                PTaskGroup.UUID))
        .where(Predicates.in(PTaskGroupShop.TABLE_ALIAS, PTaskGroupShop.SHOP, shops.toArray()))
        .where(Predicates.equals(PTaskGroupShop.TABLE_ALIAS, PTaskGroupShop.TENANT, tenant));
    List<TaskGroup> result = jdbcTemplate.query(select.build(), TASK_GROUP_MAPPER);
    if (CollectionUtils.isEmpty(result)) {
      return null;
    }
    return result;
  }

  public List<TaskGroup> getByShop(String tenant, String shop) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shop, "门店id");
    SelectBuilder select = new SelectBuilder().from(PTaskGroup.TABLE_NAME, PTaskGroup.TABLE_ALIAS)
        .innerJoin(PTaskGroupShop.TABLE_NAME, PTaskGroupShop.TABLE_ALIAS,
            Predicates.equals(PTaskGroupShop.TABLE_ALIAS, PTaskGroupShop.TASK_GROUP, PTaskGroup.TABLE_ALIAS,
                PTaskGroup.UUID))
        .where(Predicates.equals(PTaskGroupShop.TABLE_ALIAS, PTaskGroupShop.SHOP, shop))
        .where(Predicates.equals(PTaskGroupShop.TABLE_ALIAS, PTaskGroupShop.TENANT, tenant));
    List<TaskGroup> result = jdbcTemplate.query(select.build(), TASK_GROUP_MAPPER);
    if (CollectionUtils.isEmpty(result)) {
      return null;
    }
    return result;
  }

  public List<TaskGroup> queryByPage(String tenant, int page, int size) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(page, "页码");
    Assert.notNull(size, "大小");
    SelectBuilder select = new SelectBuilder().from(PTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PTaskGroup.TENANT, tenant))
        .limit(page, size);
    return jdbcTemplate.query(select.build(), TASK_GROUP_MAPPER);
  }

  private String getTableField(String tableAlias, String field) {
    return tableAlias + "." + field;
  }

  public int updateState(String tenant, String uuid, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    UpdateBuilder update = new UpdateBuilder().table(PTaskGroup.TABLE_NAME)
        .setValue(PTaskGroup.STATE, TaskGroupState.SUBMITTED.name())
        .where(Predicates.equals(PTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PTaskGroup.UUID, uuid));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    return jdbcTemplate.update(update.build());
  }

  public int updateTaskGroupName(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(taskGroup.getUuid(), "uuid");
    Assert.notNull(taskGroup.getName(), "name");

    UpdateBuilder update = new UpdateBuilder().table(PTaskGroup.TABLE_NAME)
        .setValue(PTaskGroup.NAME, taskGroup.getName())
        .where(Predicates.equals(PTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PTaskGroup.UUID, taskGroup.getUuid()));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    return jdbcTemplate.update(update.build());
  }

  public TaskTemplate getTaskTemplate(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");

    SelectStatement selectStatement = new SelectBuilder().from(PTaskTemplate.TABLE_NAME)
        .where(Predicates.equals(PTaskTemplate.UUID, uuid))
        .where(Predicates.equals(PTaskTemplate.TENANT, tenant))
        .limit(1)
        .build();
    List<TaskTemplate> query = jdbcTemplate.query(selectStatement, MAPPER);
    return query.isEmpty() ? null : query.get(0);
  }

  public TaskTemplate getTaskTemplate(String tenant, String ownerUuid, String name) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(ownerUuid, "ownerUuid");
    Assert.notNull(name, "name");

    SelectStatement selectStatement = new SelectBuilder().from(PTaskTemplate.TABLE_NAME)
        .where(Predicates.equals(PTaskTemplate.OWNER, ownerUuid))
        .where(Predicates.equals(PTaskTemplate.TENANT, tenant))
        .where(Predicates.equals(PTaskTemplate.NAME, name))
        .limit(1)
        .build();
    List<TaskTemplate> query = jdbcTemplate.query(selectStatement, MAPPER);
    return query.isEmpty() ? null : query.get(0);
  }

  public List<TaskGroup> listByCodes(String tenant, List<String> codes) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(codes, "codes");
    SelectStatement selectStatement = new SelectBuilder().from(PTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PTaskGroup.TENANT, tenant))
        .where(Predicates.in2(PTaskGroup.CODE, codes.toArray()))
        .orderBy(PTaskGroup.CREATE_INFO_TIME, false)
        .build();
    return jdbcTemplate.query(selectStatement, TASK_GROUP_MAPPER);
  }

  /**
   * 根据租户查询所有
   *
   * @param tenant               租户
   * @param type                 任务类型
   * @param importTaskGroupNames 任务组名称集合
   * @return
   */
  public List<TaskGroup> listByTypeAndName(String tenant, String type, List<String> importTaskGroupNames) {
    Assert.hasText(tenant, "tenant");
    SelectStatement selectStatement = new SelectBuilder().from(PTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PTaskGroup.TYPE, type))
        .where(Predicates.in2(PTaskGroup.NAME, importTaskGroupNames.toArray()))
        .build();
    return jdbcTemplate.query(selectStatement, TASK_GROUP_MAPPER);
  }

  /**
   * 批量插入
   *
   * @param tenant              租户
   * @param insertTaskGroupList 任务组对象(巡检对象)
   * @param operateInfo         操作人
   */
  public void saveBatch(String tenant, Collection<TaskGroup> insertTaskGroupList, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(insertTaskGroupList)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (TaskGroup taskGroup : insertTaskGroupList) {
      statements.add(buildInsertStatement(tenant, taskGroup, operateInfo));
    }
    batchUpdate(statements);
  }

  /**
   * 构建输入流
   *
   * @param tenant      租户
   * @param taskGroup   taskGroup
   * @param operateInfo 操作嗯
   * @return
   */
  private InsertStatement buildInsertStatement(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) {
    InsertBuilder insert = new InsertBuilder().table(PTaskGroup.TABLE_NAME)
        .addValues(PEntity.forSaveNew(taskGroup))
        .addValue(PTaskGroup.NAME, taskGroup.getName())
        .addValue(PTaskGroup.STATE, taskGroup.getState())
        .addValue(PTaskGroup.TENANT, tenant)
        .addValue(PTaskGroup.TYPE, taskGroup.getType().name())
        .addValue(PTaskGroup.CODE, taskGroup.getCode());
    if (operateInfo != null) {
      insert.addValues(PStandardEntity.toCreateInfoFieldValues(operateInfo));
      insert.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    return insert.build();
  }
}
