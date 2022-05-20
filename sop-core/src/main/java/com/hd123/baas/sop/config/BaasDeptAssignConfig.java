package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BcGroup(name = "部门作用域")
public class BaasDeptAssignConfig {
  private static final String PREFIX = "dept.assign.";
  @BcKey(name = "json格式", editor = "JSON")
  private String json;

  public static final String ASSIGN_CONFIG_KEY = "dept.assign.json";
}
