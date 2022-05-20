package com.hd123.baas.sop.evcall.exector.skumgr;

import java.util.Date;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Getter
@Setter
public class ShopSkuTaskMsg extends AbstractTenantEvCallMessage {

  public Date getExecuteDate() {
    if (executeDate != null) {
      // 兼容数据。java.sql.Date
      executeDate = new Date(executeDate.getTime());
    }
    return executeDate;
  }

  /** 指定待商品下发的日期 */
  private Date executeDate;
  // 组织id
  private String orgId;

}
