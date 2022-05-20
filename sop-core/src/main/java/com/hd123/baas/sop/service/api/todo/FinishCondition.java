package com.hd123.baas.sop.service.api.todo;

import com.hd123.baas.sop.service.api.todo.TargetTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author mibo
 */
@Setter
@Getter
public class FinishCondition {
  /**
   * 结束范围
   */
  private TargetTypeEnum overRange;
  /**
   * 结束条件
   */
  private String overCondition;
  /**
   * 结束描述
   */
  private String overDescription;
}
