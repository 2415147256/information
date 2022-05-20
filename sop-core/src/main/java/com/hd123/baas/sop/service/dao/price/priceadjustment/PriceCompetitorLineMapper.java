package com.hd123.baas.sop.service.dao.price.priceadjustment;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.priceadjustment.PriceCompetitorLine;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @Author: maodapeng
 * @Date: 2020/12/4 15:39
 */
public class PriceCompetitorLineMapper extends PEntity.RowMapper<PriceCompetitorLine> {
  @Override
  public PriceCompetitorLine mapRow(ResultSet rs, int i) throws SQLException {
    PriceCompetitorLine line = new PriceCompetitorLine();
    line.setUuid(rs.getString(PPriceCompetitorLine.UUID));
    line.setTenant(rs.getString(PPriceCompetitorLine.TENANT));
    line.setOwner(rs.getString(PPriceCompetitorLine.OWNER));
    line.setSkuId(rs.getString(PPriceCompetitorLine.SKU_ID));
    line.setSkuCode(rs.getString(PPriceCompetitorLine.SKU_CODE));
    line.setSalePrice(rs.getBigDecimal(PPriceCompetitorLine.SALE_PRICE));
    line.setQty(rs.getBigDecimal(PPriceCompetitorLine.QTY));
    line.setIgnore(rs.getBoolean("ignore"));
    return line;
  }
}
