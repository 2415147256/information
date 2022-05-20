package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.task.ShopTaskState;
import com.hd123.baas.sop.service.api.task.ShopTaskTransfer;
import com.hd123.baas.sop.service.api.task.ShopTaskTransferState;
import com.hd123.baas.sop.service.api.task.ShopTaskTransferType;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author guyahui
 * @date 2021/5/20 14:10
 */
@Repository
public class ShopTaskTransferDaoBof extends BofBaseDao {

  public static final TEMapper<ShopTaskTransfer> MAPPER = TEMapperBuilder
      .of(ShopTaskTransfer.class, PShopTaskTransfer.class)
      .primaryKey(PShopTaskTransfer.TENANT, PShopTaskTransfer.UUID)
      .map("state", PShopTaskTransfer.STATE, EnumConverters.toString(ShopTaskTransferState.class),
          EnumConverters.toEnum(ShopTaskTransferState.class))
      .build();

  public int transfer(String tenant, ShopTaskTransfer shopTaskTransfer) {

    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getShopTaskId(), "shopTaskId");
    Assert.hasText(shopTaskTransfer.getShop(), "shopId");
    Assert.hasText(shopTaskTransfer.getShopCode(), "shopCode");
    Assert.hasText(shopTaskTransfer.getTransferFrom(), "transferFrom");
    Assert.hasText(shopTaskTransfer.getTransferTo(), "transferTo");

    InsertBuilder insert = new InsertBuilder().table(PShopTaskTransfer.TABLE_NAME)
        .addValues(MAPPER.forInsert(shopTaskTransfer));
    return jdbcTemplate.update(insert.build());
  }

  public int updateState(String tenant, ShopTaskTransfer shopTaskTransfer) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getUuid(), "uuid");

    UpdateBuilder update = new UpdateBuilder().table(PShopTaskTransfer.TABLE_NAME)
        .addValue(PShopTaskTransfer.OPER_TIME, shopTaskTransfer.getOperTime())
        .addValue(PShopTaskTransfer.REASON, shopTaskTransfer.getReason())
        .addValue(PShopTaskTransfer.STATE, shopTaskTransfer.getState().name())
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.UUID, shopTaskTransfer.getUuid()));
    return jdbcTemplate.update(update.build());

  }

  public List<ShopTaskTransfer> listByShopTaskLogIdAndTransferFrom(String tenant, String shopTaskLogId,
                                                                   String transferFrom) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskLogId, "shopTaskLogId");
    Assert.hasText(transferFrom, "transferFrom");

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME)
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_LOG_ID, shopTaskLogId))
        .where(Predicates.equals(PShopTaskTransfer.TRANSFER_FROM, transferFrom))
        .orderBy(PShopTaskTransfer.TRANSFER_TIME, false)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public List<ShopTaskTransfer> listBatchByShopTaskIdAndTransferFrom(String tenant, String shopTaskId,
                                                                     String transferFrom) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");
    Assert.hasText(transferFrom, "transferFrom");

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME)
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_ID, shopTaskId))
        .where(Predicates.equals(PShopTaskTransfer.TRANSFER_FROM, transferFrom))
        .where(Predicates.equals(PShopTaskTransfer.TYPE, ShopTaskTransferType.BATCH.name()))
        .where(Predicates.isNull(PShopTaskTransfer.BATCH_ID))
        .orderBy(PShopTaskTransfer.TRANSFER_TIME, false)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public ShopTaskTransfer listByShopTaskLogIdAndTransferTo(String tenant, String shopTaskLogId, String transferTo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskLogId, "shopTaskId");
    Assert.hasText(transferTo, "transferTo");

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME)
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_LOG_ID, shopTaskLogId))
        .where(Predicates.equals(PShopTaskTransfer.TRANSFER_TO, transferTo))
        .orderBy(PShopTaskTransfer.TRANSFER_TIME, false)
        .build();
    List<ShopTaskTransfer> query = jdbcTemplate.query(selectStatement, MAPPER);
    return CollectionUtils.isEmpty(query) ? null : query.get(0);
  }

  public List<ShopTaskTransfer> listByShopTaskLogId(String tenant, String shopTaskLogId, String operatorId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskLogId, "shopTaskLogId");
    Assert.hasText(operatorId, "operatorId");
    /*
    	SELECT a.* from 'shop_task_transfer' a where a.shop_task_log = () and
    	a.transfer_time >= (SELECT min(b.transfer_time) where 'shop_task_transfer' b where b.transfer_from = '' and b.shop_task_log  = () and b.transfer_from = ())
     */
    SelectStatement selectMinTransferTime = new SelectBuilder().select("min(b.transfer_time) as transfer_time")
        .from(PShopTaskTransfer.TABLE_NAME, "b")
        .where(Predicates.equals("b", PShopTaskTransfer.TRANSFER_FROM, operatorId))
        .where(Predicates.equals("b", PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals("b", PShopTaskTransfer.SHOP_TASK_LOG_ID, shopTaskLogId)).build();

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME, "a")
        .where(Predicates.equals("a", PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals("a", PShopTaskTransfer.SHOP_TASK_LOG_ID, shopTaskLogId))
        .where(Predicates.greaterOrEquals("a", PShopTaskTransfer.TRANSFER_TIME, selectMinTransferTime))
        .orderBy(PShopTaskTransfer.TRANSFER_TIME, false)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public List<ShopTaskTransfer> listByShopTaskLogIdsList(String tenant, List<String> shopTaskLogIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(shopTaskLogIds, "shopTaskLogIds");

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME)
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.in2(PShopTaskTransfer.SHOP_TASK_LOG_ID, shopTaskLogIds.toArray()))
        .orderBy(PShopTaskTransfer.TRANSFER_TIME, false)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public void cancel(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    UpdateBuilder update = new UpdateBuilder().table(PShopTaskTransfer.TABLE_NAME)
        .addValue(PShopTaskTransfer.STATE, ShopTaskTransferState.CANCELED.name())
        .setValue(PShopTaskTransfer.OPER_TIME, new Date())
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.UUID, uuid));
    jdbcTemplate.update(update.build());

  }

  public void cancelByShopTaskId(String tenant, List<String> shopTaskIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTaskIds, "shopTaskIds");

    List<UpdateStatement> list = new ArrayList<>();
    for (String shopTaskId : shopTaskIds) {
      UpdateBuilder update = new UpdateBuilder().table(PShopTaskTransfer.TABLE_NAME)
          .addValue(PShopTaskTransfer.STATE, ShopTaskTransferState.CANCELED.name())
          .addValue(PShopTaskTransfer.OPER_TIME, new Date())
          .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
          .where(Predicates.equals(PShopTaskTransfer.STATE, ShopTaskTransferState.TRANSFER.name()))
          .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_ID, shopTaskId));
      list.add(update.build());
    }
    batchUpdate(list);
  }

  public void cancelByLogId(String tenant, String logId) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(logId, "logId");

    UpdateBuilder update = new UpdateBuilder().table(PShopTaskTransfer.TABLE_NAME)
        .addValue(PShopTaskTransfer.STATE, ShopTaskTransferState.CANCELED.name())
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.STATE, ShopTaskTransferState.TRANSFER.name()))
        .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_LOG_ID, logId));
    jdbcTemplate.update(update.build());
  }

  public ShopTaskTransfer get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME)
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(selectStatement, MAPPER));
  }

  public List<ShopTaskTransfer> listByShopTaskIdAndTransferFrom(String tenant, String shopTaskId, String transferFrom) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");
    Assert.hasText(transferFrom, "transferFrom");

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME)
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_ID, shopTaskId))
        .where(Predicates.equals(PShopTaskTransfer.TRANSFER_FROM, transferFrom))
        .orderBy(PShopTaskTransfer.TRANSFER_TIME, false)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public List<ShopTaskTransfer> listTransferByShopTaskIdList(String tenant, List<String> shopTaskIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTaskIds);

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME)
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.in2(PShopTaskTransfer.SHOP_TASK_ID, shopTaskIds.toArray()))
        .orderBy(PShopTaskTransfer.TRANSFER_TIME, false)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public void autoCancelByExpireShopTask(String tenant) {
    Assert.notNull(tenant, "tenant");
    SelectStatement selectStatement = new SelectBuilder().select(PShopTask.UUID)
        .from(PShopTask.TABLE_NAME, PShopTask.TABLE_ALIAS)
        .where(Predicates.equals(PShopTask.TABLE_ALIAS, PShopTask.TENANT, tenant))
        .where(Predicates.equals(PShopTask.TABLE_ALIAS, PShopTask.STATE, ShopTaskState.EXPIRED.name()))
        .build();
    UpdateBuilder updateBuilder = new UpdateBuilder().table(PShopTaskTransfer.TABLE_NAME)
        .setValue(PShopTaskTransfer.STATE, ShopTaskTransferState.CANCELED.name())
        .setValue(PShopTaskTransfer.OPER_TIME, new Date())
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.STATE, ShopTaskTransferState.TRANSFER.name()))
        .where(Predicates.in2(PShopTaskTransfer.SHOP_TASK_ID, selectStatement));
    jdbcTemplate.update(updateBuilder.build());
  }

  public ShopTaskTransfer listBatchByShopTaskIdAndTransferTo(String tenant, String shopTaskId, String transferTo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");
    Assert.hasText(transferTo, "transferTo");

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME)
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_ID, shopTaskId))
        .where(Predicates.equals(PShopTaskTransfer.TYPE, ShopTaskTransferType.BATCH.name()))
        .where(Predicates.equals(PShopTaskTransfer.TRANSFER_TO, transferTo))
        .where(Predicates.isNull(PShopTaskTransfer.BATCH_ID))
        .orderBy(PShopTaskTransfer.TRANSFER_TIME, false)
        .build();
    List<ShopTaskTransfer> query = jdbcTemplate.query(selectStatement, MAPPER);
    return CollectionUtils.isEmpty(query) ? null : query.get(0);
  }

  public void cancelLogTransfer(String tenant, String shopTaskId, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");

    UpdateBuilder updateBuilder = new UpdateBuilder().table(PShopTaskTransfer.TABLE_NAME)
        .setValue(PShopTaskTransfer.STATE, ShopTaskTransferState.CANCELED.name())
        .setValue(PShopTaskTransfer.OPER_TIME, new Date())
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_ID, shopTaskId))
        .where(Predicates.equals(PShopTaskTransfer.TRANSFER_FROM, operateInfo.getOperator().getId()))
        .where(Predicates.isNull(PShopTaskTransfer.TYPE))
        .where(Predicates.equals(PShopTaskTransfer.STATE, ShopTaskTransferState.TRANSFER.name()));
    jdbcTemplate.update(updateBuilder.build());
  }

  /**
   * 根据任务ID获取主题交接记录，其中batchId为空时表示主题交接，否则为主题交接和该主题下的小项交接的交接记录
   *
   * @param tenant      租户
   * @param shopTaskIds 任务Id
   * @return
   */
  public List<ShopTaskTransfer> listBatchTransferByShopTaskIdList(String tenant, List<String> shopTaskIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTaskIds);

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME)
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.in2(PShopTaskTransfer.SHOP_TASK_ID, shopTaskIds.toArray()))
        .where(Predicates.equals(PShopTaskTransfer.TYPE, ShopTaskTransferType.BATCH.name()))
        .where(Predicates.isNull(PShopTaskTransfer.BATCH_ID))
        .orderBy(PShopTaskTransfer.TRANSFER_TIME, false)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  /**
   * 根据任务ID获取主题交接记录，其中batchId为空时表示主题交接，否则为主题交接和该主题下的小项交接的交接记录
   *
   * @param tenant      租户
   * @param shopTaskIds 任务Id
   * @return
   */
  public List<ShopTaskTransfer> listLogBatchTransferByShopTaskIdList(String tenant, List<String> shopTaskIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTaskIds);

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME)
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.in2(PShopTaskTransfer.SHOP_TASK_ID, shopTaskIds.toArray()))
        .where(Predicates.equals(PShopTaskTransfer.TYPE, ShopTaskTransferType.BATCH.name()))
        .where(Predicates.isNotNull(PShopTaskTransfer.BATCH_ID))
        .orderBy(PShopTaskTransfer.TRANSFER_TIME, false)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public void cancelBatchTransfer(String tenant, String shopTaskId, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");

    UpdateBuilder updateBuilder = new UpdateBuilder().table(PShopTaskTransfer.TABLE_NAME)
        .setValue(PShopTaskTransfer.STATE, ShopTaskTransferState.CANCELED.name())
        .setValue(PShopTaskTransfer.OPER_TIME, new Date())
        .where(Predicates.equals(PShopTaskTransfer.TYPE, ShopTaskTransferType.BATCH.name()))
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_ID, shopTaskId))
        .where(Predicates.equals(PShopTaskTransfer.TRANSFER_FROM, operateInfo.getOperator().getId()))
        .where(Predicates.equals(PShopTaskTransfer.STATE, ShopTaskTransferState.TRANSFER.name()));
    jdbcTemplate.update(updateBuilder.build());
  }

  public int batchSaveShopTaskLogTransfer(String tenant, List<ShopTaskTransfer> shopTaskTransferList) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(shopTaskTransferList);

    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    shopTaskTransferList.forEach(entity -> batchUpdater
        .add(new InsertBuilder().table(PShopTaskTransfer.TABLE_NAME).addValues(MAPPER.forInsert(entity)).build()));
    return batchUpdater.update().stream().flatMapToInt(Arrays::stream).sum();
  }

  public void changeBatchTransferState(String tenant, String transferUuid, ShopTaskTransferState state) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(transferUuid, "transferUuid");
    Assert.notNull(state, "state");

    UpdateBuilder updateBuilder = new UpdateBuilder().table(PShopTaskTransfer.TABLE_NAME)
        .setValue(PShopTaskTransfer.STATE, state.name())
        .setValue(PShopTaskTransfer.OPER_TIME, new Date())
        .where(Predicates.equals(PShopTaskTransfer.BATCH_ID, transferUuid))
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.STATE, ShopTaskTransferState.TRANSFER.name()));
    jdbcTemplate.update(updateBuilder.build());
  }

  /**
   * 取消对应交接（批量）主题（任务）的小项任务
   *
   * @param tenant      租户
   * @param shopTaskId  任务ID
   * @param logId       小项记录Id
   * @param operateInfo 当前登录人信息
   */
  public void cancelBatchTransferLog(String tenant, String shopTaskId, String logId, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");
    Assert.hasText(logId, "logId");

    UpdateBuilder updateBuilder = new UpdateBuilder().table(PShopTaskTransfer.TABLE_NAME)
        .setValue(PShopTaskTransfer.STATE, ShopTaskTransferState.CANCELED.name())
        .setValue(PShopTaskTransfer.OPER_TIME, new Date())
        .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_ID, shopTaskId))
        .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_LOG_ID, logId))
        .where(Predicates.equals(PShopTaskTransfer.TRANSFER_FROM, operateInfo.getOperator().getId()))
        .where(Predicates.equals(PShopTaskTransfer.TYPE, ShopTaskTransferType.BATCH.name()))
        .where(Predicates.isNotNull(PShopTaskTransfer.BATCH_ID));
    jdbcTemplate.update(updateBuilder.build());
  }

  public void refuseLogBatchTransfer(String tenant, ShopTaskTransfer shopTaskTransferHistory) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTaskTransferHistory);

    UpdateBuilder updateBuilder = new UpdateBuilder().table(PShopTaskTransfer.TABLE_NAME)
        .setValue(PShopTaskTransfer.STATE, ShopTaskTransferState.REFUSED.name())
        .setValue(PShopTaskTransfer.REASON, shopTaskTransferHistory.getReason())
        .setValue(PShopTaskTransfer.OPER_TIME, shopTaskTransferHistory.getOperTime())
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.BATCH_ID, shopTaskTransferHistory.getUuid()));
    jdbcTemplate.update(updateBuilder.build());
  }

  //查询主题交接记录
  public List<ShopTaskTransfer> listByShopTaskId(String tenant, String shopTaskId, String operatorId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");
    Assert.hasText(operatorId, "operatorId");
    /*
    SELECT a.* from 'shop_task_transfer' a where a.tenant =() and a.shop_task_id = () and a.type = 'BATCH' AND b.shop_task_log_id is null and b.batch_id is null and
    a.transfer_time >= (SELECT min(b.transfer_time) where 'shop_task_transfer' b where
    b.shop_task_id  = () and b.transfer_from = () and b.type = 'BATCH' AND b.shop_task_log_id is null and b.batch_id is null and b.tenant = ())
     */
    SelectStatement selectMinTransferTime = new SelectBuilder().select("min(b.transfer_time) as transfer_time")
        .from(PShopTaskTransfer.TABLE_NAME, "b")
        .where(Predicates.equals("b", PShopTaskTransfer.TRANSFER_FROM, operatorId))
        .where(Predicates.equals("b", PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals("b", PShopTaskTransfer.SHOP_TASK_ID, shopTaskId))
        .where(Predicates.equals("b", PShopTaskTransfer.TYPE, ShopTaskTransferType.BATCH.name()))
        .where(Predicates.isNull("b", PShopTaskTransfer.SHOP_TASK_LOG_ID))
        .where(Predicates.isNull("b", PShopTaskTransfer.BATCH_ID))
        .build();

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME, "a")
        .where(Predicates.equals("a", PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals("a", PShopTaskTransfer.SHOP_TASK_ID, shopTaskId))
        .where(Predicates.equals("a", PShopTaskTransfer.TYPE, ShopTaskTransferType.BATCH.name()))
        .where(Predicates.isNull("a", PShopTaskTransfer.SHOP_TASK_LOG_ID))
        .where(Predicates.isNull("a", PShopTaskTransfer.BATCH_ID))
        .where(Predicates.greaterOrEquals("a", PShopTaskTransfer.TRANSFER_TIME, selectMinTransferTime))
        .orderBy(PShopTaskTransfer.TRANSFER_TIME, false)
        .build();
    return jdbcTemplate.query(selectStatement, MAPPER);
  }

  public List<ShopTaskTransfer> listByBatchId(String tenant, String shopTaskId, String batchId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");
    Assert.hasText(batchId, "batchId");

    SelectStatement selectStatement = new SelectBuilder().from(PShopTaskTransfer.TABLE_NAME)
        .where(Predicates.equals(PShopTaskTransfer.TENANT, tenant))
        .where(Predicates.equals(PShopTaskTransfer.SHOP_TASK_ID, shopTaskId))
        .where(Predicates.equals(PShopTaskTransfer.BATCH_ID, batchId))
        .where(Predicates.equals(PShopTaskTransfer.TYPE, ShopTaskTransferType.BATCH.name())).build();

    return jdbcTemplate.query(selectStatement, MAPPER);
  }
}
