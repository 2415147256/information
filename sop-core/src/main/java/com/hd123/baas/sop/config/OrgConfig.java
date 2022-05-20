package com.hd123.baas.sop.config;

import com.hd123.baas.sop.common.OrgConstants;
import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import com.qianfan123.baas.config.api.field.ConfigEditor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BcGroup(appId = "sop-service",name = "多组织")
public class OrgConfig {

  private static final String PREFIX = "org.";

  @BcKey(name = "总部组织id", editor = ConfigEditor.TEXT, description = "多组织租户总部的组织id")
  private String masterId = OrgConstants.DEFAULT_SOP_ORG_ID;
  @BcKey(name = "组织列表，json对象，格式：[{id:xx,code:xx,name:xx}]",editor = ConfigEditor.JSON)
  private String list;

  @BcKey(name = "是否开启多组织查看")
  private boolean authView = false;

  @BcKey(name = "多组织排除的模块,以','分割")
  private String excludeModules;

  @BcKey(name = "是否开启所属组织自动授权于对应的角色")
  private boolean enableRoleAuthOwnerOrg = true;

  @Setter
  @Getter
  public static class Org {
    private String id;
    private String code;
    private String name;
  }
}

