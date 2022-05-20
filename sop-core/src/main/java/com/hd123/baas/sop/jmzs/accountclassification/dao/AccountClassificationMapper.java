package com.hd123.baas.sop.jmzs.accountclassification.dao;

import com.hd123.baas.sop.jmzs.accountclassification.api.AccountClassification;
import com.hd123.baas.sop.jmzs.accountclassification.api.AccountClassificationState;
import com.hd123.baas.sop.jmzs.accountclassification.api.AccountClassificationType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountClassificationMapper extends PAccountClassification.RowMapper<AccountClassification> {
  @Override
  public AccountClassification mapRow(ResultSet rs, int i) throws SQLException {
    AccountClassification target = new AccountClassification();
    mapFields(rs, i, target);
    target.setTenant(rs.getString(PAccountClassification.TENANT));
    target.setOrgId(rs.getString(PAccountClassification.ORG_ID));
    target.setCode(rs.getInt(PAccountClassification.CODE));
    target.setState(AccountClassificationState.valueOf(rs.getString(PAccountClassification.STATE)));
    target.setType(AccountClassificationType.valueOf(rs.getString(PAccountClassification.TYPE)));
    target.setName(rs.getString(PAccountClassification.NAME));

    return target;
  }
}
