package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.baas.sop.service.api.electricscale.ShopElecScaleTemplate;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShopElecScaleTemplateMapper extends PEntity.RowMapper<ShopElecScaleTemplate> {
  @Override
  public ShopElecScaleTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
    ShopElecScaleTemplate template = new ShopElecScaleTemplate();
    super.mapFields(rs, rowNum, template);
    template.setIsAllShop(rs.getBoolean(PShopElecScaleTemplate.ISALLSHOP));
    template.setElecScaleTemplate(rs.getString(PShopElecScaleTemplate.ELECTRONIC_SCALE_TEMPLATE));
    template.setShop(rs.getString(PShopElecScaleTemplate.SHOP));
    template.setShopCode(rs.getString(PShopElecScaleTemplate.SHOPCODE));
    template.setShopName(rs.getString(PShopElecScaleTemplate.SHOPNAME));
    return template;
  }
}
