package com.hd123.baas.sop.service.dao.explosivev2;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Line;
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
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author shenmin
 */
@Repository
public class ExplosiveV2LineDao {
  public static final TEMapper<ExplosiveV2Line> MAPPER = TEMapperBuilder.of(ExplosiveV2Line.class, PExplosiveV2Line.class)
      .primaryKey(PExplosiveV2Line.UUID)
      .build();

  private static final QueryProcessor QUERY_PROCESSOR =
      new QueryProcessorBuilder(ExplosiveV2Line.class, PExplosiveV2Line.class).build();

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Tx
  public void insert(String tenant, List<ExplosiveV2Line> lines) {
    Assert.notBlank(tenant);
    Assert.notEmpty(lines);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveV2Line line : lines) {
      line.setUuid(UUID.randomUUID().toString());
      line.setTenant(tenant);
      InsertStatement insert = new InsertBuilder()
          .table(PExplosiveV2Line.TABLE_NAME)
          .addValues(MAPPER.forInsert(line))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public List<ExplosiveV2Line> listByOwner(String tenant, String owner) {
    Assert.notBlank(tenant);
    Assert.notBlank(owner);
    SelectStatement select = new SelectBuilder()
        .from(PExplosiveV2Line.TABLE_NAME)
        .where(Predicates.equals(PExplosiveV2Line.TENANT, tenant))
        .where(Predicates.equals(PExplosiveV2Line.OWNER, owner))
        .orderBy(PExplosiveV2Line.LINE_NO, true)
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }

  public void delete(String tenant, String owner) {
    Assert.notBlank(tenant);
    Assert.notBlank(owner);
    DeleteStatement delete = new DeleteBuilder()
        .table(PExplosiveV2Line.TABLE_NAME)
        .where(Predicates.equals(PExplosiveV2Line.TENANT, tenant))
        .where(Predicates.equals(PExplosiveV2Line.OWNER, owner))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<ExplosiveV2Line> listByOwners(String tenant, List<String> explosiveIds) {
    Assert.notBlank(tenant);
    Assert.notEmpty(explosiveIds);
    SelectStatement select = new SelectBuilder()
        .from(PExplosiveV2Line.TABLE_NAME)
        .where(Predicates.equals(PExplosiveV2Line.TENANT, tenant))
        .where(Predicates.in2(PExplosiveV2Line.OWNER, explosiveIds.toArray()))
        .build();
    List<ExplosiveV2Line> list = jdbcTemplate.query(select, MAPPER);
    return list.isEmpty() ? null : list;
  }

  public List<ExplosiveV2Line> listBySkuIds(String tenant, String owner, Set<String> skuIds) {
    Assert.notBlank(tenant);
    Assert.notBlank(owner);
    Assert.notEmpty(skuIds);
    SelectStatement select = new SelectBuilder()
        .from(PExplosiveV2Line.TABLE_NAME)
        .where(Predicates.equals(PExplosiveV2Line.TENANT, tenant))
        .where(Predicates.equals(PExplosiveV2Line.OWNER, owner))
        .where(Predicates.in2(PExplosiveV2Line.SKU_ID, skuIds.toArray()))
        .forUpdate()
        .build();
    List<ExplosiveV2Line> list = jdbcTemplate.query(select, MAPPER);
    return list.isEmpty() ? null : list;
  }

  public void batchUpdate(String tenant, String owner, List<ExplosiveV2Line> lines) {
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveV2Line line : lines) {
      line.setTenant(tenant);
      UpdateStatement update = new UpdateBuilder()
          .table(PExplosiveV2Line.TABLE_NAME)
          .where(Predicates.equals(PExplosiveV2Line.UUID, line.getUuid()))
          .where(Predicates.equals(PExplosiveV2Line.OWNER, owner))
          .where(Predicates.equals(PExplosiveV2Line.SKU_ID, line.getSkuId()))
          .addValues(MAPPER.forUpdate(line))
          .build();
      batchUpdater.add(update);
    }
    batchUpdater.update();

  }
}
