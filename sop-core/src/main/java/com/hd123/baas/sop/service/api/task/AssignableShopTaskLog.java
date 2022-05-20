package com.hd123.baas.sop.service.api.task;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author shenmin
 * @date 2022/1/5
 */
@Getter
@Setter
public class AssignableShopTaskLog {

  //logUUID
  private String uuid;
  //任务ID
  private String owner;
  //得分
  private BigDecimal score;
  //反馈
  private List<Feedback> feedbacks;
  //操作类型 save/submit
  private String operateType;
  //appId
  private String appId;
}
