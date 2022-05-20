package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.task.*;
import com.hd123.baas.sop.service.dao.taskgroup.PTaskTemplate;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class ShopTaskLogDaoBof extends BofBaseDao {

  private static TEMapper<ShopTaskLog> MAPPER = TEMapperBuilder.of(ShopTaskLog.class, PShopTaskLog.class)
      .primaryKey(PShopTaskLog.UUID, PShopTaskLog.TENANT)
      .build();


  public List<ShopTaskLog> list(String tenant, String shopTaskId) {
    /*
      SELECT _shop_task_log.*
      FROM shop_task_log AS _shop_task_log
      LEFT JOIN shop_task AS _shop_task ON (_shop_task_log.owner = _shop_task.uuid) AND (_shop_task.tenant = ?)
      LEFT JOIN task_template AS _task_template ON (_task_template.owner = _shop_task.task_group)
           AND (_task_template.name = _shop_task_log.name) AND (_task_template.tenant = ?)
      WHERE (_shop_task_log.tenant = ?) AND (_shop_task_log.owner = ?) AND (_shop_task_log.operator_id = ?)
      ORDER BY _task_template.seq ASC
     */
    SelectStatement select = new SelectBuilder()
        .select(ExpressionUtils.of(PShopTaskLog.TABLE_ALIAS, "*"))
        .from(PShopTaskLog.TABLE_NAME, PShopTaskLog.TABLE_ALIAS)
        .leftJoin(PShopTask.TABLE_NAME, PShopTask.TABLE_ALIAS,
            Predicates.and(
                Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.OWNER, PShopTask.TABLE_ALIAS, PShopTask.UUID),
                Predicates.equals(PShopTask.TABLE_ALIAS, PShopTask.TENANT, tenant)
            )
        )
        .leftJoin(PTaskTemplate.TABLE_NAME, PTaskTemplate.TABLE_ALIAS,
            Predicates.and(
                Predicates.equals(PTaskTemplate.TABLE_ALIAS, PTaskTemplate.OWNER, PShopTask.TABLE_ALIAS, PShopTask.TASK_GROUP),
                Predicates.equals(PTaskTemplate.TABLE_ALIAS, PTaskTemplate.NAME, PShopTaskLog.TABLE_ALIAS, PShopTaskLog.NAME),
                Predicates.equals(PTaskTemplate.TABLE_ALIAS, PTaskTemplate.TENANT, tenant)
            )
        )
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.OWNER, shopTaskId))
        .orderBy(PTaskTemplate.TABLE_ALIAS, PTaskTemplate.SEQ, true)
        .orderBy(PTaskTemplate.TABLE_ALIAS, PTaskTemplate.CREATE_INFO_TIME, true)
        .build();
    return jdbcTemplate.query(select.getSql(), select.getParameters().toArray(), MAPPER);
  }

  public List<ShopTaskLog> list(String tenant, String shopTaskId, String loginId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");
    Assert.hasText(loginId, "loginId");
    /*
      SELECT _shop_task_log.*
      FROM shop_task_log AS _shop_task_log
      LEFT JOIN shop_task AS _shop_task ON (_shop_task_log.owner = _shop_task.uuid) AND (_shop_task.tenant = ?)
      LEFT JOIN task_template AS _task_template ON (_task_template.owner = _shop_task.task_group)
           AND (_task_template.name = _shop_task_log.name) AND (_task_template.tenant = ?)
      WHERE (_shop_task_log.tenant = ?) AND (_shop_task_log.owner = ?) AND (_shop_task_log.operator_id = ?)
      ORDER BY _task_template.seq ASC
     */
    SelectStatement select = new SelectBuilder()
        .select(ExpressionUtils.of(PShopTaskLog.TABLE_ALIAS, "*"))
        .from(PShopTaskLog.TABLE_NAME, PShopTaskLog.TABLE_ALIAS)
        .leftJoin(PShopTask.TABLE_NAME, PShopTask.TABLE_ALIAS,
            Predicates.and(
                Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.OWNER, PShopTask.TABLE_ALIAS, PShopTask.UUID),
                Predicates.equals(PShopTask.TABLE_ALIAS, PShopTask.TENANT, tenant)
            )
        )
        .leftJoin(PTaskTemplate.TABLE_NAME, PTaskTemplate.TABLE_ALIAS,
            Predicates.and(
                Predicates.equals(PTaskTemplate.TABLE_ALIAS, PTaskTemplate.OWNER, PShopTask.TABLE_ALIAS, PShopTask.TASK_GROUP),
                Predicates.equals(PTaskTemplate.TABLE_ALIAS, PTaskTemplate.NAME, PShopTaskLog.TABLE_ALIAS, PShopTaskLog.NAME),
                Predicates.equals(PTaskTemplate.TABLE_ALIAS, PTaskTemplate.TENANT, tenant)
            )
        )
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.OWNER, shopTaskId))
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.OPERATOR_ID, loginId))
        .orderBy(PTaskTemplate.TABLE_ALIAS, PTaskTemplate.SEQ, true)
        .orderBy(PTaskTemplate.TABLE_ALIAS, PTaskTemplate.CREATE_INFO_TIME, true)
        .build();
    return jdbcTemplate.query(select.getSql(), select.getParameters().toArray(), MAPPER);
  }

  public void update(String tenant, ShopTaskLog log, OperateInfo operateInfo) {
    log.setLastModifyInfo(operateInfo);
    log.setFinishInfo(operateInfo);
    UpdateStatement update = new UpdateBuilder().table(PShopTaskLog.TABLE_NAME)
        .addValues(MAPPER.forUpdate(log))
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals(PShopTaskLog.UUID, log.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public void batchUpdate(String tenant, List<ShopTaskLog> logs, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(logs, "logs");
    List<UpdateStatement> updates = new ArrayList<>();
    for (ShopTaskLog log : logs) {
      log.setLastModifyInfo(operateInfo);
      UpdateStatement update = new UpdateBuilder().table(PShopTaskLog.TABLE_NAME)
          .addValues(MAPPER.forUpdate(log))
          .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
          .where(Predicates.equals(PShopTaskLog.UUID, log.getUuid()))
          .build();
      updates.add(update);
    }
    batchUpdate(updates);

  }

  public ShopTaskLog get(String tenant, String uuid) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    SelectStatement statement = new SelectBuilder().from(PShopTaskLog.TABLE_NAME)
        .where(Predicates.equals(PShopTaskLog.UUID, uuid))
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .build();
    return getFirst(jdbcTemplate.query(statement, MAPPER));
  }

  public void batchInsert(String tenant, List<ShopTaskLog> logs, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(logs);
    List<InsertStatement> list = new ArrayList<>();
    for (ShopTaskLog log : logs) {
      log.setTenant(tenant);
      if (log.getUuid() == null) {
        log.setUuid(UUID.randomUUID().toString());
      }
      log.setCreateInfo(operateInfo);
      log.setLastModifyInfo(operateInfo);
      InsertBuilder insert = new InsertBuilder().table(PShopTaskLog.TABLE_NAME).addValues(MAPPER.forInsert(log));
      list.add(insert.build());
    }
    batchUpdate(list);
  }

  public void insert(String tenant, ShopTaskLog log, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(log);
    log.setTenant(tenant);
    if (log.getUuid() == null) {
      log.setUuid(UUID.randomUUID().toString());
    }
    log.setCreateInfo(operateInfo);
    log.setLastModifyInfo(operateInfo);
    InsertBuilder insert = new InsertBuilder().table(PShopTaskLog.TABLE_NAME).addValues(MAPPER.forInsert(log));
    jdbcTemplate.update(insert.build());
  }

  public int count(String tenant, List<String> owners, String state) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(owners, "owners");
    Assert.notNull(state, "state");
    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskLog.TABLE_NAME)
        .select("count(1)")
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.in2(PShopTaskLog.OWNER, owners.toArray()))
        .where(Predicates.equals(PShopTaskLog.STATE, state))
        .build();
    List<Integer> count = jdbcTemplate.query(selectStatement, new SingleColumnRowMapper<>(Integer.class));
    if (CollectionUtils.isEmpty(count)) {
      return new Integer(0);
    }
    return count.get(0);
  }

  public int countByLoginId(String tenant, List<String> owners, String state, String loginId) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(owners, "owners");
    Assert.notNull(state, "state");
    Assert.hasText(loginId, "loginId");
    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskLog.TABLE_NAME)
        .select("count(1)")
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.in2(PShopTaskLog.OWNER, owners.toArray()))
        .where(Predicates.equals(PShopTaskLog.STATE, state))
        .where(Predicates.equals(PShopTaskLog.OPERATOR_ID, loginId))
        .build();
    List<Integer> count = jdbcTemplate.query(selectStatement, new SingleColumnRowMapper<>(Integer.class));
    if (CollectionUtils.isEmpty(count)) {
      return new Integer(0);
    }
    return count.get(0);
  }

  public int count(String tenant, List<String> owners) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(owners, "owners");
    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskLog.TABLE_NAME)
        .select("count(1)")
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.in2(PShopTaskLog.OWNER, owners.toArray()))
        .build();
    List<Integer> count = jdbcTemplate.query(selectStatement, new SingleColumnRowMapper<>(Integer.class));
    if (CollectionUtils.isEmpty(count)) {
      return new Integer(0);
    }
    return count.get(0);
  }

  public int countByLoginId(String tenant, List<String> owners, String loginId) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(owners, "owners");
    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskLog.TABLE_NAME)
        .select("count(1)")
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.in2(PShopTaskLog.OWNER, owners.toArray()))
        .where(Predicates.equals(PShopTaskLog.OPERATOR_ID, loginId))
        .build();
    List<Integer> count = jdbcTemplate.query(selectStatement, new SingleColumnRowMapper<>(Integer.class));
    if (CollectionUtils.isEmpty(count)) {
      return new Integer(0);
    }
    return count.get(0);
  }

  public void changeShopTaskLogOperator(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getTransferTo(), "transferTo");
    Assert.hasText(shopTaskTransfer.getShopTaskId(), "shopTaskId");

    UpdateBuilder update = new UpdateBuilder().table(PShopTaskLog.TABLE_NAME)
        .addValue(PShopTaskLog.OPERATOR_ID, shopTaskTransfer.getTransferTo())
        .addValue(PShopTaskLog.OPERATOR_NAME, shopTaskTransfer.getTransferToName())
        .setValue(PShopTaskLog.SCORE, null)
        .setValue(PShopTaskLog.FEEDBACK, null)
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals(PShopTaskLog.UUID, shopTaskTransfer.getShopTaskLogId()));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    jdbcTemplate.update(update.build());
  }

  public void changeBatchShopTaskLogOperator(String tenant, ShopTaskTransfer shopTaskTransfer, List<String> logIds, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getTransferTo(), "transferTo");
    Assert.hasText(shopTaskTransfer.getShopTaskId(), "shopTaskId");

    UpdateBuilder update = new UpdateBuilder().table(PShopTaskLog.TABLE_NAME)
        .addValue(PShopTaskLog.OPERATOR_ID, shopTaskTransfer.getTransferTo())
        .addValue(PShopTaskLog.OPERATOR_NAME, shopTaskTransfer.getTransferToName())
        .setValue(PShopTaskLog.SCORE, null)
        .setValue(PShopTaskLog.FEEDBACK, null)
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals(PShopTaskLog.OWNER, shopTaskTransfer.getShopTaskId()))
        .where(Predicates.equals(PShopTaskLog.OPERATOR_ID, shopTaskTransfer.getTransferFrom()))
        .where(Predicates.in2(PShopTaskLog.UUID, logIds.toArray()))
        .where(Predicates.equals(PShopTaskLog.STATE, ShopTaskLogState.UNFINISHED.name()));
    if (operateInfo != null) {
      update.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    jdbcTemplate.update(update.build());
  }

  public List<ShopTaskLog> listByOwners(String tenant, List<String> owners) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(owners, "owners");

    SelectBuilder selectBuilder = new SelectBuilder().from(PShopTaskLog.TABLE_NAME)
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.in2(PShopTaskLog.OWNER, owners.toArray()))
        .orderBy(PShopTaskLog.CREATE_INFO_TIME, false);
    return jdbcTemplate.query(selectBuilder.build(), MAPPER);
  }

  public void deleteByOperateIdAndReply(String tenant, String shopTaskId, String transferFrom) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");
    Assert.hasText(transferFrom, "transferFrom");
    DeleteStatement deleteSql = new DeleteBuilder().table(PShopTaskLog.TABLE_NAME)
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals(PShopTaskLog.OWNER, shopTaskId))
        .where(Predicates.equals(PShopTaskLog.OPERATOR_ID, transferFrom))
        .where(Predicates.equals(PShopTaskLog.TYPE, ShopTaskLogType.REPLY.name()))
        .build();
    jdbcTemplate.update(deleteSql);
  }

  public List<ShopTaskLog> listUnFinished(String tenant, String ownerId, String transferFrom) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(ownerId, "ownerId");
    Assert.hasText(transferFrom, "transferFrom");

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskLog.TABLE_NAME)
        .where(Predicates.equals(PShopTaskLog.OWNER, ownerId))
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals(PShopTaskLog.STATE, ShopTaskLogState.UNFINISHED.name()))
        .where(Predicates.equals(PShopTaskLog.OPERATOR_ID, transferFrom))
        .build();

    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public List<ShopTaskLog> listUnfinishedByOperateIdAndShopTaskId(String tenant, String shopTaskId, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");

    SelectStatement build = new SelectBuilder().from(PShopTaskLog.TABLE_NAME, PShopTaskLog.TABLE_ALIAS)
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.OWNER, shopTaskId))
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.OPERATOR_ID, operateInfo.getOperator().getId()))
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.STATE, ShopTaskLogState.UNFINISHED.name())).build();
    return jdbcTemplate.query(build, MAPPER);
  }

  public List<ShopTaskLog> listByShopTaskIdsAndOperatorId(String tenant, List<String> shopTaskIds, String operatorId) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(shopTaskIds, "shopTaskIds");
    Assert.hasText(operatorId, "operatorId");

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskLog.TABLE_NAME)
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.in2(PShopTaskLog.OWNER, shopTaskIds.toArray()))
        .where(Predicates.equals(PShopTaskLog.OPERATOR_ID, operatorId))
        .orderBy(PShopTaskLog.CREATE_INFO_TIME, false)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public List<ShopTaskLog> listByUuidList(String tenant, List<String> logUuidList) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(logUuidList);

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskLog.TABLE_NAME)
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.in2(PShopTaskLog.UUID, logUuidList.toArray())).build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  /**
   * 获取已交接接受的log记录
   *
   * @param tenant     租户
   * @param plan       计划ID
   * @param planPeriod 计划周期
   * @param shop       店铺
   * @param operatorId 登录者ID
   * @return
   */
  public List<ShopTaskLog> listTransferredByUK(String tenant, String plan, String planPeriod, String shop, String operatorId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(plan, "plan");
    Assert.hasText(planPeriod, "planPeriod");
    Assert.hasText(shop, "shop");

    /*
    SELECT log.*FROM shop_task_log log WHERE log.tenant = () log.operator_id !=() AND log.`owner` IN (
    SELECT shop_task.uuid FROM shop_task shop_task and shop_task.plan=() and shop_task.planPeriod = () and shop_task.shop = () and shop_task.tenant = () ) AND log.uuid IN (
    SELECT DISTINCT shop_task_log_id FROM shop_task_transfer transfer WHERE transfer.state='ACCEPTED' AND transfer_from=() and transfer.tenant = ()) ORDER BY log.`owner`
     */
    SelectStatement selectShopTaskUUID = new SelectBuilder().select(PShopTask.UUID)
        .from(PShopTask.TABLE_NAME, PShopTask.TABLE_ALIAS)
        .where(Predicates.equals(PShopTask.TABLE_ALIAS, PShopTask.PLAN, plan))
        .where(Predicates.equals(PShopTask.TABLE_ALIAS, PShopTask.PLAN_PERIOD, planPeriod))
        .where(Predicates.equals(PShopTask.TABLE_ALIAS, PShopTask.SHOP, shop))
        .where(Predicates.equals(PShopTask.TABLE_ALIAS, PShopTask.TENANT, tenant)).build();
    SelectStatement selectTransferUUID = new SelectBuilder().select(PShopTaskTransfer.SHOP_TASK_LOG_ID)
        .distinct()
        .from(PShopTaskTransfer.TABLE_NAME, PShopTaskTransfer.TABLE_ALIAS)
        .where(Predicates.equals(PShopTaskTransfer.TABLE_ALIAS, PShopTaskTransfer.STATE, ShopTaskTransferState.ACCEPTED.name()))
        .where(Predicates.equals(PShopTaskTransfer.TABLE_ALIAS, PShopTaskTransfer.TRANSFER_FROM, operatorId))
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant)).build();
    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskLog.TABLE_NAME, PShopTaskLog.TABLE_ALIAS)
        .where(Predicates.equals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.TENANT, tenant))
        .where(Predicates.notEquals(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.OPERATOR_ID, operatorId))
        .where(Predicates.in(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.OWNER, selectShopTaskUUID))
        .where(Predicates.in(PShopTaskLog.TABLE_ALIAS, PShopTaskLog.UUID, selectTransferUUID))
        .orderBy(PShopTaskLog.OWNER).build();

    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public void changeShopTaskLogStateByOwner(String tenant, String owner, ShopTaskState state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(operateInfo);
    Assert.notNull(state);
    Assert.hasText(owner, "owner");

    UpdateStatement updateStatement = new UpdateBuilder().table(PShopTaskLog.TABLE_NAME)
        .setValue(PShopTaskLog.STATE, state.name())
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PShopTaskLog.TENANT, tenant))
        .where(Predicates.equals(PShopTaskLog.OWNER, owner))
        .where(Predicates.equals(PShopTaskLog.STATE, ShopTaskState.NOT_STARTED.name()))
        .build();
    jdbcTemplate.update(updateStatement);
  }
}
