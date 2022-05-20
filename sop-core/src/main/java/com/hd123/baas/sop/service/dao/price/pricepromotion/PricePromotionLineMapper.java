package com.hd123.baas.sop.service.dao.price.pricepromotion;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionLine;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionLineType;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/13.
 */
public class PricePromotionLineMapper extends PEntity.RowMapper<PricePromotionLine> {
  @Override
  public PricePromotionLine mapRow(ResultSet rs, int rowNum) throws SQLException {
    PricePromotionLine entity = new PricePromotionLine();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PPricePromotionLine.TENANT));
    entity.setOwner(rs.getString(PPricePromotionLine.OWNER));
    entity.setType(PricePromotionLineType.valueOf(rs.getString(PPricePromotionLine.TYPE)));
    entity.setRule(rs.getString(PPricePromotionLine.RULE));

    PriceSku sku = new PriceSku();
    sku.setId(rs.getString(PPricePromotionLine.SKU_ID));
    sku.setCode(rs.getString(PPricePromotionLine.SKU_CODE));
    sku.setName(rs.getString(PPricePromotionLine.SKU_NAME));
    sku.setQpc(rs.getBigDecimal(PPricePromotionLine.SKU_QPC));
    sku.setUnit(rs.getString(PPricePromotionLine.SKU_UNIT));
    entity.setSku(sku);
    entity.setSkuGroup(rs.getString(PPricePromotionLine.SKU_GROUP));
    entity.setSkuGroupName(rs.getString(PPricePromotionLine.SKU_GROUP_NAME));

    return entity;
  }
}
