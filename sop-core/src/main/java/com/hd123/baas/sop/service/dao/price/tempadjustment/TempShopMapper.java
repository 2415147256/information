package com.hd123.baas.sop.service.dao.price.tempadjustment;

import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempShop;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author maodapeng
 * @Since
 */
public class TempShopMapper extends PEntity.RowMapper<TempShop> {
  @Override
  public TempShop mapRow(ResultSet rs, int i) throws SQLException {
    TempShop tempShop = new TempShop();
    tempShop.setShop(rs.getString(PTempPriceAdjustmentLine.SHOP));
    tempShop.setCode(rs.getString(PTempPriceAdjustmentLine.SHOP_CODE));
    tempShop.setName(rs.getString(PTempPriceAdjustmentLine.SHOP_NAME));
    return tempShop;
  }
}
