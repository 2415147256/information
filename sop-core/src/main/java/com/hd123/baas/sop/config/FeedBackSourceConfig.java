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
@BcGroup(name = "质量反馈来源")
public class FeedBackSourceConfig {

  @BcKey(name = "来源")
  private String source = "[\"EC\",\"offline\"]";
}
