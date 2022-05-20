package com.hd123.baas.sop.common;

import static com.hd123.baas.sop.common.OrgConstants.*;

public class DefaultOrgIdConvert {

  /**
   * 默认组织ID强制转换
   *
   * @param orgId
   *     组织ID
   */
  public static String toH6DefOrgId(String orgId) {
    return toH6DefOrgId(orgId, true);
  }

  /**
   * @param orgId
   *     组织ID
   * @param force
   *     是否强制
   */
  public static String toH6DefOrgId(String orgId, boolean force) {
    if (orgId == null) {
      return null;
    }
    String h6DefaultId = null;
    if (force) {
      h6DefaultId = DEFAULT_H6_ORG_ID;
    }
    orgId = orgId.equals(DEFAULT_SOP_ORG_ID) ? h6DefaultId : orgId;
    orgId = orgId.equals(DEFAULT_MAS_ORG_ID) ? h6DefaultId : orgId;
    return orgId;
  }

  public static String toMasDefOrgId(String orgId) {
    if (orgId == null) {
      return null;
    }
    orgId = orgId.equals(DEFAULT_SOP_ORG_ID) ? DEFAULT_MAS_ORG_ID : orgId;
    return orgId;
  }
}
