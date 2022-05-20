package com.hd123.baas.sop.service.dao.price.shopprice;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceGradeManager;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/19.
 */
public class ShopPriceGradeManagerMapper extends PEntity.RowMapper<ShopPriceGradeManager> {
  @Override
  public ShopPriceGradeManager mapRow(ResultSet rs, int rowNum) throws SQLException {
    ShopPriceGradeManager entity = new ShopPriceGradeManager();
    super.mapFields(rs, rowNum, entity);
    entity.setEffectiveStartDate(rs.getTimestamp(PShopPriceGradeManager.EFFECTIVE_START_DATE));
    entity.setTenant(rs.getString(PShopPriceGradeManager.TENANT));
    entity.setOrgId(rs.getString(PShopPriceGradeManager.ORG_ID));
    entity.setShop(rs.getString(PShopPriceGradeManager.SHOP));
    entity.setSkuGroup(rs.getString(PShopPriceGradeManager.SKU_GROUP));
    entity.setSkuPosition(rs.getString(PShopPriceGradeManager.SKU_POSITION));
    entity.setPriceGrade(rs.getString(PShopPriceGradeManager.PRICE_GRADE));
    entity.setSource(rs.getString(PShopPriceGradeManager.SOURCE));
    entity.setSourceCreateTime(rs.getTimestamp(PShopPriceGradeManager.SOURCE_CREATE_TIME));
    return entity;
  }

}
