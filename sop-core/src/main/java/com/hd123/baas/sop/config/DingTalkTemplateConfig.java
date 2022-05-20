package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BcGroup(name = "钉钉工作台消息提醒模板配置")
public class DingTalkTemplateConfig {
  private static final String PREFIX = "dingTalkTemplate.";
  @BcKey(name = "模板id")
  private String templateId ;
}
