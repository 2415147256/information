package com.hd123.baas.sop.service.api.subsidyplan;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class LogExt {
  /** 属性 */
  private String field;
  /** 旧值 */
  private String oldValue;
  /** 新值 */
  private String newValue;
}
