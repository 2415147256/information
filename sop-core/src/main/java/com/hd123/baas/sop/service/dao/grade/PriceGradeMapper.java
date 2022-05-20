package com.hd123.baas.sop.service.dao.grade;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.entity.PriceGrade;

public class PriceGradeMapper implements RowMapper<PriceGrade> {
  @Override
  public PriceGrade mapRow(ResultSet rs, int i) throws SQLException {
    PriceGrade priceGrade = new PriceGrade();
    priceGrade.setUuid(rs.getInt(PPriceGrade.UUID));
    priceGrade.setOrgId(rs.getString(PPriceGrade.ORG_ID));
    priceGrade.setName(rs.getString(PPriceGrade.NAME));
    priceGrade.setTenant(rs.getString(PPriceGrade.TENANT));
    priceGrade.setSeq(rs.getInt(PPriceGrade.SEQ));
    priceGrade.setDft(rs.getBoolean(PPriceGrade.DFT));
    return priceGrade;
  }
}
