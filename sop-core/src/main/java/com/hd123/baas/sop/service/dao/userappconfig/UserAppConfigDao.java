package com.hd123.baas.sop.service.dao.userappconfig;

import java.util.List;

import com.hd123.baas.sop.service.api.userappconfig.UserAppConfig;
import com.hd123.baas.sop.utils.IdGenUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;

@Repository
public class UserAppConfigDao {
  private static final TEMapper<UserAppConfig> MAPPER = TEMapperBuilder.of(UserAppConfig.class, PUserAppConfig.class).build();

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public UserAppConfig getById(String tenant, String appId, String id) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(appId, "appId");
    Assert.hasText(id, "id");
    SelectStatement selectStatement = new SelectBuilder().from(PUserAppConfig.TABLE_NAME)
        .where(Predicates.equals(PUserAppConfig.TENANT, tenant))
        .where(Predicates.equals(PUserAppConfig.APP_ID, appId))
        .where(Predicates.equals(PUserAppConfig.ID, id))
        .build();
    List<UserAppConfig> records = jdbcTemplate.query(selectStatement, MAPPER);
    if (CollectionUtils.isNotEmpty(records)) {
      return records.get(0);
    }
    return null;
  }

  public int insert(String tenant, UserAppConfig userAppConfig) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(userAppConfig, "userAppConfig");
    userAppConfig.setTenant(tenant);
    if (StringUtils.isEmpty(userAppConfig.getUuid())) {
      userAppConfig.setUuid(IdGenUtils.buildRdUuid());
    }
    InsertStatement insertStatement = new InsertBuilder().table(PUserAppConfig.TABLE_NAME)
        .values(MAPPER.forInsert(userAppConfig))
        .build();
    return jdbcTemplate.update(insertStatement);
  }

  public int updateExt(String tenant, UserAppConfig userAppConfig) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(userAppConfig, "userAppConfig");
    UpdateStatement updateStatement = new UpdateBuilder()
        .table(PUserAppConfig.TABLE_NAME)
        .setValue(PUserAppConfig.EXT, userAppConfig.getExt())
        .where(Predicates.equals(PUserAppConfig.TENANT, tenant))
        .where(Predicates.equals(PUserAppConfig.APP_ID, userAppConfig.getAppId()))
        .where(Predicates.equals(PUserAppConfig.ID, userAppConfig.getId()))
        .build();
    return jdbcTemplate.update(updateStatement);
  }
}
