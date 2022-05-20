package com.hd123.baas.sop.service.dao.price.gradeadjustment;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.gradeadjustment.PriceGradeAdjustmentLine;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/12.
 */
public class PriceGradeAdjustmentLineMapper extends PEntity.RowMapper<PriceGradeAdjustmentLine> {
  @Override
  public PriceGradeAdjustmentLine mapRow(ResultSet rs, int rowNum) throws SQLException {
    PriceGradeAdjustmentLine entity = new PriceGradeAdjustmentLine();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PPriceGradeAdjustmentLine.TENANT));
    entity.setOwner(rs.getString(PPriceGradeAdjustmentLine.OWNER));
    entity.setSkuGroup(rs.getString(PPriceGradeAdjustmentLine.SKU_GROUP));
    entity.setSkuGroupName(rs.getString(PPriceGradeAdjustmentLine.SKU_GROUP_NAME));
    entity.setSkuPosition(rs.getString(PPriceGradeAdjustmentLine.SKU_POSITION));
    entity.setSkuPositionName(rs.getString(PPriceGradeAdjustmentLine.SKU_POSITION_NAME));
    entity.setPriceGrade(rs.getString(PPriceGradeAdjustmentLine.PRICE_GRADE));
    entity.setPriceGradeName(rs.getString(PPriceGradeAdjustmentLine.PRICE_GRADE_NAME));
    return entity;
  }
}
