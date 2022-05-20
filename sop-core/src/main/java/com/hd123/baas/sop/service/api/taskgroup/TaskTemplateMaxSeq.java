package com.hd123.baas.sop.service.api.taskgroup;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author guyahui
 * @date 2021/7/29 16:26
 */
@Getter
@Setter
@ToString
public class TaskTemplateMaxSeq implements Serializable {

  private static final long serialVersionUID = 1609835578869068207L;

  // 主题ID
  private String owner;
  // 排序值
  private int seq = 9999;
  // 租户
  private String tenant;

}
