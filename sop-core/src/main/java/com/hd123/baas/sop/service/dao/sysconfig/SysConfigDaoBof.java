package com.hd123.baas.sop.service.dao.sysconfig;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.sysconfig.SysConfig;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Repository
public class SysConfigDaoBof extends BofBaseDao {

  static final SysConfigMapper MAPPER = new SysConfigMapper();

  public void insert(String tenant, SysConfig item) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(item, "uuid");

    InsertBuilder insert = new InsertBuilder().table(PSysConfig.TABLE_NAME);
    insert.addValue(PSysConfig.TENANT, tenant);
    insert.addValue(PSysConfig.CF_KYE, item.getCfKey());
    insert.addValue(PSysConfig.SPEC, item.getSpec());
    insert.addValue(PSysConfig.CF_VALUE, item.getCfValue());
    jdbcTemplate.update(insert.build());
  }

  public void update(String tenant, SysConfig item) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(item, "uuid");

    UpdateBuilder update = new UpdateBuilder().table(PSysConfig.TABLE_NAME);
    update.setValue(PSysConfig.CF_VALUE, item.getCfValue());

    update.where(Predicates.equals(PSysConfig.TENANT, tenant));
    update.where(Predicates.equals(PSysConfig.SPEC, item.getSpec()));
    update.where(Predicates.equals(PSysConfig.CF_KYE, item.getCfKey()));
    jdbcTemplate.update(update.build());
  }

  public SysConfig get(String tenant, String spec, String cfKey) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(cfKey, "key");
    SelectBuilder select = new SelectBuilder().from(PSysConfig.TABLE_NAME);

    select.where(Predicates.equals(PSysConfig.TENANT, tenant));
    select.where(Predicates.equals(PSysConfig.CF_KYE, cfKey));
    select.where(Predicates.equals(PSysConfig.SPEC, spec));
    List<SysConfig> query = jdbcTemplate.query(select.build(), MAPPER);
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    return query.get(0);
  }

  public List<SysConfig> list(String tenant) {
    Assert.notNull(tenant, "租户");
    SelectBuilder select = new SelectBuilder().from(PSysConfig.TABLE_NAME);

    select.where(Predicates.equals(PSysConfig.TENANT, tenant));
    return jdbcTemplate.query(select.build(), MAPPER);
  }

  public List<SysConfig> list(String tenant, String spec) {
    Assert.notNull(tenant, "租户");
    SelectBuilder select = new SelectBuilder().from(PSysConfig.TABLE_NAME);

    select.where(Predicates.equals(PSysConfig.TENANT, tenant));
    select.where(Predicates.equals(PSysConfig.SPEC, spec));
    return jdbcTemplate.query(select.build(), MAPPER);
  }

  public List<SysConfig> list(String tenant, String spec, String cfKey) {
    Assert.notNull(tenant, "租户");
    SelectBuilder select = new SelectBuilder().from(PSysConfig.TABLE_NAME);
    select.where(Predicates.equals(PSysConfig.TENANT, tenant));
    select.where(Predicates.equals(PSysConfig.SPEC, spec));
    select.where(Predicates.equals(PSysConfig.CF_KYE, cfKey));
    return jdbcTemplate.query(select.build(), MAPPER);
  }

  public List<SysConfig> listByKey(String tenant, String cfKey) {
    Assert.notNull(tenant, "租户");
    SelectBuilder select = new SelectBuilder().from(PSysConfig.TABLE_NAME);

    select.where(Predicates.equals(PSysConfig.TENANT, tenant));
    select.where(Predicates.equals(PSysConfig.CF_KYE, cfKey));
    return jdbcTemplate.query(select.build(), MAPPER);
  }

}
