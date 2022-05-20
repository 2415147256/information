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
@BcGroup(name = "语音提醒")
public class VoiceConfig {
  private static final String PREFIX = "voice.";
  public static final String VOICE_ENABLED = PREFIX + "enabled";
  @BcKey(name = "是否启用")
  private boolean enabled = false;
}
