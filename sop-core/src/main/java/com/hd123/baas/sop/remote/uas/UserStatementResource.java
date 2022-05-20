package com.hd123.baas.sop.remote.uas;

import lombok.Getter;

/**
 * @author zhangjiahao
 * @since 1.0.0
 */
public enum UserStatementResource {

  /**
   * gpas:customer
   */
  CUSTOMER("gpas:customer"),

  /**
   * shop
   */
  SHOP("shop"),
  /**
   * 组织内的数据
   */
  ORG_DATA("org:data"),
  /**
   * 区域数据
   */
  AREA_DATA("area:data")
  ;

  @Getter
  private String caption;

  UserStatementResource(String caption) {
    this.caption = caption;
  }
}
