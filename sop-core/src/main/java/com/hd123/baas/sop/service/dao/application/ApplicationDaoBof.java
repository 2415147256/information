package com.hd123.baas.sop.service.dao.application;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.appmanage.Module;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.lang.Assert;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationDaoBof extends BofBaseDao {

  public List<Module> all(String tenant) {
    Assert.notNull(tenant, "租户");
    SelectBuilder select = new SelectBuilder().from(PApplication.TABLE_NAME)
        .where(Predicates.equals(PApplication.TENANT, tenant));
    return jdbcTemplate.query(select.build(), new ApplicationMapper());
  }

  public List<Module> queryByIds(String tenant, List<String> list) {
    Assert.notNull(tenant,"租户");
    Assert.notEmpty(list,"ids");

    SelectBuilder select = new SelectBuilder().from(PApplication.TABLE_NAME)
            .where(Predicates.equals(PApplication.TENANT,tenant))
            .where(Predicates.in2(PApplication.UUID,list.toArray()))
            .orderBy(setUUIDOrder(list));
    return jdbcTemplate.query(select.build(),new ApplicationMapper());
  }

  private String setUUIDOrder(List<String> list){
    String uuids = "";
    for (String s : list) {
      uuids = s + ",";
    }
    uuids = uuids.substring(0,uuids.length() - 1);
    return "FIELD(UUID," + uuids +")";
  }
}
