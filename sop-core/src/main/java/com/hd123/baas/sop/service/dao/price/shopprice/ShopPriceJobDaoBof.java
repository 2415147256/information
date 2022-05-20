package com.hd123.baas.sop.service.dao.price.shopprice;

import java.util.Collection;
import java.util.UUID;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceJob;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceJobState;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Repository
public class ShopPriceJobDaoBof extends BofBaseDao {
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopPriceJob.class, PShopPriceJob.class).build();

  public QueryResult<ShopPriceJob> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(ShopPriceJob.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new ShopPriceJobMapper());
  }

  public long queryCount(String tenant, QueryDefinition qd) {
    qd.addByField(ShopPriceJob.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    selectStatement.getSelectClause().getFields().clear();
    selectStatement.select("count(1)");
    QueryResult<Long> result = executor.query(selectStatement, new SingleColumnRowMapper<>(Long.class));
    if (result.getRecordCount() == 0) {
      return 0;
    }
    return result.getRecords().get(0);
  }

  public ShopPriceJob get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    SelectStatement select = new SelectBuilder().select(PShopPriceJob.allColumns())
        .from(PShopPriceJob.TABLE_NAME)
        .where(Predicates.equals(PShopPriceJob.TENANT, tenant))
        .where(Predicates.equals(PShopPriceJob.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new ShopPriceJobMapper()));
  }

  public ShopPriceJob getByShopAndTask(String tenant, String shop, String taskId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shop, "shop");
    Assert.hasText(taskId, "taskId");
    SelectStatement select = new SelectBuilder().select(PShopPriceJob.allColumns())
        .from(PShopPriceJob.TABLE_NAME)
        .where(Predicates.equals(PShopPriceJob.TENANT, tenant))
        .where(Predicates.equals(PShopPriceJob.SHOP, shop))
        .where(Predicates.equals(PShopPriceJob.TASK_ID, taskId))
        .build();
    return getFirst(jdbcTemplate.query(select, new ShopPriceJobMapper()));
  }

  public void changeState(String tenant, String uuid, ShopPriceJobState state, String errMsg, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(state, "state");
    UpdateStatement update = new UpdateBuilder().table(PShopPriceJob.TABLE_NAME)
        .addValue(PShopPriceJob.STATE, state.name())
        .addValue(PShopPriceJob.ERR_MSG, errMsg)
        .addValues(PShopPriceJob.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PShopPriceJob.TENANT, tenant))
        .where(Predicates.equals(PShopPriceJob.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }

  public void batchInsert(String tenant, Collection<ShopPriceJob> priceJobs, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(operateInfo, "operateInfo");
    if (CollectionUtils.isEmpty(priceJobs)) {
      return;
    }
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopPriceJob priceJob : priceJobs) {
      updater.add(buildInsertStatement(tenant, priceJob, operateInfo));
    }
    updater.update();
  }

  public void buildUpdate(String tenant, Collection<ShopPriceJob> priceJobs, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(operateInfo, "operateInfo");
    if (CollectionUtils.isEmpty(priceJobs)) {
      return;
    }
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopPriceJob priceJob : priceJobs) {
      updater.add(buildUpdateStatement(tenant, priceJob, operateInfo));
    }
    updater.update();
  }

  public void insert(String tenant, ShopPriceJob priceJob, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(priceJob, "priceJob");
    Assert.notNull(operateInfo, "operateInfo");
    InsertStatement select = buildInsertStatement(tenant, priceJob, operateInfo);
    jdbcTemplate.update(select);
  }

  public void update(String tenant, ShopPriceJob priceJob, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(priceJob, "priceJob");
    Assert.notNull(operateInfo, "operateInfo");
    UpdateStatement update = buildUpdateStatement(tenant, priceJob, operateInfo);
    jdbcTemplate.update(update);
  }

  private InsertStatement buildInsertStatement(String tenant, ShopPriceJob priceJob, OperateInfo operateInfo) {
    priceJob.setCreateInfo(operateInfo);
    priceJob.setLastModifyInfo(operateInfo);
    if (StringUtils.isBlank(priceJob.getUuid())) {
      priceJob.setUuid(UUID.randomUUID().toString());
    }
    InsertBuilder builder = new InsertBuilder().table(PShopPriceJob.TABLE_NAME)
        .addValues(PShopPriceJob.forSaveNew(priceJob))
        .addValue(PShopPriceJob.TENANT, tenant)
        .addValue(PShopPriceJob.ORG_ID, priceJob.getOrgId())
        .addValue(PShopPriceJob.SHOP, priceJob.getShop())
        .addValue(PShopPriceJob.TASK_ID, priceJob.getTaskId())
        .addValue(PShopPriceJob.EXECUTE_DATE, priceJob.getExecuteDate())
        .addValue(PShopPriceJob.PRICE_ADJUSTMENT, priceJob.getPriceAdjustment())
        .addValue(PShopPriceJob.STATE, priceJob.getState().name())

        .addValue(PShopPriceJob.SHOP_CODE, priceJob.getShopCode())
        .addValue(PShopPriceJob.SHOP_NAME, priceJob.getShopName())
        .addValue(PShopPriceJob.ERR_MSG, priceJob.getErrMsg());
    return builder.build();
  }

  private UpdateStatement buildUpdateStatement(String tenant, ShopPriceJob priceJob, OperateInfo operateInfo) {
    priceJob.setLastModifyInfo(operateInfo);
    UpdateBuilder builder = new UpdateBuilder().table(PShopPriceJob.TABLE_NAME)
        .addValues(PShopPriceJob.forSaveModify(priceJob))
        .addValue(PShopPriceJob.SHOP, priceJob.getShop())
        .addValue(PShopPriceJob.ORG_ID, priceJob.getOrgId())
        .addValue(PShopPriceJob.TASK_ID, priceJob.getTaskId())
        .addValue(PShopPriceJob.EXECUTE_DATE, priceJob.getExecuteDate())
        .addValue(PShopPriceJob.PRICE_ADJUSTMENT, priceJob.getPriceAdjustment())
        .addValue(PShopPriceJob.STATE, priceJob.getState().name())

        .addValue(PShopPriceJob.SHOP_CODE, priceJob.getShopCode())
        .addValue(PShopPriceJob.SHOP_NAME, priceJob.getShopName())
        .addValue(PShopPriceJob.ERR_MSG, priceJob.getErrMsg())

        .where(Predicates.equals(PShopPriceJob.TENANT, tenant))
        .where(Predicates.equals(PShopPriceJob.UUID, priceJob.getUuid()));
    return builder.build();
  }

  public void updateErrMsg(String tenant, String uuid, String errMsg, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(operateInfo, "operateInfo");

    UpdateBuilder builder = new UpdateBuilder().table(PShopPriceJob.TABLE_NAME)
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .addValue(PShopPriceJob.ERR_MSG, errMsg)

        .where(Predicates.equals(PShopPriceJob.TENANT, tenant))
        .where(Predicates.equals(PShopPriceJob.UUID, uuid));
    jdbcTemplate.update(builder.build());
  }

}
