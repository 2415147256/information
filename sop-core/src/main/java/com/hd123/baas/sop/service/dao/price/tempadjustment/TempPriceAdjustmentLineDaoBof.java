package com.hd123.baas.sop.service.dao.price.tempadjustment;

import java.util.List;

import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempShop;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.jdbc.sql.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempPriceAdjustmentLine;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class TempPriceAdjustmentLineDaoBof extends BofBaseDao {

  public static QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(TempPriceAdjustmentLine.class,
      PTempPriceAdjustmentLine.class).build();

  public void batchInsert(String tenant, String owner, List<TempPriceAdjustmentLine> lines) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notEmpty(lines);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (TempPriceAdjustmentLine line : lines) {
      InsertStatement statement = new InsertBuilder().table(PTempPriceAdjustmentLine.TABLE_NAME)
          .addValues(PTempPriceAdjustmentLine.forSaveNew(line))
          .addValue(PTempPriceAdjustmentLine.TENANT, tenant)
          .addValue(PTempPriceAdjustmentLine.OWNER, owner)
          .addValue(PTempPriceAdjustmentLine.SHOP, line.getShop())
          .addValue(PTempPriceAdjustmentLine.SHOP_CODE, line.getShopCode())
          .addValue(PTempPriceAdjustmentLine.SHOP_NAME, line.getShopName())
          .addValue(PTempPriceAdjustmentLine.SKU_ID, line.getSkuId())
          .addValue(PTempPriceAdjustmentLine.SKU_GID, line.getSkuGid())
          .addValue(PTempPriceAdjustmentLine.SKU_CODE, line.getSkuCode())
          .addValue(PTempPriceAdjustmentLine.SKU_NAME, line.getSkuName())
          .addValue(PTempPriceAdjustmentLine.SKU_QPC, line.getSkuQpc())
          .addValue(PTempPriceAdjustmentLine.SALE_PRICE, line.getSalePrice())
          .build();
      updater.add(statement);
    }
    updater.update();
  }

  public void delete(String tenant, String owner) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");
    DeleteStatement delete = new DeleteBuilder().table(PTempPriceAdjustmentLine.TABLE_NAME)
        .where(Predicates.equals(PTempPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PTempPriceAdjustmentLine.OWNER, owner))
        .build();
    jdbcTemplate.update(delete);
  }

  public long countShop(String tenant, String owner) {
    SelectStatement selectStatement = new SelectBuilder().select(PTempPriceAdjustmentLine.SHOP)
        .from(PTempPriceAdjustmentLine.TABLE_NAME)
        .where(Predicates.equals(PTempPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PTempPriceAdjustmentLine.OWNER, owner))
        .build();
    selectStatement.distinct();
    List<String> shops = jdbcTemplate.query(selectStatement, new SingleColumnRowMapper<>(String.class));
    if (CollectionUtils.isEmpty(shops)) {
      return new Long(0);
    }
    return new Long(shops.size());
  }

  public long count(String tenant, String owner) {
    SelectStatement selectStatement = new SelectBuilder().from(PTempPriceAdjustmentLine.TABLE_NAME)
        .where(Predicates.equals(PTempPriceAdjustmentLine.TENANT, tenant))
        .where(Predicates.equals(PTempPriceAdjustmentLine.OWNER, owner))
        .build();
    selectStatement.select("count(1)");
    List<Long> count = jdbcTemplate.query(selectStatement, new SingleColumnRowMapper<>(Long.class));
    if (CollectionUtils.isEmpty(count)) {
      return new Long(0);
    }
    return count.get(0);
  }

  public QueryResult<TempPriceAdjustmentLine> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(TempPriceAdjustmentLine.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new TempPriceAdjustmentLineMapper());
  }

  public QueryResult<TempShop> queryShop(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    select.getSelectClause().getFields().clear();
    select.select(PTempPriceAdjustmentLine.SHOP);
    select.select(PTempPriceAdjustmentLine.SHOP_CODE);
    select.select(PTempPriceAdjustmentLine.SHOP_NAME);
    select.distinct();
    return executor.query(select, new TempShopMapper());
  }
}
