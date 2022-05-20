package com.hd123.baas.sop.service.dao.screen;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.screen.PriceScreenShop;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;

/**
 * (PriceScreenShop)表数据库访问层
 *
 * @author makejava
 * @since 2021-08-09 18:25:21
 */
@Repository
public class PriceScreenShopDaoBof extends BofBaseDao {

  private static final TEMapper<PriceScreenShop> PRICE_SCREEN_SHOP_TE_MAPPER = TEMapperBuilder
      .of(PriceScreenShop.class, PriceScreenShop.PriceScreenShopSchema.class)
      .primaryKey(PriceScreenShop.PriceScreenShopSchema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PriceScreenShop.class,
      PriceScreenShop.PriceScreenShopSchema.class).build();

  public void batchSave(String tenant, List<PriceScreenShop> priceScreenShops) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(priceScreenShops, "priceScreenShops");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (PriceScreenShop priceScreenShop : priceScreenShops) {
      priceScreenShop.setTenant(tenant);
      InsertStatement insert = new InsertBuilder().table(PriceScreenShop.PriceScreenShopSchema.TABLE_NAME)
          .addValues(PRICE_SCREEN_SHOP_TE_MAPPER.forInsert(priceScreenShop, true))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public void removeByOwner(String tenant, String owner) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");

    DeleteStatement deleteStatement = new DeleteBuilder().table(PriceScreenShop.PriceScreenShopSchema.TABLE_NAME)
        .where(Predicates.equals(PriceScreenShop.PriceScreenShopSchema.TENANT, tenant))
        .where(Predicates.equals(PriceScreenShop.PriceScreenShopSchema.OWNER, owner))
        .build();
    jdbcTemplate.update(deleteStatement);
  }

  public List<PriceScreenShop> listByOwner(String tenant, String owner) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(owner, "owner");

    SelectStatement select = new SelectBuilder().from(PriceScreenShop.PriceScreenShopSchema.TABLE_NAME)
        .where(Predicates.equals(PriceScreenShop.PriceScreenShopSchema.TENANT, tenant),
            Predicates.equals(PriceScreenShop.PriceScreenShopSchema.OWNER, owner))
        .build();

    return jdbcTemplate.query(select, PRICE_SCREEN_SHOP_TE_MAPPER);
  }

  public List<PriceScreenShop> listByOwners(String tenant, Collection<String> owners) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(owners, "owners");

    SelectStatement select = new SelectBuilder().from(PriceScreenShop.PriceScreenShopSchema.TABLE_NAME)
        .where(Predicates.equals(PriceScreenShop.PriceScreenShopSchema.TENANT, tenant),
            Predicates.in2(PriceScreenShop.PriceScreenShopSchema.OWNER, owners.toArray()))
        .build();

    return jdbcTemplate.query(select, PRICE_SCREEN_SHOP_TE_MAPPER);
  }
}
