package com.hd123.baas.sop.remote.uas;

import lombok.Getter;

/**
 * @author zhangjiahao
 * @since 1.0.0
 */
public enum UserStatementEffect {

  /**
   * 允许
   */
  ALLOW("允许"),

  /**
   * 拒绝
   */
  DENY("拒绝");

  @Getter
  private String caption;

  UserStatementEffect(String caption) {
    this.caption = caption;
  }
}
