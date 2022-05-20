package com.hd123.baas.sop.remote.rsh6sop.ordadvlmtdate;

import java.util.List;

import com.hd123.spms.commons.calendar.DateRange;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class OrdAdvLmtDate {
  // 门店代码
  private String storeCode;
  // 时间段信息
  private List<DateRange> data;
}
