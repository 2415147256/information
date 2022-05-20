package com.hd123.baas.sop.service.dao.price.priceadjustment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceCompetitorLine;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.sql.*;

/**
 * @Author: maodapeng
 * @Date: 2020/12/4 15:45
 */
@Repository
public class PriceCompetitorLineDaoBof extends BofBaseDao {
  public List<PriceCompetitorLine> list(String tenant, String owner) {
    Assert.notNull(tenant, "租户");
    SelectStatement select = new SelectBuilder().select(PPriceCompetitorLine.allColumns())
        .from(PPriceCompetitorLine.TABLE_NAME)
        .where(Predicates.equals(PPriceCompetitorLine.TENANT, tenant))
        .where(Predicates.equals(PPriceCompetitorLine.OWNER, owner))
        .build();
    return jdbcTemplate.query(select, new PriceCompetitorLineMapper());
  }

  public PriceCompetitorLine get(String tenant, String owner, String skuId) {
    Assert.notNull(tenant, "租户");
    SelectStatement select = new SelectBuilder().select(PPriceCompetitorLine.allColumns())
        .from(PPriceCompetitorLine.TABLE_NAME)
        .where(Predicates.equals(PPriceCompetitorLine.TENANT, tenant))
        .where(Predicates.equals(PPriceCompetitorLine.OWNER, owner))
        .where(Predicates.equals(PPriceCompetitorLine.SKU_ID, skuId))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceCompetitorLineMapper()));
  }

  public void batchDelete(String tenant, String owner, Collection skuIds) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(tenant, "owner");
    Assert.notNull(skuIds, "skuIds");
    DeleteStatement deleteStatement = new DeleteBuilder().table(PPriceCompetitorLine.TABLE_NAME)
        .where(Predicates.equals(PPriceCompetitorLine.TENANT, tenant))
        .where(Predicates.equals(PPriceCompetitorLine.OWNER, owner))
        .where(Predicates.in(null, PPriceCompetitorLine.SKU_ID, skuIds.toArray()))
        .build();
    jdbcTemplate.update(deleteStatement);
  }

  public void batchInsert(String tenant, List<PriceCompetitorLine> lines) {
    Assert.notNull(tenant, "租户");
    List<InsertStatement> inserts = new ArrayList<>();
    for (PriceCompetitorLine line : lines) {
      InsertStatement insertStatement = buildInsertStatement(tenant, line);
      inserts.add(insertStatement);
    }
    batchUpdate(inserts);
  }

  public void batchUpdate(String tenant, List<PriceCompetitorLine> lines) {
    Assert.notNull(tenant, "租户");
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (PriceCompetitorLine line : lines) {
      UpdateStatement updateStatement = buildUpdateStatement(tenant, line);
      batchUpdater.add(updateStatement);
    }
    batchUpdater.update();
  }

  public void changeIgnore(String tenant, String owner, Collection<String> skuIds, boolean ignore) {
    if (CollectionUtils.isEmpty(skuIds)) {
      return;
    }
    UpdateStatement statement = new UpdateBuilder().table(PPriceCompetitorLine.TABLE_NAME)
        .setValue(PPriceCompetitorLine.IGNORE, ignore)
        .where(Predicates.in2(PPriceCompetitorLine.SKU_ID, skuIds.toArray()))
        .where(Predicates.equals(PPriceCompetitorLine.TENANT, tenant))
        .where(Predicates.equals(PPriceCompetitorLine.OWNER, owner))
        .build();
    jdbcTemplate.update(statement);
  }

  private InsertStatement buildInsertStatement(String tenant, PriceCompetitorLine line) {
    if (StringUtils.isBlank(line.getUuid())) {
      line.setUuid(UUID.randomUUID().toString());
    }
    return new InsertBuilder().table(PPriceCompetitorLine.TABLE_NAME)
        .addValue(PPriceCompetitorLine.TENANT, tenant)
        .addValue(PPriceCompetitorLine.UUID, line.getUuid())
        .addValue(PPriceCompetitorLine.OWNER, line.getOwner())
        .addValue(PPriceCompetitorLine.SKU_ID, line.getSkuId())
        .addValue(PPriceCompetitorLine.SKU_CODE, line.getSkuCode())
        .addValue(PPriceCompetitorLine.SALE_PRICE, line.getSalePrice())
        .addValue(PPriceCompetitorLine.QTY, line.getQty())
        .addValue(PPriceCompetitorLine.IGNORE, line.isIgnore())
        .build();
  }

  private UpdateStatement buildUpdateStatement(String tenant, PriceCompetitorLine line) {
    return new UpdateBuilder().table(PPriceCompetitorLine.TABLE_NAME)
        .setValue(PPriceCompetitorLine.SKU_ID, line.getSkuId())
        .setValue(PPriceCompetitorLine.SKU_CODE, line.getSkuCode())
        .setValue(PPriceCompetitorLine.SALE_PRICE, line.getSalePrice())
        .setValue(PPriceCompetitorLine.QTY, line.getQty())
        .where(Predicates.equals(PPriceCompetitorLine.UUID, line.getUuid()))
        .where(Predicates.equals(PPriceCompetitorLine.TENANT, line.getUuid()))
        .where(Predicates.equals(PPriceCompetitorLine.OWNER, line.getUuid()))
        .build();
  }
}
