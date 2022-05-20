package com.hd123.baas.sop.service.dao.skutag;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.skutag.SkuTagSummary;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @Author maodapeng
 * @Since
 */
public class SkuTagSummaryMapper extends PEntity.RowMapper<SkuTagSummary> {
  @Override
  public SkuTagSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
    SkuTagSummary entity = new SkuTagSummary();
    super.mapFields(rs, rowNum, entity);
    entity.setUuid(rs.getString(SkuTagSummary.Schema.UUID));
    entity.setTenant(rs.getString(SkuTagSummary.Schema.TENANT));
    String orgId = rs.getString(SkuTagSummary.Schema.ORG_ID);
    entity.setOrgId(orgId);
    entity.setSkuId(rs.getString(SkuTagSummary.Schema.SKU_ID));
    entity.setShopNum(rs.getInt(SkuTagSummary.Schema.SHOP_NUM));
    return entity;
  }
}
