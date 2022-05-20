package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.baas.sop.service.api.electricscale.ShopElecScale;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShopElectricScaleMapper extends PStandardEntity.RowMapper<ShopElecScale> {
  @Override
  public ShopElecScale mapRow(ResultSet rs, int rowNum) throws SQLException {
    ShopElecScale result = new ShopElecScale();
    super.mapFields(rs, rowNum, result);
    result.setTenant(rs.getString(PShopElecScale.TENANT));
    result.setIp(rs.getString(PShopElecScale.IP));
    result.setModel(rs.getString(PShopElecScale.MODEL));
    result.setName(rs.getString(PShopElecScale.NAME));
    result.setShopCode(rs.getString(PShopElecScale.SHOP_CODE));
    result.setShopName(rs.getString(PShopElecScale.SHOP_NAME));
    result.setOrgId(rs.getString(PShopElecScale.ORG_ID));
    return result;
  }
}
