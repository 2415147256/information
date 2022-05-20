package com.hd123.baas.sop.service.dao.explosivev2.report;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2Line;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
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
 * 爆品活动日志行(ExplosiveLogV2Line)表数据库访问层
 *
 * @author liuhaoxin
 * @since 2021-12-07 18:12:47
 */
@Repository
public class ExplosiveLogV2LineDaoBof extends BofBaseDao {

  private static final TEMapper<ExplosiveLogV2Line> MAPPER = TEMapperBuilder
      .of(ExplosiveLogV2Line.class, ExplosiveLogV2Line.Schema.class)
      .primaryKey(ExplosiveLogV2Line.Schema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ExplosiveLogV2Line.class,
      ExplosiveLogV2Line.Schema.class).build();

  public void batchInsert(String tenant, List<ExplosiveLogV2Line> lines) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(lines, "lines");

    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveLogV2Line line : lines) {
      line.setTenant(tenant);
      InsertStatement insert = new InsertBuilder().table(ExplosiveLogV2Line.Schema.TABLE_NAME)
          .addValues(MAPPER.forInsert(line, true))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public List<ExplosiveLogV2Line> listByOwners(String tenant, List<String> owners) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(owners, "owners");

    SelectStatement select = new SelectBuilder().from(ExplosiveLogV2Line.Schema.TABLE_NAME)
        .where(Predicates.equals(ExplosiveLogV2Line.Schema.TENANT, tenant),
            Predicates.in2(ExplosiveLogV2Line.Schema.OWNER, owners.toArray()))
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }

  public void batchUpdateQty(String tenant, List<ExplosiveLogV2Line> lines) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(lines, "lines");
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveLogV2Line line : lines) {
      line.setTenant(tenant);
      UpdateStatement update = new UpdateBuilder()
          .table(ExplosiveLogV2Line.Schema.TABLE_NAME)
          .where(Predicates.equals(ExplosiveLogV2Line.Schema.OWNER, line.getOwner()))
          .where(Predicates.equals(ExplosiveLogV2Line.Schema.SKU_ID, line.getSkuId()))
          .setValue(ExplosiveLogV2Line.Schema.QTY, line.getQty())
          .build();
      batchUpdater.add(update);
    }
    batchUpdater.update();
  }
}

