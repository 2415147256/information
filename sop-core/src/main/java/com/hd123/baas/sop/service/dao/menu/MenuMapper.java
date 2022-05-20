package com.hd123.baas.sop.service.dao.menu;

import com.hd123.baas.sop.service.api.menu.Menu;
import com.hd123.baas.sop.service.api.menu.MenuType;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author W.J.H.7
 * @since 2022-01-23
 */
public class MenuMapper extends PStandardEntity.RowMapper<Menu> {
  @Override
  public Menu mapRow(ResultSet rs, int rowNum) throws SQLException {
    Menu target = new Menu();
    super.mapFields(rs, rowNum, target);

    target.setTenant(rs.getString(PMenu.TENANT));

    target.setCode(rs.getString(PMenu.CODE));
    target.setPath(rs.getString(PMenu.PATH));
    target.setUpperCode(rs.getString(PMenu.UPPER_CODE));
    target.setTitle(rs.getString(PMenu.TITLE));
    target.setType(MenuType.valueOf(rs.getString(PMenu.TYPE)));

    target.setIcon(rs.getString(PMenu.ICON));
    target.setParameters(rs.getString(PMenu.PARAMETERS));

    target.setSequence(rs.getInt(PMenu.SEQUENCE));

    return target;
  }
}
