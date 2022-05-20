package com.hd123.baas.sop.service.dao.range;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.entity.PriceRange;

public class PriceRangeMapper implements RowMapper<PriceRange> {
  @Override
  public PriceRange mapRow(ResultSet rs, int i) throws SQLException {
    PriceRange priceRange = new PriceRange();
    priceRange.setUuid(rs.getInt(PPriceRange.UUID));
    priceRange.setOrgId(rs.getString(PPriceRange.ORG_ID));
    priceRange.setName(rs.getString(PPriceRange.NAME));
    priceRange.setTenant(rs.getString(PPriceRange.TENANT));
    return priceRange;
  }
}
