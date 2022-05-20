package com.hd123.baas.sop.service.dao.price.gradeadjustment;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.gradeadjustment.PriceGradeAdjustment;
import com.hd123.baas.sop.service.api.price.gradeadjustment.PriceGradeAdjustmentState;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/12.
 */
public class PriceGradeAdjustmentMapper extends PStandardEntity.RowMapper<PriceGradeAdjustment> {
  @Override
  public PriceGradeAdjustment mapRow(ResultSet rs, int rowNum) throws SQLException {
    PriceGradeAdjustment entity = new PriceGradeAdjustment();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PPriceGradeAdjustment.TENANT));
    entity.setOrgId(rs.getString(PPriceGradeAdjustment.ORG_ID));
    entity.setFlowNo(rs.getString(PPriceGradeAdjustment.FLOW_NO));
    entity.setReason(rs.getString(PPriceGradeAdjustment.REASON));
    entity.setState(PriceGradeAdjustmentState.valueOf(rs.getString(PPriceGradeAdjustment.STATE)));
    entity.setEffectiveStartDate(rs.getDate(PPriceGradeAdjustment.EFFECTIVE_START_DATE));
    return entity;
  }
}
