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
@BcGroup(name = "加盟助手配置")
public class JmzsConfig {

  private static final String PREFIX = "jmzs.";
  @BcKey(name = "加盟助手消息来源",description = "[{\"type\":\"SALE\",\"order\":10}],order值越小越靠前")
  private String msgSourceType;

}
