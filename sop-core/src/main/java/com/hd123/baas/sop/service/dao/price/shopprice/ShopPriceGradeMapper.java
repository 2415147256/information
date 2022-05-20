package com.hd123.baas.sop.service.dao.price.shopprice;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceGrade;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/16.
 */
public class ShopPriceGradeMapper extends PEntity.RowMapper<ShopPriceGrade> {
  @Override
  public ShopPriceGrade mapRow(ResultSet rs, int rowNum) throws SQLException {
    ShopPriceGrade entity = new ShopPriceGrade();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PShopPriceGrade.TENANT));
    entity.setOrgId(rs.getString(PShopPriceGrade.ORG_ID));
    entity.setShop(rs.getString(PShopPriceGrade.SHOP));
    entity.setSkuGroup(rs.getString(PShopPriceGrade.SKU_GROUP));
    entity.setSkuPosition(rs.getString(PShopPriceGrade.SKU_POSITION));
    entity.setPriceGrade(rs.getString(PShopPriceGrade.PRICE_GRADE));
    entity.setSourceCreateTime(rs.getTimestamp(PShopPriceGrade.SOURCE_CREATE_TIME));
    entity.setSource(rs.getString(PShopPriceGrade.SOURCE));
    return entity;
  }
}
