package com.hd123.baas.sop.remote.screen;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lilong on 2020/10/28.
 */
@Getter
@Setter
@BcGroup(name = "mkh-screen")
public class MkhScreenConfig {
  private static final String PREFIX = "mkh-screen.";

  @BcKey(name = "服务地址")
  private String serverUrl = "http://localhost:8090";
  @BcKey(name = "用户名")
  private String username = "guest";
  @BcKey(name = "密码")
  private String password = "guest";

}
