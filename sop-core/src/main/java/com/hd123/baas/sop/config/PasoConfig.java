package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BcGroup(name = "Paso报表")
public class PasoConfig {

  private static final String PREFIX = "paso.";

  @BcKey(name = "报表的服务地址", description = "参考：https://<对应环境的域名>/pasoreport-web/www/view/integration.html")
  private String url;
  @BcKey(name = "授权登录报表的用户id")
  private String curUser;
  @BcKey(name = "授权登录报表的用户code")
  private String curUserCode;
  @BcKey(name = "授权登录报表的组织id")
  private String curOrg;
  @BcKey(name = "授权登录报表的组织code")
  private String curOrgCode;
  @BcKey(name = "tokenName", description = "来自PASO报表配置项：latin-webframe.login.tokenName的值")
  private String tokenName = "_id_";
  @BcKey(name = "secret", description = "来自PASO报表配置项：latin-webframe.login.secret的值")
  private String secret;
}
