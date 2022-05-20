package com.hd123.baas.sop.service.dao.screen;

import java.util.Date;

import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceAdjustment;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.screen.PriceScreen;
import com.hd123.baas.sop.service.api.screen.PriceScreenShop;
import com.hd123.baas.sop.service.api.screen.PriceScreenState;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;

/**
 * (PriceScreen)表数据库访问层
 *
 * @author makejava
 * @since 2021-08-09 11:39:31
 */
@Repository
public class PriceScreenDaoBof extends BofBaseDao {

  private static final TEMapper<PriceScreen> PRICE_SCREEN_TE_MAPPER = TEMapperBuilder
      .of(PriceScreen.class, PriceScreen.PriceScreenSchema.class)
      .map("state", PriceScreen.PriceScreenSchema.STATE, EnumConverters.toString(PriceScreenState.class),
          EnumConverters.toEnum(PriceScreenState.class))
      .primaryKey(PriceScreen.PriceScreenSchema.UUID)
      .build();

  private static final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PriceScreen.class,
      PriceScreen.PriceScreenSchema.class).addConditionProcessor((condition, context) -> {
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(PriceScreen.Queries.SHOP_KEYWORD_LIKE, condition.getOperation())) {
          String value = (String) condition.getParameter();
          return Predicates.or(Predicates.equals(PriceScreen.PriceScreenSchema.ALL_SHOPS, true),
              Predicates.exists(new SelectBuilder().select("1")
                  .from(PriceScreenShop.PriceScreenShopSchema.TABLE_NAME,
                      PriceScreenShop.PriceScreenShopSchema.TABLE_NAME)
                  .where(Predicates.or(
                      // 门店名称
                      Predicates.like(PriceScreenShop.PriceScreenShopSchema.TABLE_NAME,
                          PriceScreenShop.PriceScreenShopSchema.SHOP_NAME, value),
                      // 门店代码
                      Predicates.like(PriceScreenShop.PriceScreenShopSchema.TABLE_NAME,
                          PriceScreenShop.PriceScreenShopSchema.SHOP_CODE, value)))
                  .where(Predicates.equals(PriceScreenShop.PriceScreenShopSchema.TABLE_NAME,
                      PriceScreenShop.PriceScreenShopSchema.OWNER, alias, PriceScreen.PriceScreenSchema.UUID))
                  .where(Predicates.equals(PriceScreenShop.PriceScreenShopSchema.TABLE_NAME,
                      PriceScreen.PriceScreenSchema.TENANT, alias, PriceScreenShop.PriceScreenShopSchema.TENANT))
                  .build()));
        }
        return null;
      }).build();

  public void saveNew(String tenant, PriceScreen priceScreen, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(priceScreen, "priceScreen");

    priceScreen.setCreateInfo(operateInfo);
    priceScreen.setLastModifyInfo(operateInfo);
    priceScreen.setTenant(tenant);

    InsertStatement insert = new InsertBuilder().table(PriceScreen.PriceScreenSchema.TABLE_NAME)
        .values(PRICE_SCREEN_TE_MAPPER.forInsert(priceScreen, true))
        .build();
    jdbcTemplate.update(insert);
  }

  public void deleted(String tenant, String uuid, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    UpdateStatement update = new UpdateBuilder().table(PriceScreen.PriceScreenSchema.TABLE_NAME)
        .setValue(PriceScreen.PriceScreenSchema.DELETED, 1)
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_OPERATOR_FULL_NAME,
            operateInfo.getOperator().getFullName())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_OPERATOR_NAMESPACE,
            operateInfo.getOperator().getNamespace())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_TIME, operateInfo.getTime())
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.UUID, uuid))
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.TENANT, tenant))
        .build();
    jdbcTemplate.update(update);
  }

  public void update(String tenant, PriceScreen priceScreen, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(priceScreen, "priceScreen");

    priceScreen.setTenant(tenant);
    priceScreen.setLastModifyInfo(operateInfo);
    UpdateStatement update = new UpdateBuilder().table(PriceScreen.PriceScreenSchema.TABLE_NAME)
        .setValues(PRICE_SCREEN_TE_MAPPER.forUpdate(priceScreen, true))
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.UUID, priceScreen.getUuid()))
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.DELETED, 0))
        .build();
    jdbcTemplate.update(update);
  }

  public void terminate(String tenant, String uuid, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    UpdateStatement update = new UpdateBuilder().table(PriceScreen.PriceScreenSchema.TABLE_NAME)
        .setValue(PriceScreen.PriceScreenSchema.STATE, PriceScreenState.TERMINATED.name())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_OPERATOR_FULL_NAME,
            operateInfo.getOperator().getFullName())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_OPERATOR_NAMESPACE,
            operateInfo.getOperator().getNamespace())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_TIME, operateInfo.getTime())
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.UUID, uuid))
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.DELETED, 0))
        .build();
    jdbcTemplate.update(update);
  }



  public void effectByDate(String tenant, Date date, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(date, "date");

    UpdateStatement update = new UpdateBuilder().table(PriceScreen.PriceScreenSchema.TABLE_NAME)
        .setValue(PriceScreen.PriceScreenSchema.STATE, PriceScreenState.PUBLISHED.name())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_OPERATOR_FULL_NAME,
            operateInfo.getOperator().getFullName())
        .setValue(TempShopPriceAdjustment.Schema.LAST_MODIFY_INFO_OPERATOR_NAMESPACE,
            operateInfo.getOperator().getNamespace())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_TIME, operateInfo.getTime())
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.TENANT, tenant))
        .where(Predicates.lessOrEquals(PriceScreen.PriceScreenSchema.EFFECTIVE_START_TIME, date))
        .where(Predicates.greaterOrEquals(PriceScreen.PriceScreenSchema.EFFECTIVE_END_TIME, date))
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.DELETED, 0))
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.STATE, PriceScreenState.CONFIRMED.name()))
        .build();

    jdbcTemplate.update(update);
  }

  public void expireByDate(String tenant, Date date, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(date, "date");

    UpdateStatement update = new UpdateBuilder().table(PriceScreen.PriceScreenSchema.TABLE_NAME)
        .setValue(PriceScreen.PriceScreenSchema.STATE, PriceScreenState.EXPIRED.name())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_OPERATOR_FULL_NAME,
            operateInfo.getOperator().getFullName())
        .setValue(TempShopPriceAdjustment.Schema.LAST_MODIFY_INFO_OPERATOR_NAMESPACE,
            operateInfo.getOperator().getNamespace())
        .setValue(PriceScreen.PriceScreenSchema.LAST_MODIFY_INFO_TIME, operateInfo.getTime())
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.TENANT, tenant))
        .where(Predicates.lessOrEquals(PriceScreen.PriceScreenSchema.EFFECTIVE_END_TIME, date))
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.DELETED, 0))
        .where(Predicates.in2(PriceScreen.PriceScreenSchema.STATE, PriceScreenState.CONFIRMED.name(),
            PriceScreenState.PUBLISHED.name()))
        .build();
    jdbcTemplate.update(update);
  }

  public PriceScreen get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    SelectStatement select = new SelectBuilder().from(PriceScreen.PriceScreenSchema.TABLE_NAME)
        .where(Predicates.equals(PriceScreen.PriceScreenSchema.TENANT, tenant),
            Predicates.equals(PriceScreen.PriceScreenSchema.UUID, uuid),
            Predicates.equals(PriceScreen.PriceScreenSchema.DELETED, 0))
        .build();
    return getFirst(select, PRICE_SCREEN_TE_MAPPER);
  }

  public QueryResult<PriceScreen> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(PriceScreen.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(PriceScreen.Queries.DELETED, Cop.EQUALS, 0);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, PRICE_SCREEN_TE_MAPPER);
  }
}
