package com.hd123.baas.sop.service.dao.application;

import com.hd123.baas.sop.service.api.appmanage.Module;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ApplicationMapper extends PEntity.RowMapper<Module> {

  @Override
  public Module mapRow(ResultSet rs, int rowNum) throws SQLException {
    Module result = new Module();
    super.mapFields(rs,rowNum,result);
    result.setGroupName(rs.getString(PApplication.GROUP_NAME));
    result.setName(rs.getString(PApplication.NAME));
    result.setTenant(rs.getString(PApplication.TENANT));
    result.setUuid(rs.getString(PApplication.UUID));
    return result;
  }
}
