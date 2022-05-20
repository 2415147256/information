package com.hd123.baas.sop.remote.rsh6sop.storeprom;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class StorePromAlcPrcBill implements Serializable {
  private static final long serialVersionUID = -1752313664563343632L;

  public static final String TABLE_NAME = "StorePromAlcPrcBill";

  /**
   * 促销ID
   */
  private String promId;
  /**
   * 促销名称
   */
  private String promName;
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
   * 起订金额
   */
  private BigDecimal ordLimitAmount;
  /**
   * 起订数量
   */
  private BigDecimal ordLimitQty;
  /**
   * 总部承担比例
   */
  private BigDecimal headSharingRate;
  /**
   * 督导承担比例
   */
  private BigDecimal supervisorSharingRate;
  /**
   * 状态
   */
  private String state;

  /**
   * 详情
   */
  private List<StorePromAlcPrcBillDtl> details;

}
