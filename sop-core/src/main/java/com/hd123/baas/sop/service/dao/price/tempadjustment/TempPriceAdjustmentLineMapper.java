package com.hd123.baas.sop.service.dao.price.tempadjustment;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempPriceAdjustmentLine;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @Author maodapeng
 * @Since
 */
public class TempPriceAdjustmentLineMapper extends PEntity.RowMapper<TempPriceAdjustmentLine> {
  @Override
  public TempPriceAdjustmentLine mapRow(ResultSet rs, int i) throws SQLException {
    TempPriceAdjustmentLine line = new TempPriceAdjustmentLine();
    super.mapFields(rs, i, line);
    line.setOwner(rs.getString(PTempPriceAdjustmentLine.OWNER));
    line.setShop(rs.getString(PTempPriceAdjustmentLine.SHOP));
    line.setShopName(rs.getString(PTempPriceAdjustmentLine.SHOP_NAME));
    line.setShopCode(rs.getString(PTempPriceAdjustmentLine.SHOP_CODE));

    line.setSkuId(rs.getString(PTempPriceAdjustmentLine.SKU_ID));
    line.setSkuGid(rs.getString(PTempPriceAdjustmentLine.SKU_GID));
    line.setSkuCode(rs.getString(PTempPriceAdjustmentLine.SKU_CODE));
    line.setSkuName(rs.getString(PTempPriceAdjustmentLine.SKU_NAME));
    line.setSkuQpc(rs.getBigDecimal(PTempPriceAdjustmentLine.SKU_QPC));

    line.setSalePrice(rs.getBigDecimal(PTempPriceAdjustmentLine.SALE_PRICE));
    return line;
  }
}
