/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	h6-sop-api
 * 文件名：	StorePromPlan.java
 * 模块说明：	
 * 修改历史：
 * 2021年7月2日 - huangchun - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.subsidyplan;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 门店补贴计划
 * 
 * @author huangchun
 *
 */
@Data
public class StorePromPlan implements Serializable {
  private static final long serialVersionUID = -3791082413345113428L;

  public static final String TABLE_NAME = "STOREPROMPLAN";

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
