package com.hd123.baas.sop.service.dao.price.tempshopadjustment;

import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import com.alibaba.druid.util.StringUtils;
import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceAdjustmentLine;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class TempShopPriceAdjustmentLineDaoBof extends BofBaseDao {

  private static final TEMapper<TempShopPriceAdjustmentLine> TEMP_SHOP_PRICE_ADJUSTMENT_LINE_TE_MAPPER = TEMapperBuilder
      .of(TempShopPriceAdjustmentLine.class, TempShopPriceAdjustmentLine.Schema.class)
      .primaryKey(TempShopPriceAdjustmentLine.Schema.UUID)
      .build();

  public void batchInsert(String tenant, String owner, List<TempShopPriceAdjustmentLine> lines) {
    Assert.notNull(tenant, "tenant");
    Assert.hasText(owner, "owner");
    Assert.notEmpty(lines);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (TempShopPriceAdjustmentLine line : lines) {
      updater.add(buildInsertStatement(tenant, owner, line));
    }
    updater.update();
  }

  public List<TempShopPriceAdjustmentLine> list(String tenant, String owner) {
    Assert.notNull(tenant, "tenant");
    Assert.hasText(owner, "owner");
    SelectStatement selectStatement = new SelectBuilder().from(TempShopPriceAdjustmentLine.Schema.TABLE_NAME)
        .where(Predicates.equals(TempShopPriceAdjustmentLine.Schema.TENANT, tenant))
        .where(Predicates.equals(TempShopPriceAdjustmentLine.Schema.OWNER, owner))
        .build();
    return jdbcTemplate.query(selectStatement, TEMP_SHOP_PRICE_ADJUSTMENT_LINE_TE_MAPPER);
  }

  public void delete(String tenant, String owner) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owner, "owner");
    DeleteStatement delete = new DeleteBuilder().table(TempShopPriceAdjustmentLine.Schema.TABLE_NAME)
        .where(Predicates.equals(TempShopPriceAdjustmentLine.Schema.TENANT, tenant))
        .where(Predicates.equals(TempShopPriceAdjustmentLine.Schema.OWNER, owner))
        .build();
    jdbcTemplate.update(delete);
  }

  public long count(String tenant, String owner) {
    SelectStatement selectStatement = new SelectBuilder().select("count(1)")
        .from(TempShopPriceAdjustmentLine.Schema.TABLE_NAME)
        .where(Predicates.equals(TempShopPriceAdjustmentLine.Schema.TENANT, tenant))
        .where(Predicates.equals(TempShopPriceAdjustmentLine.Schema.OWNER, owner))
        .build();
    List<Integer> shops = jdbcTemplate.query(selectStatement, new SingleColumnRowMapper<>(Integer.class));
    if (CollectionUtils.isEmpty(shops)) {
      return 0L;
    }
    return shops.get(0);
  }

  private InsertStatement buildInsertStatement(String tenant, String owner, TempShopPriceAdjustmentLine line) {
    Assert.notNull(owner, "owner");
    Assert.notNull(line.getSkuId(), "skuId");
    if (StringUtils.isEmpty(line.getUuid())) {
      line.setUuid(UUID.randomUUID().toString());
    }
    return new InsertBuilder().table(TempShopPriceAdjustmentLine.Schema.TABLE_NAME)
        .addValue(TempShopPriceAdjustmentLine.Schema.UUID, line.getUuid())
        .addValue(TempShopPriceAdjustmentLine.Schema.TENANT, tenant)
        .addValue(TempShopPriceAdjustmentLine.Schema.OWNER, owner)
        .addValue(TempShopPriceAdjustmentLine.Schema.SKU_ID, line.getSkuId())
        .addValue(TempShopPriceAdjustmentLine.Schema.SKU_CODE, line.getSkuCode())
        .addValue(TempShopPriceAdjustmentLine.Schema.SKU_NAME, line.getSkuName())
        .addValue(TempShopPriceAdjustmentLine.Schema.SKU_QPC, line.getSkuQpc())
        .addValue(TempShopPriceAdjustmentLine.Schema.SKU_GID, line.getSkuGid())
        .addValue(TempShopPriceAdjustmentLine.Schema.BASE_SHOP_PRICE, line.getBaseShopPrice())
        .build();
  }

}
