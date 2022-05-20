package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.baas.sop.service.api.electricscale.ElecScaleKeyboard;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ElectronicScaleKeyboardMapper extends PEntity.RowMapper<ElecScaleKeyboard> {
  @Override
  public ElecScaleKeyboard mapRow(ResultSet rs, int rowNum) throws SQLException {
    ElecScaleKeyboard result = new ElecScaleKeyboard();
    super.mapFields(rs, rowNum, result);
    result.setTenant(rs.getString(PElectronicScaleKeyboard.TENANT));
    result.setUuid(rs.getString(PElectronicScaleKeyboard.UUID));
    return result;
  }
}
