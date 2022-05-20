package com.hd123.baas.sop.service.dao.sysconfig;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.sysconfig.SysConfig;

/**
 * @author W.J.H.7
 */
public class SysConfigMapper implements RowMapper<SysConfig> {
  @Override
  public SysConfig mapRow(ResultSet rs, int i) throws SQLException {
    SysConfig target = new SysConfig();
    target.setTenant(rs.getString(PSysConfig.TENANT));
    target.setSpec(rs.getString(PSysConfig.SPEC));
    target.setCfKey(rs.getString(PSysConfig.CF_KYE));
    target.setCfValue(rs.getString(PSysConfig.CF_VALUE));
    return target;
  }
}
