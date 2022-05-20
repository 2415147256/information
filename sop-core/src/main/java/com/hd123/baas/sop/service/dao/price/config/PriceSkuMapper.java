package com.hd123.baas.sop.service.dao.price.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.basedata.category.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.price.PriceSku;

/**
 * @author zhengzewang on 2020/11/11.
 */
public class PriceSkuMapper implements RowMapper<PriceSku> {
  @Override
  public PriceSku mapRow(ResultSet rs, int rowNum) throws SQLException {
    PriceSku sku = new PriceSku();
    sku.setTenant(rs.getString(PPriceSku.TENANT));
    sku.setOrgId(rs.getString(PPriceSku.ORG_ID));
    sku.setId(rs.getString(PPriceSku.ID));
    sku.setCode(rs.getString(PPriceSku.CODE));
    sku.setName(rs.getString(PPriceSku.NAME));
    sku.setQpc(rs.getBigDecimal(PPriceSku.QPC));
    sku.setUnit(rs.getString(PPriceSku.UNIT));
    sku.setGoodsGid(rs.getString(PPriceSku.GOODS_GID));
    String categoryId = rs.getString(PPriceSku.CATEGORY_ID);
    if (StringUtils.isNotBlank(categoryId)){
      Category category = new Category();
      category.setId(categoryId);
      sku.setCategory(category);
    }
    return sku;
  }
}
