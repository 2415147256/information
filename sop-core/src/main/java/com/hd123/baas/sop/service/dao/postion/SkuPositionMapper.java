package com.hd123.baas.sop.service.dao.postion;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.entity.SkuPosition;

public class SkuPositionMapper implements RowMapper<SkuPosition> {
  @Override
  public SkuPosition mapRow(ResultSet rs, int i) throws SQLException {
    SkuPosition skuPosition = new SkuPosition();
    skuPosition.setUuid(rs.getInt(PSkuPosition.UUID));
    skuPosition.setOrgId(rs.getString(PSkuPosition.ORG_ID));
    skuPosition.setName(rs.getString(PSkuPosition.NAME));
    skuPosition.setTenant(rs.getString(PSkuPosition.TENANT));
    return skuPosition;
  }
}
