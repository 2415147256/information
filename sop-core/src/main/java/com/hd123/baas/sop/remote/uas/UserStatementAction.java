package com.hd123.baas.sop.remote.uas;

import lombok.Getter;

/**
 * @author zhangjiahao
 */
public enum UserStatementAction {
  /**
   * query
   */
  QUERY("query");

  @Getter
  private String caption;

  UserStatementAction(String caption) {
    this.caption = caption;
  }
}
