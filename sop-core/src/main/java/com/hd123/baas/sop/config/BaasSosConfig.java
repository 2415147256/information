package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Deprecated
@Getter
@Setter
@BcGroup(name = "店务配置-->废弃")
public class BaasSosConfig {

  @BcKey(name = "店务租户id")
  private String tenant = "";
}
