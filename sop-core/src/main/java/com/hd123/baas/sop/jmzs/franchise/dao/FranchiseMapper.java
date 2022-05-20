package com.hd123.baas.sop.jmzs.franchise.dao;

import com.hd123.baas.sop.jmzs.franchise.api.Franchise;
import com.hd123.baas.sop.utils.JsonUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FranchiseMapper extends PFranchise.RowMapper<Franchise> {
  @Override
  public Franchise mapRow(ResultSet rs, int i) throws SQLException {
    Franchise target = new Franchise();
    mapFields(rs, i, target);
    target.setTenant(rs.getString(PFranchise.TENANT));
    target.setOrgId(rs.getString(PFranchise.ORG_ID));
    target.setCode(rs.getString(PFranchise.CODE));
    target.setId(rs.getString(PFranchise.ID));
    target.setName(rs.getString(PFranchise.NAME));
    target.setMobile(rs.getString(PFranchise.MOBILE));
    target.setPosition(rs.getString(PFranchise.POSITION));
    target.setStatus(rs.getString(PFranchise.STATUS));
    target.setContractImages(JsonUtil.jsonToList(rs.getString(PFranchise.CONTRACT_IMAGES), String.class));
    target.setCreateDate(rs.getTimestamp(PFranchise.CREATE_DATE));
    target.setDeleted(rs.getBoolean(PFranchise.DELETED));
    target.setExt(rs.getString(PFranchise.EXT));

    return target;
  }
}
