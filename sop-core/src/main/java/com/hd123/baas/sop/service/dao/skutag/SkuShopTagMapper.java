package com.hd123.baas.sop.service.dao.skutag;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.skutag.ShopTag;
import com.hd123.baas.sop.service.api.skutag.SkuShopTag;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @Author maodapeng
 * @Since
 */
public class SkuShopTagMapper extends PStandardEntity.RowMapper<SkuShopTag> {
  @Override
  public SkuShopTag mapRow(ResultSet rs, int rowNum) throws SQLException {
    SkuShopTag entity = new SkuShopTag();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(ShopTag.Schema.TENANT));
    entity.setOrgId(rs.getString(ShopTag.Schema.ORG_ID));
    entity.setSkuId(rs.getString(ShopTag.Schema.SKU_ID));
    entity.setShop(rs.getString(ShopTag.Schema.SHOP));
    entity.setShopName(rs.getString(ShopTag.Schema.SHOP_NAME));
    entity.setShopCode(rs.getString(ShopTag.Schema.SHOP_CODE));
    return entity;
  }
}
