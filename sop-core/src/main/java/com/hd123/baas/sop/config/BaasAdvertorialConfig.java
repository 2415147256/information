package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BcGroup(name = "图文投放")
public class BaasAdvertorialConfig {
  private static final String PREFIX = "advertorial.";
  @BcKey(name = "图片投放路径", editor = "Text")
  private String path;
}
