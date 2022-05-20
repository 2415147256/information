package com.hd123.baas.sop.service.dao.option;


import com.hd123.baas.sop.service.api.option.Option;
import com.hd123.baas.sop.service.api.option.OptionType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OptionMapper extends POption.RowMapper<Option> {
  @Override
  public Option mapRow(ResultSet rs, int i) throws SQLException {
    Option target = new Option();
    mapFields(rs, i, target);
    target.setTenant(rs.getString(POption.TENANT));
    target.setOpKey(rs.getString(POption.OP_KEY));
    target.setType(OptionType.valueOf(rs.getString(POption.TYPE)));
    target.setOpValue(rs.getString(POption.OP_VALUE));

    return target;
  }
}
