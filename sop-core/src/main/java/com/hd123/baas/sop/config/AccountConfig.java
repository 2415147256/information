package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BcGroup(name = "account")
public class AccountConfig {

  private static final String PREFIX = "account.";

  @BcKey(name = "租户")
  private String tenant;
  @BcKey(name = "服务地址")
  private String serverUrl = "http://116.228.14.102:9401/mkh-account-service";
  @BcKey(name = "用户名")
  private String username = "guest";
  @BcKey(name = "密码")
  private String password = "guest";
}
