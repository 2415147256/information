package com.hd123.baas.sop.service.dao.explosivev2.sign;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveSignV2Line;
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
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 爆品活动行(ExplosiveSignV2Line)表数据库访问层
 *
 * @author liuhaoxin
 * @since 2021-12-03 13:34:19
 */
@Repository
public class ExplosiveV2SignLineDaoBof extends BofBaseDao {

  private static final TEMapper<ExplosiveSignV2Line> MAPPER = TEMapperBuilder
      .of(ExplosiveSignV2Line.class, ExplosiveSignV2Line.Schema.class)
      .primaryKey(ExplosiveSignV2Line.Schema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ExplosiveSignV2Line.class,
      ExplosiveSignV2Line.Schema.class).build();

  public void batchInsert(String tenant, List<ExplosiveSignV2Line> lines) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(lines, "lines");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveSignV2Line line : lines) {
      line.setTenant(tenant);
      InsertStatement insert = new InsertBuilder().table(ExplosiveSignV2Line.Schema.TABLE_NAME)
          .addValues(MAPPER.forInsert(line, true))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public void delByOwner(String tenant, String owner) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");

    DeleteStatement deleteStatement = new DeleteBuilder().table(ExplosiveSignV2Line.Schema.TABLE_NAME)
        .where(Predicates.equals(ExplosiveSignV2Line.Schema.TENANT, tenant))
        .where(Predicates.equals(ExplosiveSignV2Line.Schema.OWNER, owner))
        .build();
    jdbcTemplate.update(deleteStatement);
  }

  public void updateLineQty(String tenant, String uuid, List<ExplosiveSignV2Line> lines) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(lines, "explosiveSignV2Line");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveSignV2Line line : lines) {
      line.setTenant(tenant);
      UpdateStatement update = new UpdateBuilder().table(ExplosiveSignV2Line.Schema.TABLE_NAME)
          .setValue(ExplosiveSignV2Line.Schema.QTY, line.getQty())
          .where(Predicates.equals(ExplosiveSignV2Line.Schema.TENANT, tenant))
          .where(Predicates.equals(ExplosiveSignV2Line.Schema.OWNER, uuid))
          .where(Predicates.equals(ExplosiveSignV2Line.Schema.SKU_ID, line.getSkuId()))
          .build();
      updater.add(update);
    }
    updater.update();
  }

  public ExplosiveSignV2Line get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    SelectStatement select = new SelectBuilder().from(ExplosiveSignV2Line.Schema.TABLE_NAME)
        .where(Predicates.equals(ExplosiveSignV2Line.Schema.TENANT, tenant),
            Predicates.equals(ExplosiveSignV2Line.Schema.UUID, uuid))
        .build();
    return getFirst(select, MAPPER);
  }

  public List<ExplosiveSignV2Line> listByOwners(String tenant, List<String> owners) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(owners, "owners");

    SelectStatement select = new SelectBuilder().from(ExplosiveSignV2Line.Schema.TABLE_NAME)
        .where(Predicates.equals(ExplosiveSignV2Line.Schema.TENANT, tenant),
            Predicates.in2(ExplosiveSignV2Line.Schema.OWNER, owners.toArray()))
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }

  public List<ExplosiveSignV2Line> listByUuids(String tenant, List<String> uuids) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuids, "uuids");

    SelectStatement select = new SelectBuilder().from(ExplosiveSignV2Line.Schema.TABLE_NAME)
        .where(Predicates.equals(ExplosiveSignV2Line.Schema.TENANT, tenant),
            Predicates.in2(ExplosiveSignV2Line.Schema.UUID, uuids.toArray()))
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }
}

