package com.hd123.baas.sop.service.dao.price.shopprice;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceManager;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author zhengzewang on 2020/11/20.
 */
@Slf4j
@Repository
public class ShopPriceManagerDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopPriceManager.class, PShopPriceManager.class)
      .build();

  public QueryResult<ShopPriceManager> query(String tenant, QueryDefinition qd) {
    qd.addByField(ShopPriceManager.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new ShopPriceManagerMapper());
  }

  public List<ShopPriceManager> list(String tenant, QueryDefinition qd) {
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return jdbcTemplate.query(select, new ShopPriceManagerMapper());
  }

  public long queryCount(String tenant, QueryDefinition qd) {
    qd.addByField(ShopPriceManager.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    selectStatement.getSelectClause().getFields().clear();
    selectStatement.select("count(1)");
    QueryResult<Long> result = executor.query(selectStatement, new SingleColumnRowMapper<>(Long.class));
    if (result.getRecordCount() == 0) {
      return 0;
    }
    return result.getRecords().get(0);
  }

  public void batchInsert(String tenant, Collection<ShopPriceManager> managers) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(managers)) {
      return;
    }
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopPriceManager manager : managers) {
      updater.add(buildInsertStatement(tenant, manager));
    }
    updater.update();
  }

  public void batchUpdatePrice(String tenant, Collection<ShopPriceManager> managers) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(managers)) {
      return;
    }
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopPriceManager manager : managers) {
      updater.add(buildUpdateStatement(tenant, manager));
    }
    updater.update();
  }

  private UpdateStatement buildUpdateStatement(String tenant, ShopPriceManager manager) {
    return new UpdateBuilder().table(PShopPriceManager.TABLE_NAME)
        .setValue(PShopPriceManager.SALE_PRICE, manager.getSalePrice())
        .setValue(PShopPriceManager.PROMOTION_SOURCE,manager.getPromotionSource())
        .setValues(PShopPriceManager.forSaveModify(manager))
        .setValue(PShopPriceManager.LAST_MODIFY_INFO_TIME, new Date())

        .where(Predicates.equals(PShopPriceManager.TENANT, tenant))
        .where(Predicates.equals(PShopPriceManager.SHOP, manager.getShop()))
        .where(Predicates.equals(PShopPriceManager.SKU_ID, manager.getSku().getId()))
        .where(Predicates.equals(PShopPriceManager.EFFECTIVE_DATE, manager.getEffectiveDate()))
        .build();
  }

  private InsertStatement buildInsertStatement(String tenant, ShopPriceManager manager) {
    if (StringUtils.isBlank(manager.getUuid())) {
      manager.setUuid(UUID.randomUUID().toString());
    }

    return new InsertBuilder().table(PShopPriceManager.TABLE_NAME)
        .addValues(PShopPriceManager.forSaveNew(manager))
        .addValue(PShopPriceManager.TENANT, tenant)
        .addValue(PShopPriceManager.ORG_ID, manager.getOrgId())
        .addValue(PShopPriceManager.SHOP, manager.getShop())
        .addValue(PShopPriceManager.SHOP_CODE, manager.getShopCode())
        .addValue(PShopPriceManager.SHOP_NAME, manager.getShopName())
        .addValue(PShopPriceManager.EFFECTIVE_DATE, manager.getEffectiveDate())
        .addValue(PShopPriceManager.EFFECTIVE_END_DATE, manager.getEffectiveEndDate())
        .addValue(PShopPriceManager.SKU_ID, manager.getSku().getId())
        .addValue(PShopPriceManager.SKU_GID, manager.getSku().getGoodsGid())
        .addValue(PShopPriceManager.SKU_QPC, manager.getSku().getQpc())
        .addValue(PShopPriceManager.SKU_CODE, manager.getSku().getCode())
        .addValue(PShopPriceManager.SKU_NAME, manager.getSku().getName())
        .addValue(PShopPriceManager.IN_PRICE, manager.getInPrice())
        .addValue(PShopPriceManager.BASE_PRICE, manager.getBasePrice())
        .addValue(PShopPriceManager.PROMOTION_SOURCE,manager.getPromotionSource())
        .addValue(PShopPriceManager.SHOP_PRICE, manager.getShopPrice())
        .addValue(PShopPriceManager.SALE_PRICE, manager.getSalePrice())
        .addValue(PShopPriceManager.CHANGED, manager.getChanged())

        .addValue(PShopPriceManager.CREATE_INFO_TIME, new Date())
        .addValue(PShopPriceManager.LAST_MODIFY_INFO_TIME, new Date())
        .build();
  }

  public void clearBeforeDate(String tenant, String orgId, Date date) {
    /**
     * delete from shop_price_manager where (tenant = 'mkhsqprd') AND uuid
     * in(select a.uuid from (select uuid from shop_price_manager where effective_date <= '2021-06-30 00:00:00') a );
     */
    SelectStatement selectInner=new SelectBuilder().select(PShopPriceManager.UUID)
        .from(PShopPriceManager.TABLE_NAME).where(Predicates.equals(PShopPriceManager.TENANT, tenant))
        .where(Predicates.less(PShopPriceManager.EFFECTIVE_DATE,date)).build();
    SelectStatement selectStatement =new SelectBuilder().select("a.uuid").from(selectInner,"a").build();
    DeleteStatement delete = new DeleteBuilder().table(PShopPriceManager.TABLE_NAME)
        .where(Predicates.equals(PShopPriceManager.TENANT, tenant))
        .where(Predicates.equals(PShopPriceManager.ORG_ID, orgId))
        .where(Predicates.in2(PShopPriceManager.UUID,selectStatement))
        .build();
    log.info("批量删除sql:{},参数：{},{}",delete.getSql(),tenant,date);
    jdbcTemplate.update(delete);
  }

  public List<ShopPriceManager> list(String tenant, String shop, Date effectiveDate, List<String> skuIds) {
    if (CollectionUtils.isEmpty(skuIds)) {
      return new ArrayList<>();
    }
    SelectBuilder selectBuilder = new SelectBuilder().from(PShopPriceManager.TABLE_NAME, PShopPriceManager.TABLE_ALIAS);
    selectBuilder.where(Predicates.equals(PShopPriceManager.TENANT, tenant));
    selectBuilder.where(Predicates.equals(PShopPriceManager.SHOP, shop));
    selectBuilder.where(Predicates.equals(PShopPriceManager.EFFECTIVE_DATE, effectiveDate));
    selectBuilder.where(Predicates.in2(PShopPriceManager.SKU_ID, skuIds.toArray()));
    return jdbcTemplate.query(selectBuilder.build(), new ShopPriceManagerMapper());
  }

  public List<ShopPriceManager> listByGoodsIds(String tenant, String shop, Date effectiveDate, List<String> goodsIds) {
    if (CollectionUtils.isEmpty(goodsIds)) {
      return new ArrayList<>();
    }
    SelectBuilder selectBuilder = new SelectBuilder().from(PShopPriceManager.TABLE_NAME, PShopPriceManager.TABLE_ALIAS);
    selectBuilder.where(Predicates.equals(PShopPriceManager.TENANT, tenant));
    selectBuilder.where(Predicates.equals(PShopPriceManager.SHOP, shop));
    selectBuilder.where(Predicates.equals(PShopPriceManager.EFFECTIVE_DATE, effectiveDate));
    selectBuilder.where(Predicates.in2(PShopPriceManager.SKU_GID, goodsIds.toArray()));
    return jdbcTemplate.query(selectBuilder.build(), new ShopPriceManagerMapper());
  }

  public void remove(String tenant, String shop, Date effectiveDate, List<String> skuIds) {
    if (CollectionUtils.isEmpty(skuIds)) {
      return;
    }

    DeleteBuilder deleteBuilder = new DeleteBuilder().table(PShopPriceManager.TABLE_NAME);
    deleteBuilder.where(Predicates.equals(PShopPriceManager.TENANT, tenant));
    deleteBuilder.where(Predicates.equals(PShopPriceManager.SHOP, shop));
    deleteBuilder.where(Predicates.equals(PShopPriceManager.EFFECTIVE_DATE, effectiveDate));
    deleteBuilder.where(Predicates.in2(PShopPriceManager.SKU_ID, skuIds.toArray()));
    jdbcTemplate.update(deleteBuilder.build());
  }

}
