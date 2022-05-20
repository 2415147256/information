package com.hd123.baas.sop.service.dao.postion;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.entity.SkuPositionAssoc;
import com.hd123.baas.sop.service.dao.grade.PPriceGrade;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

@Repository
public class SkuPositionAssocDaoBof extends BofBaseDao {
  public int insert(String tenant, SkuPositionAssoc skuPositionAssoc) {
    Assert.notNull(skuPositionAssoc);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuPositionAssoc.getSkuId(), "skuId");
    Assert.notNull(skuPositionAssoc.getSkuPositionId(), "skuPositionId");
    InsertStatement insert = new InsertBuilder().table(PPriceGrade.TABLE_NAME)
        .addValue(PSkuPositionAssoc.TENANT, tenant)
        .addValue(PSkuPositionAssoc.UUID, skuPositionAssoc.getUuid())
        .addValue(PSkuPositionAssoc.SKU_ID, skuPositionAssoc.getSkuId())
        .addValue(PSkuPositionAssoc.SKU_POSITION_ID, skuPositionAssoc.getSkuPositionId())
        .build();
    return jdbcTemplate.update(insert);
  }

  public List<int[]> batchInsert(String tenant, List<SkuPositionAssoc> skuPositionAssocs) {
    Assert.notNull(skuPositionAssocs);
    Assert.notNull(tenant, "租戶");
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (SkuPositionAssoc assoc : skuPositionAssocs) {
      InsertBuilder insert = new InsertBuilder().table(PSkuPositionAssoc.TABLE_NAME)
          .addValue(PSkuPositionAssoc.TENANT, tenant)
          .addValue(PSkuPositionAssoc.UUID, assoc.getUuid())
          .addValue(PSkuPositionAssoc.SKU_ID, assoc.getSkuId())
          .addValue(PSkuPositionAssoc.SKU_POSITION_ID, assoc.getSkuPositionId());
      batchUpdater.add(insert.build());
    }
    return batchUpdater.update();
  }

  public int update(String tenant, SkuPositionAssoc assoc) {
    Assert.notNull(assoc);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(assoc.getUuid(), "uuid");
    Assert.notNull(assoc.getSkuId(), "skuId");
    Assert.notNull(assoc.getSkuPositionId(), "skuPositionId");
    UpdateStatement update = new UpdateBuilder().table(PSkuPositionAssoc.TABLE_NAME)
        .setValue(PSkuPositionAssoc.SKU_ID, assoc.getSkuId())
        .setValue(PSkuPositionAssoc.SKU_POSITION_ID, assoc.getSkuPositionId())
        .where(Predicates.equals(PSkuPositionAssoc.TENANT, tenant))
        .where(Predicates.equals(PSkuPositionAssoc.UUID, assoc.getUuid()))
        .build();
    return jdbcTemplate.update(update);

  }

  public void delete(String tenant, List<Integer> uuids) {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(uuids, "uuids");
    DeleteStatement delete = new DeleteBuilder().table(PSkuPositionAssoc.TABLE_NAME)
        .where(Predicates.in2(PSkuPositionAssoc.UUID, uuids.toArray()))
        .where(Predicates.equals(PSkuPositionAssoc.TENANT, tenant))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<int[]> batchDelete(String tenant, List<SkuPositionAssoc> skuPositionAssocs) {
    Assert.notNull(skuPositionAssocs);
    Assert.notNull(tenant, "租戶");
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (SkuPositionAssoc assoc : skuPositionAssocs) {
      DeleteBuilder delete = new DeleteBuilder().table(PSkuPositionAssoc.TABLE_NAME)
          .where(Predicates.equals(PSkuPositionAssoc.TENANT, tenant))
          .where(Predicates.equals(PSkuPositionAssoc.SKU_ID, assoc.getSkuId()))
          .where(Predicates.equals(PSkuPositionAssoc.SKU_POSITION_ID, assoc.getSkuPositionId()));
      batchUpdater.add(delete.build());
    }
    return batchUpdater.update();
  }

  public List<SkuPositionAssoc> list(String tenant) {
    Assert.notNull(tenant, "租戶");
    SelectStatement select = new SelectBuilder().select(PPriceGrade.allColumns())
        .from(PSkuPositionAssoc.TABLE_NAME)
        .where(Predicates.equals(PSkuPositionAssoc.TENANT, tenant))
        .build();
    return jdbcTemplate.query(select, new SkuPositionAssocMapper());
  }

}
