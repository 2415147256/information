package com.hd123.baas.sop.service.dao.price.shopprice;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPricePromotion;
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
public class ShopPricePromotionDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopPricePromotion.class,
      PShopPricePromotion.class).build();

  public QueryResult<ShopPricePromotion> query(String tenant, QueryDefinition qd) {
    qd.addByField(ShopPricePromotion.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new ShopPricePromotionMapper());
  }

  public ShopPricePromotion get(String tenant, String shop, String skuId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shop, "shop");
    Assert.hasText(skuId, "skuId");
    SelectStatement select = new SelectBuilder().from(PShopPricePromotion.TABLE_NAME)
        .select(PShopPricePromotion.allColumns())
        .where(Predicates.equals(PShopPricePromotion.TENANT, tenant))
        .where(Predicates.equals(PShopPricePromotion.SHOP, shop))
        .where(Predicates.equals(PShopPricePromotion.SKU_ID, skuId))
        .build();
    return getFirst(jdbcTemplate.query(select, new ShopPricePromotionMapper()));
  }

  public void delete(String tenant, String source) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(source, "source");
    DeleteStatement delete = new DeleteBuilder().table(PShopPricePromotion.TABLE_NAME)
        .where(Predicates.equals(PShopPricePromotion.TENANT, tenant))
        .where(Predicates.equals(PShopPricePromotion.SOURCE, source))
        .build();
    jdbcTemplate.update(delete);
  }

  public void batchInsert(String tenant, Collection<ShopPricePromotion> promotions) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(promotions)) {
      return;
    }
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopPricePromotion promotion : promotions) {
      updater.add(buildInsertStatement(tenant, promotion));
    }
    updater.update();
  }

  public void batchUpdate(String tenant, Collection<ShopPricePromotion> promotions) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(promotions)) {
      return;
    }
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ShopPricePromotion promotion : promotions) {
      Assert.hasText(promotion.getUuid(), "promotion.uuid");
      updater.add(buildUpdateStatement(tenant, promotion));
    }
    updater.update();
  }

  private InsertStatement buildInsertStatement(String tenant, ShopPricePromotion promotion) {
    if (StringUtils.isBlank(promotion.getUuid())) {
      promotion.setUuid(UUID.randomUUID().toString());
    }
    return new InsertBuilder().table(PShopPricePromotion.TABLE_NAME)
        .addValues(PShopPricePromotion.forSaveNew(promotion))
        .addValue(PShopPricePromotion.TENANT, tenant)
        .addValue(PShopPricePromotion.ORG_ID, promotion.getOrgId())
        .addValue(PShopPricePromotion.SHOP, promotion.getShop())
        .addValue(PShopPricePromotion.EFFECTIVE_END_DATE, promotion.getEffectiveEndDate())
        .addValue(PShopPricePromotion.SKU_ID, promotion.getSku().getId())
        .addValue(PShopPricePromotion.TYPE, promotion.getType().name())
        .addValue(PShopPricePromotion.RULE, promotion.getRule())
        .addValue(PShopPricePromotion.SOURCE, promotion.getSource())
        .addValue(PShopPricePromotion.SOURCE_LAST_MODIFIED, promotion.getSourceLastModified())
        .addValue(PShopPricePromotion.PRICE_PROMOTION_TYPE, promotion.getPricePromotionType())
        .addValue(PShopPricePromotion.CREATE_INFO_TIME, new Date())
        .addValue(PShopPricePromotion.LAST_MODIFY_INFO_TIME, new Date())
        .build();
  }

  private UpdateStatement buildUpdateStatement(String tenant, ShopPricePromotion promotion) {
    if (StringUtils.isBlank(promotion.getUuid())) {
      promotion.setUuid(UUID.randomUUID().toString());
    }
    return new UpdateBuilder().table(PShopPricePromotion.TABLE_NAME)
        .addValues(PShopPricePromotion.forSaveModify(promotion))
        .addValue(PShopPricePromotion.SHOP, promotion.getShop())
        .addValue(PShopPricePromotion.ORG_ID, promotion.getOrgId())
        .addValue(PShopPricePromotion.EFFECTIVE_END_DATE, promotion.getEffectiveEndDate())
        .addValue(PShopPricePromotion.SKU_ID, promotion.getSku().getId())
        .addValue(PShopPricePromotion.TYPE, promotion.getType().name())
        .addValue(PShopPricePromotion.RULE, promotion.getRule())
        .addValue(PShopPricePromotion.SOURCE, promotion.getSource())
        .addValue(PShopPricePromotion.LAST_MODIFY_INFO_TIME, new Date())
        .addValue(PShopPricePromotion.SOURCE_LAST_MODIFIED, promotion.getSourceLastModified())
        .addValue(PShopPricePromotion.PRICE_PROMOTION_TYPE, promotion.getPricePromotionType())
        .where(Predicates.equals(PShopPricePromotion.TENANT, tenant))
        .where(Predicates.equals(PShopPricePromotion.UUID, promotion.getUuid()))
        .build();
  }

}
