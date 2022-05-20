package com.hd123.baas.sop.service.dao.price.pricepromotion;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotion;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionState;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionType;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/13.
 */
public class PricePromotionMapper extends PStandardEntity.RowMapper<PricePromotion> {
  @Override
  public PricePromotion mapRow(ResultSet rs, int rowNum) throws SQLException {
    PricePromotion entity = new PricePromotion();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PPricePromotion.TENANT));
    entity.setOrgId(rs.getString(PPricePromotion.ORG_ID));
    entity.setFlowNo(rs.getString(PPricePromotion.FLOW_NO));
    entity.setEffectiveStartDate(rs.getDate(PPricePromotion.EFFECTIVE_START_DATE));
    entity.setEffectiveEndDate(rs.getDate(PPricePromotion.EFFECTIVE_END_DATE));
    entity.setState(PricePromotionState.valueOf(rs.getString(PPricePromotion.STATE)));
    entity.setAllShops(rs.getBoolean(PPricePromotion.ALL_SHOPS));
    entity.setReason(rs.getString(PPricePromotion.REASON));
    entity.setType(PricePromotionType.valueOf(rs.getString(PPricePromotion.TYPE)));
    entity.setPromotionTargets(rs.getString(PPricePromotion.PROMOTION_TARGETS));
    entity.setOrdLimitQty(rs.getBigDecimal(PPricePromotion.ORD_LIMIT_QTY));
    entity.setOrdLimitAmount(rs.getBigDecimal(PPricePromotion.ORD_LIMIT_AMOUNT));
    entity.setHeadSharingRate(rs.getBigDecimal(PPricePromotion.HEAD_SHARING_RATE));
    entity.setSupervisorSharingRate(rs.getBigDecimal(PPricePromotion.SUPERVISOR_SHARING_RATE));
    entity.setNote(rs.getString(PPricePromotion.NOTE));
    return entity;
  }
}
