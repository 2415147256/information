package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
@BcGroup(name = "log")
public class LogConfig {

  private static final String PREFIX = "log.";

  @BcKey(name = "是否打开")
  private boolean isOpen = false;
}
