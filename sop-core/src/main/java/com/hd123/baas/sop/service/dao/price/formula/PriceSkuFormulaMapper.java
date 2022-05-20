package com.hd123.baas.sop.service.dao.price.formula;

import com.hd123.baas.sop.service.api.formula.PriceSkuFormula;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author maodapeng
 * @Since
 */
public class PriceSkuFormulaMapper extends PEntity.RowMapper<PriceSkuFormula> {
  @Override
  public PriceSkuFormula mapRow(ResultSet rs, int rowNum) throws SQLException {
    PriceSkuFormula entity = new PriceSkuFormula();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PPriceSkuFormula.TENANT));
    entity.setOrgId(rs.getString(PPriceSkuFormula.ORG_ID));
    entity.setSkuId(rs.getString(PPriceSkuFormula.SKU_ID));
    entity.setSkuCode(rs.getString(PPriceSkuFormula.SKU_CODE));
    entity.setSkuName(rs.getString(PPriceSkuFormula.SKU_NAME));
    entity.setFormula(rs.getString(PPriceSkuFormula.FORMULA));
    entity.setFormulaDesc(rs.getString(PPriceSkuFormula.FORMULA_DESC));
    entity.setDependOnSkuId(rs.getString(PPriceSkuFormula.DEPEND_ON_SKU_ID));
    return entity;
  }
}
