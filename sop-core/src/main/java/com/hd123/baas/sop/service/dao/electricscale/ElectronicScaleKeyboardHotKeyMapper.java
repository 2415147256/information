package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.baas.sop.service.api.electricscale.ElecScaleKeyboardHotKey;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ElectronicScaleKeyboardHotKeyMapper extends PEntity.RowMapper<ElecScaleKeyboardHotKey> {
  @Override
  public ElecScaleKeyboardHotKey mapRow(ResultSet rs, int rowNum) throws SQLException {
    ElecScaleKeyboardHotKey result = new ElecScaleKeyboardHotKey();
    super.mapFields(rs, rowNum, result);
    result.setTenant(rs.getString(PElectronicScaleKeyboardHotKey.TENANT));
    result.setParam(rs.getString(PElectronicScaleKeyboardHotKey.PARAM));
    result.setOwner(rs.getString(PElectronicScaleKeyboardHotKey.OWNER));
    result.setHotKey(rs.getString(PElectronicScaleKeyboardHotKey.HOT_KEY));
    return result;
  }
}
