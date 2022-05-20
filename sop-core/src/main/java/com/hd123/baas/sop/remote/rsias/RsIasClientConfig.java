package com.hd123.baas.sop.remote.rsias;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BcGroup(name = "库存中台配置")
public class RsIasClientConfig {

  private static final String PREFIX = "rsIasClient.";

  @BcKey(name = "组件版本(默认: V1)")
  private String version = RsIasClientVersion.V1.name();
}
