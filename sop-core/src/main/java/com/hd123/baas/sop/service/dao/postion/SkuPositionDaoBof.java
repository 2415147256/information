package com.hd123.baas.sop.service.dao.postion;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.entity.SkuPosition;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

@Repository
public class SkuPositionDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(SkuPosition.class, PSkuPosition.class).build();

  public int insert(String tenant, SkuPosition skuPosition) {
    Assert.notNull(skuPosition);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuPosition.getName(), "名称");
    InsertStatement insert = new InsertBuilder().table(PSkuPosition.TABLE_NAME)
        .addValue(PSkuPosition.TENANT, tenant)
        .addValue(PSkuPosition.ORG_ID, skuPosition.getOrgId())
        .addValue(PSkuPosition.UUID, skuPosition.getUuid())
        .addValue(PSkuPosition.NAME, skuPosition.getName())
        .build();
    return jdbcTemplate.update(insert);
  }

  public int update(String tenant, SkuPosition skuPosition) {
    Assert.notNull(skuPosition);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(skuPosition.getUuid(), "uuid");
    Assert.notNull(skuPosition.getName(), "名称");
    UpdateStatement update = new UpdateBuilder().table(PSkuPosition.TABLE_NAME)
        .setValue(PSkuPosition.NAME, skuPosition.getName())
        .where(Predicates.equals(PSkuPosition.TENANT, tenant))
        .where(Predicates.equals(PSkuPosition.UUID, skuPosition.getUuid()))
        .build();
    return jdbcTemplate.update(update);

  }

  public void delete(String tenant, List<Integer> uuids) {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(uuids, "uuids");
    DeleteStatement delete = new DeleteBuilder().table(PSkuPosition.TABLE_NAME)
        .where(Predicates.in2(PSkuPosition.UUID, uuids.toArray()))
        .where(Predicates.equals(PSkuPosition.TENANT, tenant))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<SkuPosition> list(String tenant, String orgId) {
    Assert.notNull(tenant, "租戶");
    SelectStatement select = new SelectBuilder().select(PSkuPosition.allColumns())
        .from(PSkuPosition.TABLE_NAME)
        .where(Predicates.equals(PSkuPosition.TENANT, tenant))
        .build();
    if (StringUtils.isNotBlank(orgId)) {
      select.where(Predicates.equals(PSkuPosition.ORG_ID, orgId));
    }
    return jdbcTemplate.query(select, new SkuPositionMapper());
  }

  public QueryResult<SkuPosition> query(String tenant, QueryDefinition qd) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(SkuPosition.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new SkuPositionMapper());
  }

  public SkuPosition queryById(String tenant, Integer positionId) {
    Assert.notNull(tenant, "租戶");
    SelectStatement select = new SelectBuilder().select(PSkuPosition.allColumns())
        .from(PSkuPosition.TABLE_NAME)
        .where(Predicates.equals(PSkuPosition.TENANT, tenant))
        .where(Predicates.equals(PSkuPosition.UUID, positionId))
        .build();
    List<SkuPosition> query = jdbcTemplate.query(select, new SkuPositionMapper());
    return getFirst(query);
  }

  public SkuPosition queryByName(String tenant, String name) {
    Assert.notNull(tenant, "租戶");
    SelectStatement select = new SelectBuilder().select(PSkuPosition.allColumns())
        .from(PSkuPosition.TABLE_NAME)
        .where(Predicates.equals(PSkuPosition.TENANT, tenant))
        .where(Predicates.equals(PSkuPosition.NAME, name))
        .build();
    List<SkuPosition> query = jdbcTemplate.query(select, new SkuPositionMapper());
    return getFirst(query);
  }

  public List<SkuPosition> queryByNames(String tenant, String orgId, List<String> names) {
    Assert.notNull(tenant, "租戶");
    SelectStatement select = new SelectBuilder().select(PSkuPosition.allColumns())
        .from(PSkuPosition.TABLE_NAME)
        .where(Predicates.equals(PSkuPosition.TENANT, tenant))
        .where(Predicates.equals(PSkuPosition.ORG_ID, orgId))
        .where(Predicates.in2(PSkuPosition.NAME, names.toArray()))
        .build();
    return jdbcTemplate.query(select, new SkuPositionMapper());
  }

  public void batchInsert(String tenant, List<SkuPosition> skuPositions) {
    Assert.notNull(tenant, "租戶");
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (SkuPosition skuPosition : skuPositions) {
      Assert.notNull(skuPosition.getName(), "名称");
      InsertStatement insert = new InsertBuilder().table(PSkuPosition.TABLE_NAME)
          .addValue(PSkuPosition.TENANT, tenant)
          .addValue(PSkuPosition.ORG_ID, skuPosition.getOrgId())
          .addValue(PSkuPosition.UUID, skuPosition.getUuid())
          .addValue(PSkuPosition.NAME, skuPosition.getName())
          .build();
      updater.add(insert);
    }
    updater.update();

  }
}
