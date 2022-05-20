package com.hd123.baas.sop.service.dao.postion;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.entity.SkuPositionAssoc;

public class SkuPositionAssocMapper implements RowMapper<SkuPositionAssoc> {
  @Override
  public SkuPositionAssoc mapRow(ResultSet rs, int i) throws SQLException {
    SkuPositionAssoc skuPositionAssoc = new SkuPositionAssoc();
    skuPositionAssoc.setUuid(rs.getInt(PSkuPositionAssoc.UUID));
    skuPositionAssoc.setSkuId(rs.getString(PSkuPositionAssoc.SKU_ID));
    skuPositionAssoc.setTenant(rs.getString(PSkuPositionAssoc.TENANT));
    skuPositionAssoc.setSkuPositionId(rs.getInt(PSkuPositionAssoc.SKU_POSITION_ID));
    return skuPositionAssoc;
  }
}
