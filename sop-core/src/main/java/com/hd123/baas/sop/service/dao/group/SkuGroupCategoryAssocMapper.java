package com.hd123.baas.sop.service.dao.group;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.entity.SkuGroupCategoryAssoc;

public class SkuGroupCategoryAssocMapper implements RowMapper<SkuGroupCategoryAssoc> {
  @Override
  public SkuGroupCategoryAssoc mapRow(ResultSet rs, int i) throws SQLException {
    SkuGroupCategoryAssoc assoc = new SkuGroupCategoryAssoc();
    assoc.setUuid(rs.getInt(PSkuGroupCategoryAssoc.UUID));
    assoc.setTenant(rs.getString(PSkuGroupCategoryAssoc.TENANT));
    assoc.setCategoryCode(rs.getString(PSkuGroupCategoryAssoc.CATEGORY_CODE));
    assoc.setCategoryId(rs.getString(PSkuGroupCategoryAssoc.CATEGORY_ID));
    assoc.setCategoryName(rs.getString(PSkuGroupCategoryAssoc.CATEGORY_NAME));
    assoc.setSkuGroupId(rs.getInt(PSkuGroupCategoryAssoc.SKU_GROUP_ID));
    return assoc;
  }
}
