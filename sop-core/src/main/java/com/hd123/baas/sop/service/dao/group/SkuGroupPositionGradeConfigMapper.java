package com.hd123.baas.sop.service.dao.group;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.entity.SkuGroupPositionGradeConfig;

public class SkuGroupPositionGradeConfigMapper implements RowMapper<SkuGroupPositionGradeConfig> {
  @Override
  public SkuGroupPositionGradeConfig mapRow(ResultSet rs, int i) throws SQLException {
    SkuGroupPositionGradeConfig config = new SkuGroupPositionGradeConfig();
    config.setUuid(rs.getInt(PSkuGroupPositionGradeConfig.UUID));
    config.setTenant(rs.getString(PSkuGroupPositionGradeConfig.TENANT));
    config.setSkuGroupId(rs.getInt(PSkuGroupPositionGradeConfig.SKU_GROUP_ID));
    config.setPricePositionId(rs.getInt(PSkuGroupPositionGradeConfig.PRICE_POSITION_ID));
    config.setPriceGradeJson(rs.getString(PSkuGroupRangeGradeConfig.PRICE_GRADE_JSON));
    return config;
  }
}
