package com.hd123.baas.sop.service.dao.price.priceadjustment;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustment;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustmentState;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/11.
 */
public class PriceAdjustmentMapper extends PStandardEntity.RowMapper<PriceAdjustment> {
  @Override
  public PriceAdjustment mapRow(ResultSet rs, int rowNum) throws SQLException {
    PriceAdjustment entity = new PriceAdjustment();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PPriceAdjustment.TENANT));
    entity.setOrgId(rs.getString(PPriceAdjustment.ORG_ID));
    entity.setFlowNo(rs.getString(PPriceAdjustment.FLOW_NO));
    entity.setEffectiveStartDate(rs.getTimestamp(PPriceAdjustment.EFFECTIVE_START_DATE));
    entity.setState(PriceAdjustmentState.valueOf(rs.getString(PPriceAdjustment.STATE)));
    return entity;
  }
}
