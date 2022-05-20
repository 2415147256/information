package com.hd123.baas.sop.service.dao.price.shopprice;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceJob;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceJobState;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/23.
 */
public class ShopPriceJobMapper extends PStandardEntity.RowMapper<ShopPriceJob> {
  @Override
  public ShopPriceJob mapRow(ResultSet rs, int rowNum) throws SQLException {
    ShopPriceJob entity = new ShopPriceJob();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PShopPriceJob.TENANT));
    entity.setOrgId(rs.getString(PShopPriceJob.ORG_ID));
    entity.setShop(rs.getString(PShopPriceJob.SHOP));
    entity.setTaskId(rs.getString(PShopPriceJob.TASK_ID));
    entity.setExecuteDate(rs.getTimestamp(PShopPriceJob.EXECUTE_DATE));
    entity.setPriceAdjustment(rs.getString(PShopPriceJob.PRICE_ADJUSTMENT));
    entity.setState(ShopPriceJobState.valueOf(rs.getString(PShopPriceJob.STATE)));

    entity.setShopCode(rs.getString(PShopPriceJob.SHOP_CODE));
    entity.setShopName(rs.getString(PShopPriceJob.SHOP_NAME));
    entity.setErrMsg(SopUtils.convert(rs.getBlob(PShopPriceJob.ERR_MSG)));

    return entity;
  }
}
