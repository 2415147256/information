package com.hd123.baas.sop.service.api.voice;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class VoiceTemplate {
  private String uuid;
  /** 代码 */
  private VoiceTemplateCode code;
  /** 名称 */
  private String name;
  /** 语音内容 */
  private String content;
}
