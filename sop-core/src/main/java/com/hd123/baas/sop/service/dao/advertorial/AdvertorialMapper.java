package com.hd123.baas.sop.service.dao.advertorial;

import com.hd123.baas.sop.service.api.advertorial.Advertorial;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdvertorialMapper extends PStandardEntity.RowMapper<Advertorial> {
  @Override
  public Advertorial mapRow(ResultSet rs, int rowNum) throws SQLException {
    Advertorial entity = new Advertorial();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PAdvertorial.TENANT));
    entity.setUuid(rs.getString(PAdvertorial.UUID));
    entity.setTitle(rs.getString(PAdvertorial.TITLE));
    entity.setContent(rs.getString(PAdvertorial.CONTENT));
    entity.setThUri(rs.getString(PAdvertorial.TH_URI));
    return entity;
  }

}
