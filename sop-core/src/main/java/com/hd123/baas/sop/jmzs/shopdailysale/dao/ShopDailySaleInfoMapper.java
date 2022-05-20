package com.hd123.baas.sop.jmzs.shopdailysale.dao;

import com.hd123.baas.sop.jmzs.shopdailysale.api.ShopDailySaleInfo;
import com.hd123.baas.sop.jmzs.shopdailysale.api.ShopDailySaleInfoHolder;
import com.hd123.baas.sop.jmzs.shopdailysale.api.ShopDailySaleInfoLine;
import com.hd123.baas.sop.jmzs.shopdailysale.api.ShopDailySaleInfoState;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.baas.sop.utils.SopUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShopDailySaleInfoMapper extends PShopDailySaleInfo.RowMapper<ShopDailySaleInfo> {

  @Override
  public ShopDailySaleInfo mapRow(ResultSet rs, int i) throws SQLException {
    ShopDailySaleInfo target = new ShopDailySaleInfo();
    mapFields(rs, i, target);
    target.setTenant(rs.getString(PShopDailySaleInfo.TENANT));
    target.setOrgId(rs.getString(PShopDailySaleInfo.ORG_ID));
    target.setCode(rs.getInt(PShopDailySaleInfo.CODE));
    target.setAmount(rs.getBigDecimal(PShopDailySaleInfo.AMOUNT));
    target.setDailySaleDate(rs.getTimestamp(PShopDailySaleInfo.DAILY_SALE_DATE));
    target.setShopId(rs.getString(PShopDailySaleInfo.SHOP_ID));
    target.setShopCode(rs.getString(PShopDailySaleInfo.SHOP_CODE));
    target.setShopName(rs.getString(PShopDailySaleInfo.SHOP_NAME));
    target.setHolder(ShopDailySaleInfoHolder.valueOf(rs.getString(PShopDailySaleInfo.HOLDER)));
    target.setHolderId(rs.getString(PShopDailySaleInfo.HOLDER_ID));
    target.setHolderCode(rs.getString(PShopDailySaleInfo.HOLDER_CODE));
    target.setHolderName(rs.getString(PShopDailySaleInfo.HOLDER_NAME));
    target.setState(ShopDailySaleInfoState.valueOf(rs.getString(PShopDailySaleInfo.STATE)));
    target.setLines(JsonUtil.jsonToList(SopUtils.convert(rs.getBlob(PShopDailySaleInfo.LINE)), ShopDailySaleInfoLine.class));

    return target;

  }
}
