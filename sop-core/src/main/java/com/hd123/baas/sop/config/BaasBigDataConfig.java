package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
@BcGroup(name = "大数据配置")
public class BaasBigDataConfig {
  private static final String PREFIX = "bigdata.";
  @BcKey(name = "租户ID")
  private String tenant;

  @BcKey(name = "服务地址")
  private String serverUrl;
  @BcKey(name = "token")
  private String token;

}