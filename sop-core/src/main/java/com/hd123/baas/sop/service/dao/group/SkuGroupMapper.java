package com.hd123.baas.sop.service.dao.group;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.entity.SkuGroup;

public class SkuGroupMapper implements RowMapper<SkuGroup> {
  @Override
  public SkuGroup mapRow(ResultSet rs, int i) throws SQLException {
    SkuGroup skuGroup = new SkuGroup();
    skuGroup.setUuid(rs.getInt(PSkuGroup.UUID));
    skuGroup.setOrgId(rs.getString(PSkuGroup.ORG_ID));
    skuGroup.setName(rs.getString(PSkuGroup.NAME));
    skuGroup.setTenant(rs.getString(PSkuGroup.TENANT));
    skuGroup.setToleranceValue(rs.getBigDecimal(PSkuGroup.TOLERANCE_VALUE));
    return skuGroup;
  }
}
