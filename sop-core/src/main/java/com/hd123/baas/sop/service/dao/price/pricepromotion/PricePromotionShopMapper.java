package com.hd123.baas.sop.service.dao.price.pricepromotion;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionShop;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/13.
 */
public class PricePromotionShopMapper extends PEntity.RowMapper<PricePromotionShop> {
  @Override
  public PricePromotionShop mapRow(ResultSet rs, int rowNum) throws SQLException {
    PricePromotionShop entity = new PricePromotionShop();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PPricePromotionShop.TENANT));
    entity.setOwner(rs.getString(PPricePromotionShop.OWNER));
    entity.setShop(rs.getString(PPricePromotionShop.SHOP));
    entity.setShopCode(rs.getString(PPricePromotionShop.SHOP_CODE));
    entity.setShopName(rs.getString(PPricePromotionShop.SHOP_NAME));
    return entity;
  }
}
