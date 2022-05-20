package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.baas.sop.service.api.electricscale.ElecScaleTemplate;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ElecScaleTemplateMapper extends PStandardEntity.RowMapper<ElecScaleTemplate> {
  @Override
  public ElecScaleTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
    ElecScaleTemplate result = new ElecScaleTemplate();
    super.mapFields(rs, rowNum, result);
    result.setElectronicScale(rs.getString(PElecScaleTemplate.ELECTRONIC_SCALE));
    result.setElecScaleKeyBoard(rs.getString(PElecScaleTemplate.ELEC_SCALE_KEYBOARD));
    result.setName(rs.getString(PElecScaleTemplate.NAME));
    result.setTenant(rs.getString(PElecScaleTemplate.TENANT));
    result.setUuid(rs.getString(PElecScaleTemplate.UUID));
    result.setOrgId(rs.getString(PElecScaleTemplate.ORG_ID));
    return result;
  }
}
