package com.hd123.baas.sop.service.dao.electricscale;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.electricscale.ElecScaleState;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

public class ShopElectricScaleStateMapper extends PEntity.RowMapper<ElecScaleState> {
  @Override
  public ElecScaleState mapRow(ResultSet rs, int rowNum) throws SQLException {
    ElecScaleState state = new ElecScaleState();
    super.mapFields(rs, rowNum, state);
    state.setTenant(rs.getString(PShopElectricScaleState.TENANT));
    state.setCreateTime(rs.getTimestamp(PShopElectricScaleState.CREATE_TIME));
    state.setElectronicScaleUuid(rs.getString(PShopElectricScaleState.ELECTRONIC_SCALE));
    state.setState(rs.getString(PShopElectricScaleState.STATE));
    state.setRemark(rs.getString(PShopElectricScaleState.REMARK));
    return state;
  }
}
