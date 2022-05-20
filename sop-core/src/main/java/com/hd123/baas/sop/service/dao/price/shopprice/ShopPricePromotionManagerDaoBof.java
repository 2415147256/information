package com.hd123.baas.sop.service.dao.price.shopprice;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPricePromotionManager;
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
@Repository
public class ShopPricePromotionManagerDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopPricePromotionManager.class,
      PShopPricePromotionManager.class).build();

  public QueryResult<ShopPricePromotionManager> query(String tenant, QueryDefinition qd) {
    qd.addByField(ShopPricePromotionManager.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new ShopPricePromotionManagerMapper());
  }

  public void batchInsert(String tenant, Collection<ShopPricePromotionManager> managers) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(managers)) {
      return;
    }
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopPricePromotionManager manager : managers) {
      updater.add(buildInsertStatement(tenant, manager));
    }
    updater.update();
  }

  private InsertStatement buildInsertStatement(String tenant, ShopPricePromotionManager manager) {
    if (StringUtils.isBlank(manager.getUuid())) {
      manager.setUuid(UUID.randomUUID().toString());
    }
    return new InsertBuilder().table(PShopPricePromotionManager.TABLE_NAME)
        .addValues(PShopPricePromotionManager.forSaveNew(manager))
        .addValue(PShopPricePromotionManager.TENANT, tenant)
        .addValue(PShopPricePromotionManager.ORG_ID, manager.getOrgId())
        .addValue(PShopPricePromotionManager.SHOP, manager.getShop())
        .addValue(PShopPricePromotionManager.EFFECTIVE_END_DATE, manager.getEffectiveEndDate())
        .addValue(PShopPricePromotionManager.SKU_ID, manager.getSku().getId())
        .addValue(PShopPricePromotionManager.TYPE, manager.getType().name())
        .addValue(PShopPricePromotionManager.RULE, manager.getRule())
        .addValue(PShopPricePromotionManager.SOURCE, manager.getSource())
        .addValue(PShopPricePromotionManager.EFFECTIVE_START_DATE, manager.getEffectiveStartDate())
        .addValue(PShopPricePromotionManager.CREATE_INFO_TIME, new Date())
        .addValue(PShopPricePromotionManager.LAST_MODIFY_INFO_TIME, new Date())
        .addValue(PShopPricePromotionManager.SOURCE_LAST_MODIFIED, manager.getSourceLastModified())
        .addValue(PShopPricePromotionManager.PRICE_PROMOTION_TYPE, manager.getPricePromotionType())
        .build();
  }

  public void batchDelete(String tenant, Collection<String> uuids) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(uuids)) {
      return;
    }
    DeleteStatement delete = new DeleteBuilder().table(PShopPricePromotionManager.TABLE_NAME)
        .where(Predicates.equals(PShopPricePromotionManager.TENANT, tenant))
        .where(Predicates.in2(PShopPricePromotionManager.UUID, uuids.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  public void deleteBeforeDate(String tenant, String orgId, Date date) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notNull(date, "date");

    DeleteStatement delete = new DeleteBuilder().table(PShopPricePromotionManager.TABLE_NAME)
        .where(Predicates.equals(PShopPricePromotionManager.TENANT, tenant))
        .where(Predicates.equals(PShopPricePromotionManager.ORG_ID, orgId))
        .where(Predicates.less(PShopPricePromotionManager.EFFECTIVE_END_DATE, date))
        .build();
    jdbcTemplate.update(delete);
  }

  public void deleteBySource(String tenant, String source) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(source, "source");

    DeleteStatement delete = new DeleteBuilder().table(PShopPricePromotionManager.TABLE_NAME)
        .where(Predicates.equals(PShopPricePromotionManager.TENANT, tenant))
        .where(Predicates.equals(PShopPricePromotionManager.SOURCE, source))
        .build();
    jdbcTemplate.update(delete);
  }

}
