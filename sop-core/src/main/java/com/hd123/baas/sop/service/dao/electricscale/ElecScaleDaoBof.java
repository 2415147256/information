package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.electricscale.ElecScale;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElecScaleDaoBof extends BofBaseDao {
  private static final ElecScaleMapper ELEC_SCALE_MAPPER = new ElecScaleMapper();

  public List<ElecScale> query(String tenant) {
    Assert.notNull(tenant, "tenent");
    SelectBuilder select = new SelectBuilder().from(PElecScale.TABLE_NAME)
        .where(Predicates.equals(PElecScale.TENANT, tenant));
    return jdbcTemplate.query(select.build(), ELEC_SCALE_MAPPER);
  }

  public ElecScale get(String tenant, String uuid) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    SelectBuilder select = new SelectBuilder().from(PElecScale.TABLE_NAME)
        .where(Predicates.equals(PElecScale.TENANT, tenant))
        .where(Predicates.equals(PElecScale.UUID, uuid));
    List<ElecScale> query = jdbcTemplate.query(select.build(), ELEC_SCALE_MAPPER);
    if (CollectionUtils.isNotEmpty(query)) {
      return query.get(0);
    }
    return null;
  }
}
