package com.hd123.baas.sop.service.dao.electricscale;

import java.util.List;

import com.hd123.baas.sop.service.api.electricscale.ElecScaleStateType;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleState;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

@Service
public class ShopElecScaleStateDaoBof extends BofBaseDao {
  private static final ShopElectricScaleStateMapper ELECTRIC_SCALE_STATE_MAPPER = new ShopElectricScaleStateMapper();

  public List<ElecScaleState> queryLastDataState(String tenant, List<String> electricScaleStateUUIDs) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(electricScaleStateUUIDs);
    SelectBuilder select = new SelectBuilder().from(new SelectBuilder().from(PShopElectricScaleState.TABLE_NAME)
        .where(Predicates.equals(PShopElectricScaleState.TENANT, tenant))
        .where(Predicates.equals(PShopElectricScaleState.TYPE, ElecScaleStateType.DATA.name()))
        .where(Predicates.in2(PShopElectricScaleState.ELECTRONIC_SCALE, electricScaleStateUUIDs.toArray()))
        .orderBy(PShopElectricScaleState.CREATE_TIME, false)
        .build(), "a").groupBy(PShopElectricScaleState.ELECTRONIC_SCALE);

    return jdbcTemplate.query(select.build(), ELECTRIC_SCALE_STATE_MAPPER);
  }

  public List<ElecScaleState> listByShopCode(String tenant, String shopCode) {

    Assert.notNull(tenant, "租户");
    Assert.notNull(shopCode, "门店代码");
    SelectBuilder select = new SelectBuilder().select("b.*")
        .from(new SelectBuilder().select("a.*")
            .from(new SelectBuilder().from(PShopElectricScaleState.TABLE_NAME, PShopElectricScaleState.TABLE_AlIAS)
                .orderBy(PShopElectricScaleState.TABLE_AlIAS, PShopElectricScaleState.ELECTRONIC_SCALE)
                .orderBy(PShopElectricScaleState.TABLE_AlIAS, PShopElectricScaleState.CREATE_TIME, false)
                .build(), "a")
            .build(), "b")
        .leftJoin(PShopElecScale.TABLE_NAME, PShopElecScale.TABLE_AlIAS,
            Predicates.equals("b", PShopElectricScaleState.ELECTRONIC_SCALE, PShopElecScale.TABLE_AlIAS,
                PShopElecScale.UUID))
        .where(Predicates.equals("b", PShopElectricScaleState.TENANT, tenant))
        .where(Predicates.equals(PShopElecScale.TABLE_AlIAS, PShopElecScale.SHOP_CODE, shopCode));
    return jdbcTemplate.query(select.build(), ELECTRIC_SCALE_STATE_MAPPER);
  }

  public void insert(String tenant, ElecScaleState states) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(states, "states");
    InsertBuilder insert = new InsertBuilder().table(PShopElectricScaleState.TABLE_NAME)
        .addValue(PShopElectricScaleState.TENANT, tenant)
        .addValue(PShopElectricScaleState.UUID, states.getUuid())
        .addValue(PShopElectricScaleState.CREATE_TIME, states.getCreateTime())
        .addValue(PShopElectricScaleState.STATE, states.getState())
        .addValue(PShopElectricScaleState.TYPE, states.getType().name())
        .addValue(PShopElectricScaleState.ELECTRONIC_SCALE, states.getElectronicScaleUuid())
        .addValue(PShopElectricScaleState.REMARK, states.getRemark());
    jdbcTemplate.update(insert.build());
  }

  public List<ElecScaleState> queryLastTemState(String tenant, List<String> uuids) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(uuids);
    SelectBuilder select = new SelectBuilder().from(new SelectBuilder().from(PShopElectricScaleState.TABLE_NAME)
            .where(Predicates.equals(PShopElectricScaleState.TENANT, tenant))
            .where(Predicates.equals(PShopElectricScaleState.TYPE, ElecScaleStateType.TEMPLATE.name()))
            .where(Predicates.in2(PShopElectricScaleState.ELECTRONIC_SCALE, uuids.toArray()))
            .orderBy(PShopElectricScaleState.CREATE_TIME, false)
            .build(), "a").groupBy(PShopElectricScaleState.ELECTRONIC_SCALE);

    return jdbcTemplate.query(select.build(), ELECTRIC_SCALE_STATE_MAPPER);
  }
}
