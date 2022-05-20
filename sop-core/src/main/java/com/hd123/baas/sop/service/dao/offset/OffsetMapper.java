package com.hd123.baas.sop.service.dao.offset;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.offset.Offset;
import com.hd123.baas.sop.service.api.offset.OffsetType;

/**
 * @author zhengzewang on 2020/11/17.
 */
public class OffsetMapper implements RowMapper<Offset> {
  @Override
  public Offset mapRow(ResultSet rs, int rowNum) throws SQLException {
    Offset entity = new Offset();
    entity.setTenant(rs.getString(POffset.TENANT));
    entity.setType(OffsetType.valueOf(rs.getString(POffset.TYPE)));
    entity.setSpec(rs.getString(POffset.SPEC));
    entity.setSeq(rs.getLong(POffset.SEQ));
    return entity;
  }
}
