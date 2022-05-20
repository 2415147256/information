package com.hd123.baas.sop.service.dao.basedata;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.basedata.sku.DSku;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author lina
 */
public class SkuMapper extends PStandardEntity.RowMapper<DSku> {
  @Override
  public DSku mapRow(ResultSet rs, int rowNum) throws SQLException {
    DSku sku = new DSku();
    super.mapFields(rs, rowNum, sku);
    sku.setOrgType(rs.getString(PSku.ORG_TYPE));
    sku.setOrgId(rs.getString(PSku.ORG_ID));
    sku.setTenant(rs.getString(PSku.TENANT));
    sku.setGoodsGid(rs.getString(PSku.GOODS_GID));
    sku.setId(rs.getString(PSku.ID));
    sku.setCode(rs.getString(PSku.CODE));
    sku.setName(rs.getString(PSku.NAME));
    sku.setQpc(rs.getBigDecimal(PSku.QPC));
    sku.setUnit(rs.getString(PSku.UNIT));
    sku.setPrice(rs.getBigDecimal(PSku.PRICE));
    sku.setCategoryId(rs.getString(PSku.CATEGORY_ID));
    sku.setDeleted(rs.getBoolean(PSku.DELETED));
    sku.setPlu(rs.getString(PSku.PLU));
    sku.setRequired(rs.getBoolean(PSku.REQUIRED));
    sku.setInputCode(rs.getString(PSku.INPUT_CODE));
    sku.setH6GoodsType(rs.getString(PSku.H6_GOODS_TYPE));
    sku.setQpcDesc(rs.getString(PSku.QPC_DESC));
    sku.setPyCode(rs.getString(PSku.SKU_PY_CODE));
    sku.setDu(rs.getInt(PSku.SKU_DU));
    return sku;
  }
}
