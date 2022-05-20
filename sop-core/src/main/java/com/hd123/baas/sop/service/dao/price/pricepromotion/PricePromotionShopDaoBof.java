package com.hd123.baas.sop.service.dao.price.pricepromotion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionShop;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/13.
 */
@Repository
public class PricePromotionShopDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PricePromotionShop.class,
      PPricePromotionShop.class).build();

  public QueryResult<PricePromotionShop> query(String tenant, String owner, QueryDefinition qd) {
    qd.addByField(PricePromotionShop.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(PricePromotionShop.Queries.OWNER, Cop.EQUALS, owner);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new PricePromotionShopMapper());
  }

  public long queryCount(String tenant, String owner, QueryDefinition qd) {
    qd.addByField(PricePromotionShop.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(PricePromotionShop.Queries.OWNER, Cop.EQUALS, owner);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    selectStatement.getSelectClause().getFields().clear();
    selectStatement.select("count(1)");
    QueryResult<Long> result = executor.query(selectStatement, new SingleColumnRowMapper<>(Long.class));
    if (result.getRecordCount() == 0) {
      return 0;
    }
    return result.getRecords().get(0);
  }

  public PricePromotionShop get(String tenant, String owner, String uuid) {
    SelectStatement select = new SelectBuilder().from(PPricePromotionShop.TABLE_NAME)
        .select(PPricePromotionShop.allColumns())
        .where(Predicates.equals(PPricePromotionShop.TENANT, tenant))
        .where(Predicates.equals(PPricePromotionShop.OWNER, owner))
        .where(Predicates.equals(PPricePromotionShop.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new PricePromotionShopMapper()));
  }

  public void insert(String tenant, String owner, PricePromotionShop shop) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notNull(shop, "shop");

    if (StringUtils.isBlank(shop.getUuid())) {
      shop.setUuid(UUID.randomUUID().toString());
    }
    InsertStatement insert = buildInsertStatement(tenant, owner, shop);
    jdbcTemplate.update(insert);
  }

  public void deleteByOwner(String tenant, String owner) {
    DeleteStatement delete = new DeleteBuilder().table(PPricePromotionShop.TABLE_NAME)
        .where(Predicates.equals(PPricePromotionShop.TENANT, tenant))
        .where(Predicates.equals(PPricePromotionShop.OWNER, owner))
        .build();
    jdbcTemplate.update(delete);
  }

  public void batchDelete(String tenant, String owner, Collection<String> uuids) {
    DeleteStatement delete = new DeleteBuilder().table(PPricePromotionShop.TABLE_NAME)
        .where(Predicates.equals(PPricePromotionShop.TENANT, tenant))
        .where(Predicates.equals(PPricePromotionShop.OWNER, owner))
        .where(Predicates.in2(PPricePromotionShop.UUID, uuids.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<PricePromotionShop> list(String tenant, String owner) {
    SelectStatement select = new SelectBuilder().from(PPricePromotionShop.TABLE_NAME)
        .select(PPricePromotionShop.allColumns())
        .where(Predicates.equals(PPricePromotionShop.TENANT, tenant))
        .where(Predicates.equals(PPricePromotionShop.OWNER, owner))
        .build();
    return jdbcTemplate.query(select, new PricePromotionShopMapper());
  }

  public List<PricePromotionShop> listByOwners(String tenant, Collection<String> owners) {
    if (CollectionUtils.isEmpty(owners)) {
      return new ArrayList<>();
    }
    SelectStatement select = new SelectBuilder().from(PPricePromotionShop.TABLE_NAME)
        .select(PPricePromotionShop.allColumns())
        .where(Predicates.equals(PPricePromotionShop.TENANT, tenant))
        .where(Predicates.in2(PPricePromotionShop.OWNER, owners.toArray()))
        .build();
    return jdbcTemplate.query(select, new PricePromotionShopMapper());
  }

  public void update(String tenant, String owner, PricePromotionShop shop) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notNull(shop, "shop");
    UpdateStatement update = new UpdateBuilder().table(PPricePromotionShop.TABLE_NAME)
        .addValue(PPricePromotionShop.SHOP, shop.getShop())
        .addValue(PPricePromotionShop.SHOP_CODE, shop.getShopCode())
        .addValue(PPricePromotionShop.SHOP_NAME, shop.getShopName())
        .where(Predicates.equals(PPricePromotionShop.TENANT, tenant))
        .where(Predicates.equals(PPricePromotionShop.OWNER, owner))
        .where(Predicates.equals(PPricePromotionShop.UUID, shop.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public void batchInsert(String tenant, String owner, Collection<PricePromotionShop> shops) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    if (CollectionUtils.isEmpty(shops)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (PricePromotionShop shop : shops) {
      if (StringUtils.isBlank(shop.getUuid())) {
        shop.setUuid(UUID.randomUUID().toString());
      }
      statements.add(buildInsertStatement(tenant, owner, shop));
    }
    batchUpdate(statements);
  }

  public void batchInsert(String tenant, Collection<PricePromotionShop> shops) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(shops)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (PricePromotionShop shop : shops) {
      Assert.notNull(shop.getOwner(), "owner");
      shop.setUuid(UUID.randomUUID().toString());

      statements.add(buildInsertStatement(tenant, shop.getOwner(), shop));
    }
    batchUpdate(statements);
  }

  private InsertStatement buildInsertStatement(String tenant, String owner, PricePromotionShop shop) {
    InsertStatement insert = new InsertBuilder().table(PPricePromotionShop.TABLE_NAME)
        .addValues(PPricePromotionShop.forSaveNew(shop))
        .addValue(PPricePromotionShop.TENANT, tenant)
        .addValue(PPricePromotionShop.OWNER, owner)
        .addValue(PPricePromotionShop.SHOP, shop.getShop())
        .addValue(PPricePromotionShop.SHOP_CODE, shop.getShopCode())
        .addValue(PPricePromotionShop.SHOP_NAME, shop.getShopName())
        .build();
    return insert;
  }

}
