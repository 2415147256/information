package com.hd123.baas.sop.fcf.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BcGroup(name = "鲜食加工")
public class FcfConfig {
  @BcKey(name = "启用鲜食加工")
  private boolean enable;
  @BcKey(name = "mas2租户")
  private String tenant;
  @BcKey(name = "阿里云推送accessKeyId")
  private String accessKeyId;
  @BcKey(name = "阿里云推送secret")
  private String secret;
  @BcKey(name = "阿里云推送appKey")
  private String appKey;
  @BcKey(name = "大数据接口地址")
  private String url = "http://172.17.10.144:31010";
}