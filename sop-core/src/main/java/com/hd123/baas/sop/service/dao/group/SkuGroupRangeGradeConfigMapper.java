package com.hd123.baas.sop.service.dao.group;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.entity.SkuGroupRangeGradeConfig;

public class SkuGroupRangeGradeConfigMapper implements RowMapper<SkuGroupRangeGradeConfig> {
  @Override
  public SkuGroupRangeGradeConfig mapRow(ResultSet rs, int i) throws SQLException {
    SkuGroupRangeGradeConfig config = new SkuGroupRangeGradeConfig();
    config.setUuid(rs.getInt(PSkuGroupRangeGradeConfig.UUID));
    config.setTenant(rs.getString(PSkuGroupRangeGradeConfig.TENANT));
    config.setSkuGroupId(rs.getInt(PSkuGroupRangeGradeConfig.SKU_GROUP_ID));
    config.setPriceRangeId(rs.getInt(PSkuGroupRangeGradeConfig.PRICE_RANGE_ID));
    config.setPriceGradeJson(rs.getString(PSkuGroupRangeGradeConfig.PRICE_GRADE_JSON));
    return config;
  }
}
