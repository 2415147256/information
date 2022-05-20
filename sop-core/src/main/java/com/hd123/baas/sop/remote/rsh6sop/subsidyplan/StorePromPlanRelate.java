/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	h6-sop-api
 * 文件名：	StorePromPlanRelate.java
 * 模块说明：	
 * 修改历史：
 * 2021年7月2日 - huangchun - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.subsidyplan;

import java.io.Serializable;

import lombok.Data;

/**
 * 门店补贴计划与促销规则的关联
 * 
 * @author huangchun
 *
 */
@Data
public class StorePromPlanRelate implements Serializable {
  private static final long serialVersionUID = -523044072695532097L;

  public static final String TABLE_NAME = "StorePromPlanRelate";

  /**
   * 计划ID
   */
  private String planId;
  /**
   * 促销促销类型:PROMOTE_ACTIVITY-零售促销，PRICE_PROMOTION-到店价促销，PRICE_PROMOTION_MODEL-到店价模型促销
   */
  private String promType;
  /**
   * 促销ID
   */
  private String promId;

}
