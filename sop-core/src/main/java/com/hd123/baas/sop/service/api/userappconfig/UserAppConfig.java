package com.hd123.baas.sop.service.api.userappconfig;

import com.hd123.rumba.commons.biz.entity.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserAppConfig extends Entity {
  /**
   * 租户id
   */
  private String tenant;
  /**
   * 组织id
   */
  private String orgId;
  /**
   * 类型
   */
  private String type = "USER_APP_CONFIG";
  /**
   * 用户id
   */
  private String id;
  /**
   * 用户code
   */
  private String code;
  /**
   * 用户name
   */
  private String name;
  /**
   * 请求来源端
   */
  private String appId;
  /**
   * 扩展信息
   */
  private String ext;
}
