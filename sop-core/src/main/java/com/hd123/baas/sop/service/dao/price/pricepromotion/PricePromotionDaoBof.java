package com.hd123.baas.sop.service.dao.price.pricepromotion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotion;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionState;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionType;
import com.hd123.baas.sop.service.dao.price.shopprice.PShopPricePromotion;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/13.
 */
@Repository
@Slf4j
public class PricePromotionDaoBof extends BofBaseDao {

  private static final TEMapper<PricePromotion> PROMOTION_MAPPER = TEMapperBuilder
      .of(PricePromotion.class, PPricePromotion.class)
      .map("type", PPricePromotion.TYPE, EnumConverters.toString(PricePromotionType.class),
          EnumConverters.toEnum(PricePromotionType.class))
      .map("state", PPricePromotion.STATE, EnumConverters.toString(PricePromotionState.class),
          EnumConverters.toEnum(PricePromotionState.class))
      .primaryKey(PPricePromotion.UUID)
      .build();

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PricePromotion.class, PPricePromotion.class)
      .addConditionProcessor((condition, context) -> {
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(PricePromotion.Queries.SHOP_EQUALS, condition.getOperation())) {
          String value = (String) condition.getParameter();
          SelectStatement select = new SelectBuilder().select("1") //
              .from(PPricePromotionShop.TABLE_NAME, PPricePromotionShop.TABLE_ALIAS) //
              .where(Predicates.equals(PPricePromotionShop.TABLE_ALIAS, PPricePromotionShop.SHOP, value)) //
              .where(Predicates.equals(PPricePromotionShop.TABLE_ALIAS, PPricePromotionShop.OWNER, alias,
                  PPricePromotion.UUID))
              .where(Predicates.equals(PPricePromotionShop.TABLE_ALIAS, PPricePromotionShop.TENANT, alias,
                  PPricePromotion.TENANT))
              .build();
          return Predicates.exists(select);
        }
        if (StringUtils.equalsIgnoreCase(PricePromotion.Queries.SHOP_IN, condition.getOperation())) {
          SelectStatement select = new SelectBuilder().select("1") //
              .from(PPricePromotionShop.TABLE_NAME, PPricePromotionShop.TABLE_ALIAS) //
              .where(Predicates.in(PPricePromotionShop.TABLE_ALIAS, PPricePromotionShop.SHOP,
                  condition.getParameters().toArray())) //
              .where(Predicates.equals(PPricePromotionShop.TABLE_ALIAS, PPricePromotionShop.OWNER, alias,
                  PPricePromotion.UUID))
              .where(Predicates.equals(PPricePromotionShop.TABLE_ALIAS, PPricePromotionShop.TENANT, alias,
                  PPricePromotion.TENANT))
              .build();
          return Predicates.exists(select);
        }
        if (StringUtils.equalsIgnoreCase(PricePromotion.Queries.SKU_KEYWORD, condition.getOperation())) {
          String value = (String) condition.getParameter();
          SelectStatement select = new SelectBuilder().select("1") //
              .from(PPricePromotionLine.TABLE_NAME, PPricePromotionLine.TABLE_ALIAS) //
              .where(
                  Predicates.or(Predicates.like(PPricePromotionLine.TABLE_ALIAS, PPricePromotionLine.SKU_CODE, value),
                      Predicates.like(PPricePromotionLine.TABLE_ALIAS, PPricePromotionLine.SKU_NAME, value))) //
              .where(Predicates.equals(PPricePromotionLine.TABLE_ALIAS, PPricePromotionLine.OWNER, alias,
                  PPricePromotion.UUID))
              .where(Predicates.equals(PPricePromotionLine.TABLE_ALIAS, PPricePromotionLine.TENANT, alias,
                  PPricePromotion.TENANT))
              .build();
          return Predicates.exists(select);
        }
        if (StringUtils.equalsIgnoreCase(PricePromotion.Queries.NOT_INT_SHOP_PRICE_PROMOTION,
            condition.getOperation())) {
          SelectStatement select = new SelectBuilder().select("1") //
              .from(PShopPricePromotion.TABLE_NAME, PShopPricePromotion.TABLE_ALIAS) //
              .where(Predicates.equals(PShopPricePromotion.TABLE_ALIAS, PShopPricePromotion.SOURCE, alias,
                  PPricePromotion.UUID))
              .where(Predicates.equals(PShopPricePromotion.TABLE_ALIAS, PShopPricePromotion.TENANT, alias,
                  PPricePromotion.TENANT))
              .build();
          return Predicates.notExists(select);
        }
        if (StringUtils.equalsIgnoreCase(PricePromotion.Queries.CREATOR_KEYWORD, condition.getOperation())) {
          String value = (String) condition.getParameter();
          return Predicates.or(Predicates.like(alias, PPricePromotion.CREATE_INFO_OPERATOR_ID, value),
              Predicates.like(alias, PPricePromotion.CREATE_INFO_OPERATOR_FULL_NAME, value));
        }
        return null;
      })
      .build();

  public QueryResult<PricePromotion> query(String tenant, QueryDefinition qd) {
    qd.addByField(PricePromotion.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    log.info("sql:{}", select.getSql());
    return executor.query(select, new PricePromotionMapper());
  }

  public PricePromotion get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    SelectStatement select = new SelectBuilder().from(PPricePromotion.TABLE_NAME)
        .select(PPricePromotion.allColumns())
        .where(Predicates.equals(PPricePromotion.TENANT, tenant))
        .where(Predicates.equals(PPricePromotion.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new PricePromotionMapper()));
  }

  public List<PricePromotion> list(String tenant, Collection<String> uuids) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(uuids, "uuids");
    SelectStatement select = new SelectBuilder().from(PPricePromotion.TABLE_NAME)
        .select(PPricePromotion.allColumns())
        .where(Predicates.equals(PPricePromotion.TENANT, tenant))
        .where(Predicates.in2(PPricePromotion.UUID, uuids.toArray()))
        .build();
    return jdbcTemplate.query(select, new PricePromotionMapper());
  }

  public void batchInsert(String tenant, List<PricePromotion> promotionList) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(promotionList, "promotionList");
    List<InsertStatement> insertStatements = new ArrayList<>();
    for (PricePromotion promotion : promotionList) {
      InsertStatement insertStatement = buildInsertStatement(tenant, promotion);
      insertStatements.add(insertStatement);
    }
    batchUpdate(insertStatements);
  }

  public void insert(String tenant, PricePromotion promotion, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(promotion, "promotion");
    promotion.setCreateInfo(operateInfo);
    promotion.setLastModifyInfo(operateInfo);
    InsertStatement insert = buildInsertStatement(tenant, promotion);
    jdbcTemplate.update(insert);
  }

  public void update(String tenant, PricePromotion promotion, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(promotion, "promotion");
    promotion.setLastModifyInfo(operateInfo);
    UpdateStatement update = new UpdateBuilder().table(PPricePromotion.TABLE_NAME)
        .addValues(PROMOTION_MAPPER.forUpdate(promotion))
        .where(Predicates.equals(PPricePromotion.TENANT, tenant))
        .where(Predicates.equals(PPricePromotion.UUID, promotion.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public void changeState(String tenant, String uuid, PricePromotionState state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(state, "state");
    UpdateStatement update = new UpdateBuilder().table(PPricePromotion.TABLE_NAME)
        .addValue(PPricePromotion.STATE, state.name())
        .addValues(PPricePromotion.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PPricePromotion.TENANT, tenant))
        .where(Predicates.equals(PPricePromotion.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }

  public void changeState(String tenant, String uuid, PricePromotionState state, String reason,
      OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(state, "state");
    Assert.notNull(reason, "reason");
    UpdateStatement update = new UpdateBuilder().table(PPricePromotion.TABLE_NAME)
        .addValue(PPricePromotion.STATE, state.name())
        .addValue(PPricePromotion.REASON, reason)
        .addValues(PPricePromotion.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PPricePromotion.TENANT, tenant))
        .where(Predicates.equals(PPricePromotion.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }

  public void canceled(String tenant, String uuid, String reason, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(reason, "reason");
    UpdateStatement update = new UpdateBuilder().table(PPricePromotion.TABLE_NAME)
        .addValue(PPricePromotion.STATE, PricePromotionState.CANCELED.name())
        .addValues(PPricePromotion.toLastModifyInfoFieldValues(operateInfo))
        .addValue(PPricePromotion.REASON, reason)
        .where(Predicates.equals(PPricePromotion.TENANT, tenant))
        .where(Predicates.equals(PPricePromotion.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }

  private InsertStatement buildInsertStatement(String tenant, PricePromotion promotion) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(promotion, "promotion");
    promotion.setTenant(tenant);
    InsertStatement insert = new InsertBuilder().table(PPricePromotion.TABLE_NAME)
        .addValues(PROMOTION_MAPPER.forInsert(promotion))
        .build();
    return insert;
  }
}
