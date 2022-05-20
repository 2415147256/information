package com.hd123.baas.sop.service.dao.skutag;

import com.hd123.baas.sop.service.api.skutag.SkuTagSummary;
import com.hd123.baas.sop.common.OrgConstants;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author maodapeng
 * @Since
 */
public class SkuTagSummaryForQueryMapper extends PEntity.RowMapper<SkuTagSummary> {
  @Override
  public SkuTagSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
    SkuTagSummary entity = new SkuTagSummary();
    super.mapFields(rs, rowNum, entity);
    entity.setUuid(rs.getString(SkuTagSummary.Schema.UUID));
    entity.setTenant(rs.getString(SkuTagSummary.Schema.TENANT));
    String orgId = rs.getString(SkuTagSummary.Schema.ORG_ID);
    if (StringUtils.isBlank(orgId)) {
      orgId = OrgConstants.DEFAULT_MAS_ORG_ID;
    }
    entity.setOrgId(orgId);
    entity.setSkuId(rs.getString(SkuTagSummary.Schema.SKU_ID));
    entity.setSkuQpc(rs.getBigDecimal(SkuTagSummary.Schema.SKU_QPC));
    entity.setSkuCode(rs.getString(SkuTagSummary.Schema.SKU_CODE));
    entity.setSkuName(rs.getString(SkuTagSummary.Schema.SKU_NAME));
    entity.setShopNum(rs.getInt(SkuTagSummary.Schema.SHOP_NUM));
    return entity;
  }
}
