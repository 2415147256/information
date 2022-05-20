package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.baas.sop.service.api.electricscale.ElecScale;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ElecScaleMapper extends PEntity.RowMapper<ElecScale> {
  @Override
  public ElecScale mapRow(ResultSet rs, int rowNum) throws SQLException {
    ElecScale result = new ElecScale();
    super.mapFields(rs, rowNum, result);
    result.setManufacturer(rs.getString(PElecScale.MANUFACTURER));
    result.setModel(rs.getString(PElecScale.MODEL));
    result.setName(rs.getString(PElecScale.NAME));
    result.setTenant(rs.getString(PElecScale.TENANT));
    result.setUuid(rs.getString(PElecScale.UUID));
    result.setLength(rs.getBigDecimal(PElecScale.LENGTH));
    result.setWidth(rs.getBigDecimal(PElecScale.WIDTH));
    return result;
  }
}
