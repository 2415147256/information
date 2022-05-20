package com.hd123.baas.sop.service.api.shopconfig;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.spms.commons.calendar.DateRange;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class ShopConfig extends TenantEntity {
  // 门店
  private String shop;
  //code
  private String code;
  // 门店暂停叫货时间段
  private List<DateRange> orderStopDates;
}
