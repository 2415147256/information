package com.hd123.baas.sop.remote.rsh6sop;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import com.qianfan123.baas.config.api.field.ConfigEditor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lilong on 2020/10/28.
 */
@Getter
@Setter
@BcGroup(name = "h6-sop")
public class RsH6SOPConfig {
  private static final String PREFIX = "h6-sop.";

  @BcKey(name = "是否开启下发，默认=true", editor = ConfigEditor.BOOL)
  private boolean enabled = Boolean.TRUE;
  @BcKey(name = "服务地址")
  private String serverUrl = "http://localhost:8090";
  @BcKey(name = "用户名")
  private String username = "guest";
  @BcKey(name = "密码")
  private String password = "guest";

}
