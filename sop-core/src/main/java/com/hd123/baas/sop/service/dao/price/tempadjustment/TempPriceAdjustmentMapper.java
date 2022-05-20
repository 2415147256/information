package com.hd123.baas.sop.service.dao.price.tempadjustment;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempPriceAdjustment;
import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempPriceAdjustmentState;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @Author maodapeng
 * @Since
 */
public class TempPriceAdjustmentMapper extends PStandardEntity.RowMapper<TempPriceAdjustment> {
  @Override
  public TempPriceAdjustment mapRow(ResultSet rs, int i) throws SQLException {
    TempPriceAdjustment temp = new TempPriceAdjustment();
    super.mapFields(rs, i, temp);
    temp.setOrgId(rs.getString(PTempPriceAdjustment.ORG_ID));
    temp.setFlowNo(rs.getString(PTempPriceAdjustment.FLOW_NO));
    temp.setEffectiveStartDate(rs.getDate(PTempPriceAdjustment.EFFECTIVE_START_DATE));
    temp.setReason(rs.getString(PTempPriceAdjustment.REASON));
    String state = rs.getString(PTempPriceAdjustment.STATE);
    if (state != null) {
      temp.setState(TempPriceAdjustmentState.valueOf(state));
    }
    return temp;
  }
}
