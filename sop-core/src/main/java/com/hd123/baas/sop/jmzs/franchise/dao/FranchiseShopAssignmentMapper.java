package com.hd123.baas.sop.jmzs.franchise.dao;

import com.hd123.baas.sop.jmzs.franchise.api.FranchiseShopAssignment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FranchiseShopAssignmentMapper extends PFranchiseShopAssignment.RowMapper<FranchiseShopAssignment> {
  @Override
  public FranchiseShopAssignment mapRow(ResultSet rs, int i) throws SQLException {
    FranchiseShopAssignment target = new FranchiseShopAssignment();
    mapFields(rs, i, target);
    target.setTenant(rs.getString(PFranchiseShopAssignment.TENANT));
    target.setShopId(rs.getString(PFranchiseShopAssignment.SHOP_ID));
    target.setShopCode(rs.getString(PFranchiseShopAssignment.SHOP_CODE));
    target.setShopName(rs.getString(PFranchiseShopAssignment.SHOP_NAME));
    target.setFranchiseId(rs.getString(PFranchiseShopAssignment.FRANCHISE_ID));
    target.setFranchiseCode(rs.getString(PFranchiseShopAssignment.FRANCHISE_CODE));
    target.setFranchiseName(rs.getString(PFranchiseShopAssignment.FRANCHISE_NAME));
    target.setFranchiseUuid(rs.getString(PFranchiseShopAssignment.FRANCHISE_UUID));

    return target;
  }
}
