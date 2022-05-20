package com.hd123.baas.sop.service.api.taskgroup;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务组状态Enum
 *
 * @author liyan
 * @date 2021/4/28
 */
@Getter
@AllArgsConstructor
public enum TaskGroupState {
  INIT("未发布") //
  , SUBMITTED("已发布") //
  ;

  private final String caption;
}
