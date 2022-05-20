package com.hd123.baas.sop.evcall.exector.subsidyplan;

import java.math.BigDecimal;
import java.util.Date;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class PlanPushEvCallMsg extends AbstractTenantEvCallMessage {
  /**
   * 计划ID
   */
  private String planId;
  /**
   * 计划名称
   */
  private String planName;
  /**
   * 门店GID
   */
  private Integer storeGid;
  /**
   * 生效时间
   */
  private Date effectiveStartTime;
  /**
   * 截止时间
   */
  private Date effectiveEndTime;
  /**
   * 账户额度
   */
  private BigDecimal amount;
  /**
   * 状态
   */
  private String state;

}
