package com.hd123.baas.sop.service.dao.price.gradeadjustment;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.gradeadjustment.PriceGradeAdjustmentShop;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/16.
 */
public class PriceGradeAdjustmentShopMapper extends PEntity.RowMapper<PriceGradeAdjustmentShop> {
  @Override
  public PriceGradeAdjustmentShop mapRow(ResultSet rs, int rowNum) throws SQLException {
    PriceGradeAdjustmentShop entity = new PriceGradeAdjustmentShop();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PPriceGradeAdjustmentShop.TENANT));
    entity.setOwner(rs.getString(PPriceGradeAdjustmentShop.OWNER));
    entity.setShop(rs.getString(PPriceGradeAdjustmentShop.SHOP));
    entity.setShopCode(rs.getString(PPriceGradeAdjustmentShop.SHOP_CODE));
    entity.setShopName(rs.getString(PPriceGradeAdjustmentShop.SHOP_NAME));
    return entity;
  }
}
