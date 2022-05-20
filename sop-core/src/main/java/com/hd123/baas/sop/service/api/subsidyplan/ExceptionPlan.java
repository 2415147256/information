package com.hd123.baas.sop.service.api.subsidyplan;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class ExceptionPlan {
  /** 门店CODE */
  private String shopCode;
  /** 门店名称 */
  private String shopName;

  /** 计划id */
  private String planId;
  /** 计划名称 */
  private String planName;

  /** 原生效开始时间 */
  private Date oldEffectiveStartTime;
  /** 原生效结束时间 */
  private Date oldEffectiveEndTime;
  /** 新生效开始时间 */
  private Date newEffectiveStartTime;
  /** 新生效结束时间 */
  private Date newEffectiveEndTime;
  /** 失效原因 */
  private String reason;
}
