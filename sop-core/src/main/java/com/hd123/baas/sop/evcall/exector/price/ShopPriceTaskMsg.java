package com.hd123.baas.sop.evcall.exector.price;

import java.util.Date;

import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Getter
@Setter
public class ShopPriceTaskMsg extends AbstractTenantEvCallMessage {

  public Date getExecuteDate() {
    if (executeDate != null) {
      // 兼容数据。java.sql.Date
      executeDate = new Date(executeDate.getTime());
    }
    return executeDate;
  }
  /** 组织ID**/
  private String orgId;
  /** 指定待计算的日期 */
  private Date executeDate;
  /**
   * 任务类型 兼容历史数据 不传标识H6TaskType.PRICE
   */
  private H6TaskType taskType;

}
