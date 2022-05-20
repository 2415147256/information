package com.hd123.baas.sop.service.dao.price.pricepromotion;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.pricepromotion.ConflictPromotionLine;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

public class ConflictPromotionLineMapper extends PEntity.RowMapper<ConflictPromotionLine> {
  @Override
  public ConflictPromotionLine mapRow(ResultSet rs, int rowNum) throws SQLException {
    ConflictPromotionLine result = new ConflictPromotionLine();
    result.setShopName(rs.getString(PPricePromotionShop.SHOP_NAME));
    result.setPromotionFlowNo(rs.getString(PPricePromotion.FLOW_NO));
    result.setSkuCode(rs.getString(PPricePromotionLine.SKU_CODE));
    result.setSkuName(rs.getString(PPricePromotionLine.SKU_NAME));
    result.setSkuGroup(rs.getString(PPricePromotionLine.SKU_GROUP));
    result.setSkuGroupName(rs.getString(PPricePromotionLine.SKU_GROUP_NAME));
    result.setShopCode(rs.getString(PPricePromotionShop.SHOP_CODE));
    result.setEffectiveEndDate(rs.getTimestamp(PPricePromotion.EFFECTIVE_END_DATE));
    result.setEffectiveStartDate(rs.getTimestamp(PPricePromotion.EFFECTIVE_START_DATE));
    result.setCreated(rs.getTimestamp(PPricePromotion.CREATE_INFO_TIME));
    result.setCreatorId(rs.getString(PPricePromotion.CREATE_INFO_OPERATOR_ID));
    result.setCreatorName(rs.getString(PPricePromotion.CREATE_INFO_OPERATOR_FULL_NAME));
    result.setUuid(rs.getString(PPricePromotion.UUID));
    result.setRule(rs.getString(PPricePromotionLine.RULE));
    result.setType(rs.getString(PPricePromotionLine.TYPE));
    return result;
  }
}
