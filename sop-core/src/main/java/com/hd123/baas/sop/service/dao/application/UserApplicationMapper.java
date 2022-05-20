package com.hd123.baas.sop.service.dao.application;

import com.hd123.baas.sop.service.api.appmanage.UserModule;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserApplicationMapper extends PEntity.RowMapper<UserModule> {
  @Override
  public UserModule mapRow(ResultSet rs, int rowNum) throws SQLException {
    UserModule result = new UserModule();
    super.mapFields(rs,rowNum,result);
    result.setSort(rs.getInt(PUserApplication.SORT));
    result.setTenant(rs.getString(PUserApplication.TENANT));
    result.setUserId(rs.getString(PUserApplication.USER_ID));
    result.setApplication(rs.getString(PUserApplication.MODULE));
    result.setCreated(rs.getTimestamp(PUserApplication.CREATED));
    result.setCreatorId(rs.getString(PUserApplication.CREATOR_ID));
    result.setCreatorNS(rs.getString(PUserApplication.CREATOR_NS));
    result.setCreatorName(rs.getString(PUserApplication.CREATOR_NAME));
    result.setUuid(rs.getString(PUserApplication.UUID));
    return result;
  }
}
