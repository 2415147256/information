package com.hd123.baas.sop.service.dao.price.shopprice;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionLineType;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPricePromotion;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/19.
 */
public class ShopPricePromotionMapper extends PEntity.RowMapper<ShopPricePromotion> {
  @Override
  public ShopPricePromotion mapRow(ResultSet rs, int rowNum) throws SQLException {
    ShopPricePromotion entity = new ShopPricePromotion();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PShopPricePromotion.TENANT));
    entity.setOrgId(rs.getString(PShopPricePromotion.ORG_ID));
    entity.setShop(rs.getString(PShopPricePromotion.SHOP));
    entity.setEffectiveEndDate(rs.getTimestamp(PShopPricePromotion.EFFECTIVE_END_DATE));
    PriceSku sku = new PriceSku();
    sku.setId(rs.getString(PShopPricePromotion.SKU_ID));
    entity.setSku(sku);
    entity.setType(PricePromotionLineType.valueOf(rs.getString(PShopPricePromotion.TYPE)));
    entity.setRule(rs.getString(PShopPricePromotion.RULE));
    entity.setSource(rs.getString(PShopPricePromotion.SOURCE));
    entity.setSourceLastModified(rs.getTimestamp(PShopPricePromotion.SOURCE_LAST_MODIFIED));
    entity.setPricePromotionType(rs.getString(PShopPricePromotion.PRICE_PROMOTION_TYPE));
    return entity;
  }
}
