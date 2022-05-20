package com.hd123.baas.sop.service.dao.price.tempshopadjustment;

import java.util.Collection;
import java.util.Date;

import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceManager;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class TempShopPriceManagerDaoBof extends BofBaseDao {

  private static final TEMapper<TempShopPriceManager> TEMP_SHOP_PRICE_MANAGER_TE_MAPPER = TEMapperBuilder
      .of(TempShopPriceManager.class, TempShopPriceManager.Schema.class)
      .primaryKey(TempShopPriceManager.Schema.UUID)
      .build();

  private static QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TempShopPriceManager.class,
      TempShopPriceManager.Schema.class).build();

  public void batchInsert(String tenant, Collection<TempShopPriceManager> managerList) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(managerList);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (TempShopPriceManager priceManager : managerList) {
      if (priceManager.getUuid() == null) {
        priceManager.setUuid(IdGenUtils.buildRdUuid());
      }
      InsertStatement insertStatement = new InsertBuilder().table(TempShopPriceManager.Schema.TABLE_NAME)
          .addValues(TEMP_SHOP_PRICE_MANAGER_TE_MAPPER.forInsert(priceManager))
          .build();
      updater.add(insertStatement);
    }
    updater.update();
  }


  public void delete(String tenant, String shop, Date effectiveDate, Collection<String> skuIds) {
    DeleteStatement deleteStatement = new DeleteBuilder().table(TempShopPriceManager.Schema.TABLE_NAME)
        .where(Predicates.equals(TempShopPriceManager.Schema.TENANT, tenant))
        .where(Predicates.equals(TempShopPriceManager.Schema.SHOP, shop))
        .where(Predicates.in2(TempShopPriceManager.Schema.SKU_ID, skuIds.toArray()))
        .where(Predicates.equals(TempShopPriceManager.Schema.EFFECTIVE_DATE, effectiveDate))
        .build();
    jdbcTemplate.update(deleteStatement);
  }

  public void deleteBeforeDate(String tenant,String orgId, Date effectiveDate) {
    DeleteStatement deleteStatement = new DeleteBuilder().table(TempShopPriceManager.Schema.TABLE_NAME)
        .where(Predicates.equals(TempShopPriceManager.Schema.TENANT, tenant))
        .where(Predicates.equals(TempShopPriceManager.Schema.ORG_ID, orgId))
        .where(Predicates.lessOrEquals(TempShopPriceManager.Schema.EFFECTIVE_DATE, effectiveDate))
        .build();
    jdbcTemplate.update(deleteStatement);
  }

  public QueryResult<TempShopPriceManager> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(TempShopPriceManager.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, TEMP_SHOP_PRICE_MANAGER_TE_MAPPER);
  }
}
