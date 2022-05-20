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
@BcGroup(name = "爆品活动语音提醒配置")
public class ExplosiveActivityVoiceConfig {
  //预定报名提前得时间
  @BcKey(name = "指定通知的角色")
  private String roleId;
}
