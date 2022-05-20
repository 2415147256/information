package com.hd123.baas.sop.service.dao.application;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.appmanage.UserModule;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserApplicationDaoBof extends BofBaseDao {

  public List<UserModule> queryApplicationIdsByUserId(String tenant, String userId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(userId, "userId");
    SelectBuilder select = new SelectBuilder().from(PUserApplication.TABLE_NAME)
        .where(Predicates.equals(PUserApplication.TENANT, tenant))
        .where(Predicates.equals(PUserApplication.USER_ID, userId))
        .orderBy(PUserApplication.SORT, true);
    return jdbcTemplate.query(select.build(), new UserApplicationMapper());
  }

  public void deleteByUserId(String tenant, String userId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(userId, "用户id");
    DeleteBuilder delete = new DeleteBuilder().table(PUserApplication.TABLE_NAME)
        .where(Predicates.equals(PUserApplication.TENANT, tenant))
        .where(Predicates.equals(PUserApplication.USER_ID, userId));
    jdbcTemplate.update(delete.build());
  }

  public void batchSave(String tenant, String userId, List<UserModule> applications, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(userId, "用户id");
    Assert.notEmpty(applications);
    List<InsertStatement> list = new ArrayList<>();
    for (UserModule application : applications) {
      InsertBuilder insert = new InsertBuilder().table(PUserApplication.TABLE_NAME)
          .addValue(PUserApplication.TENANT, tenant)
          .addValue(PUserApplication.UUID, UUID.randomUUID().toString())
          .addValue(PUserApplication.MODULE, application.getApplication())
          .addValue(PUserApplication.USER_ID, userId)
          .addValue(PUserApplication.SORT, application.getSort());
      if (operateInfo != null) {
        insert.addValues(PStandardEntity.toCreateInfoFieldValues(operateInfo));
      }
      list.add(insert.build());
    }
    batchUpdate(list);
  }
}
