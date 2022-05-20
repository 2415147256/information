package com.hd123.baas.sop.service.dao.price.shopprice;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionLineType;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPricePromotionManager;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/19.
 */
public class ShopPricePromotionManagerMapper extends PEntity.RowMapper<ShopPricePromotionManager> {
  @Override
  public ShopPricePromotionManager mapRow(ResultSet rs, int rowNum) throws SQLException {
    ShopPricePromotionManager entity = new ShopPricePromotionManager();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PShopPricePromotionManager.TENANT));
    entity.setOrgId(rs.getString(PShopPricePromotionManager.ORG_ID));
    entity.setShop(rs.getString(PShopPricePromotionManager.SHOP));

    entity.setEffectiveStartDate(rs.getTimestamp(PShopPricePromotionManager.EFFECTIVE_START_DATE));
    entity.setEffectiveEndDate(rs.getTimestamp(PShopPricePromotionManager.EFFECTIVE_END_DATE));

    PriceSku sku = new PriceSku();
    sku.setId(rs.getString(PShopPricePromotionManager.SKU_ID));
    entity.setSku(sku);
    entity.setType(PricePromotionLineType.valueOf(rs.getString(PShopPricePromotionManager.TYPE)));
    entity.setRule(rs.getString(PShopPricePromotionManager.RULE));
    entity.setSource(rs.getString(PShopPricePromotionManager.SOURCE));
    entity.setPricePromotionType(rs.getString(PShopPricePromotionManager.PRICE_PROMOTION_TYPE));
    entity.setSourceLastModified(rs.getTimestamp(PShopPricePromotionManager.SOURCE_LAST_MODIFIED));
    return entity;
  }
}
