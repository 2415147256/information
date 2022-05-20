package com.hd123.baas.sop.service.api.subsidyplan;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class ActivityRelationException {

  /** 活动ID */
  private String activityId;

  /** 活动类型 */
  private ActivityType activityType;
  /** 活动状态 */
  private ActivityState activityState;
  /** 活动名称 */
  private String activityName;
  /** 活动开始时间 */
  private Date activityStartTime;
  /** 活动结束时间 */
  private Date activityEndTime;
}
