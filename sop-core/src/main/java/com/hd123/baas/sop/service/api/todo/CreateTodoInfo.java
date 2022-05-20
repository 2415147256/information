package com.hd123.baas.sop.service.api.todo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateTodoInfo {
  /**
   * 业务id
   */
  private String sourceId;
  /**
   * 场景值
   */
  private String sceneCode;
  /**
   * 触发方
   */
  private String source;
  /**
   *携带参数
   */
  private String sourceExt;
}
