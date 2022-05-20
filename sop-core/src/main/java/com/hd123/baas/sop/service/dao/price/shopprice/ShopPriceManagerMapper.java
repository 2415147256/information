package com.hd123.baas.sop.service.dao.price.shopprice;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceManager;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/19.
 */
public class ShopPriceManagerMapper extends PEntity.RowMapper<ShopPriceManager> {
  @Override
  public ShopPriceManager mapRow(ResultSet rs, int rowNum) throws SQLException {
    ShopPriceManager entity = new ShopPriceManager();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PShopPriceManager.TENANT));
    entity.setOrgId(rs.getString(PShopPriceManager.ORG_ID));
    entity.setShop(rs.getString(PShopPriceManager.SHOP));
    entity.setShopCode(rs.getString(PShopPriceManager.SHOP_CODE));
    entity.setShopName(rs.getString(PShopPriceManager.SHOP_NAME));
    entity.setEffectiveDate(rs.getTimestamp(PShopPriceManager.EFFECTIVE_DATE));
    entity.setEffectiveEndDate(rs.getTimestamp(PShopPriceManager.EFFECTIVE_END_DATE));

    PriceSku sku = new PriceSku();
    sku.setId(rs.getString(PShopPriceManager.SKU_ID));
    sku.setCode(rs.getString(PShopPriceManager.SKU_CODE));
    sku.setName(rs.getString(PShopPriceManager.SKU_NAME));
    sku.setGoodsGid(rs.getString(PShopPriceManager.SKU_GID));
    sku.setQpc(rs.getBigDecimal(PShopPriceManager.SKU_QPC));
    entity.setSku(sku);

    entity.setInPrice(rs.getBigDecimal(PShopPriceManager.IN_PRICE));
    entity.setBasePrice(rs.getBigDecimal(PShopPriceManager.BASE_PRICE));
    entity.setPromotionSource(rs.getString(PShopPriceManager.PROMOTION_SOURCE));
    entity.setShopPrice(rs.getBigDecimal(PShopPriceManager.SHOP_PRICE));
    entity.setSalePrice(rs.getBigDecimal(PShopPriceManager.SALE_PRICE));
    entity.setChanged(rs.getInt(PShopPriceManager.CHANGED));
    return entity;
  }
}
