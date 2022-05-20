package com.hd123.baas.sop.service.dao.explosivev2;

import com.hd123.baas.sop.service.api.explosivev2.ExplosiveScope;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
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
import com.hd123.rumba.commons.util.converter.EnumConverters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author shenmin
 */
@Repository
public class ExplosiveScopeDao {
  public static final TEMapper<ExplosiveScope> MAPPER = TEMapperBuilder.of(ExplosiveScope.class, PExplosiveScope.class)
      .primaryKey(PExplosiveScope.UUID)
      .map("optionType", PExplosiveScope.OPTION_TYPE,
          EnumConverters.toString(ExplosiveScope.Type.class), EnumConverters.toEnum(ExplosiveScope.Type.class))
      .build();
  
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void insert(String tenant, List<ExplosiveScope> scopes){
    Assert.notBlank(tenant);
    Assert.notEmpty(scopes);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (ExplosiveScope scope : scopes) {
      scope.setUuid(UUID.randomUUID().toString());
      scope.setTenant(tenant);
      InsertStatement insert = new InsertBuilder()
          .table(PExplosiveScope.TABLE_NAME)
          .addValues(MAPPER.forInsert(scope))
          .build();
      updater.add(insert);
    }
    updater.update();
  }

  public void delete(String tenant, String owner) {
    Assert.notBlank(tenant);
    Assert.notBlank(owner);
    DeleteStatement delete = new DeleteBuilder()
        .table(PExplosiveScope.TABLE_NAME)
        .where(Predicates.equals(PExplosiveScope.TENANT, tenant))
        .where(Predicates.equals(PExplosiveScope.OWNER, owner))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<ExplosiveScope> listByOwner (String tenant, String owner) {
    Assert.notBlank(tenant);
    Assert.notBlank(owner);
    SelectStatement select = new SelectBuilder()
        .from(PExplosiveScope.TABLE_NAME)
        .where(Predicates.equals(PExplosiveScope.TENANT, tenant))
        .where(Predicates.equals(PExplosiveScope.OWNER, owner))
        .orderBy(PExplosiveScope.LINE_NO, true)
        .build();
    return jdbcTemplate.query(select, MAPPER);
  }

  public List<ExplosiveScope> listByOwners(String tenant, List<String> explosiveIds) {
    Assert.notBlank(tenant);
    Assert.notEmpty(explosiveIds);
    SelectStatement select = new SelectBuilder()
        .from(PExplosiveScope.TABLE_NAME)
        .where(Predicates.equals(PExplosiveScope.TENANT, tenant))
        .where(Predicates.in2(PExplosiveScope.OWNER, explosiveIds.toArray()))
        .build();
    List<ExplosiveScope> list = jdbcTemplate.query(select, MAPPER);
    return list.isEmpty() ? null : list;
  }
}
