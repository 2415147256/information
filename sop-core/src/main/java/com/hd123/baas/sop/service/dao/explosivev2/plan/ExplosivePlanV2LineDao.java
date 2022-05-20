package com.hd123.baas.sop.service.dao.explosivev2.plan;

import com.hd123.baas.sop.service.api.explosivev2.plan.ExplosivePlanV2Line;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author shenmin
 */
@Repository
public class ExplosivePlanV2LineDao {
  public static final TEMapper<ExplosivePlanV2Line> MAPPER = TEMapperBuilder.of(ExplosivePlanV2Line.class, PExplosivePlanV2Line.class)
      .primaryKey(PExplosivePlanV2Line.UUID)
      .build();
  private static final QueryProcessor QUERY_PROCESSOR =
      new QueryProcessorBuilder(ExplosivePlanV2Line.class, PExplosivePlanV2Line.class).build();

  @Autowired
  private JdbcTemplate jdbcTemplate;


  public void insert(String tenant, List<ExplosivePlanV2Line> lines){
    Assert.notBlank(tenant);
    Assert.notEmpty(lines);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ExplosivePlanV2Line line : lines) {
      line.setUuid(UUID.randomUUID().toString());
      line.setTenant(tenant);
      InsertStatement insert = new InsertBuilder()
          .table(PExplosivePlanV2Line.TABLE_NAME)
          .addValues(MAPPER.forInsert(line))
          .build();
      updater.add(insert);
    }
    updater.update();
  }


  public List<ExplosivePlanV2Line> listByOwner(String tenant, String owner){
    Assert.notBlank(tenant);
    Assert.notBlank(owner);
    SelectStatement select = new SelectBuilder()
        .from(PExplosivePlanV2Line.TABLE_NAME)
        .where(Predicates.equals(PExplosivePlanV2Line.TENANT, tenant))
        .where(Predicates.equals(PExplosivePlanV2Line.OWNER, owner))
        .orderBy(PExplosivePlanV2Line.LINE_NO, true)
        .build();
    return jdbcTemplate.query(select,MAPPER);
  }

  public void delete(String tenant, String owner) {
    Assert.notBlank(tenant);
    Assert.notBlank(owner);
    DeleteStatement delete = new DeleteBuilder()
        .table(PExplosivePlanV2Line.TABLE_NAME)
        .where(Predicates.equals(PExplosivePlanV2Line.TENANT, tenant))
        .where(Predicates.equals(PExplosivePlanV2Line.OWNER, owner))
        .build();
    jdbcTemplate.update(delete);
  }
}
