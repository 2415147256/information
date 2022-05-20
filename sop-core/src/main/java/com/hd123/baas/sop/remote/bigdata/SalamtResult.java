package com.hd123.baas.sop.remote.bigdata;

import lombok.Getter;
import lombok.Setter;

/**
 * 销售金额时段数据
 *
 * @author shenmin
 */
@Getter
@Setter
public class SalamtResult {
  //昨日销售金额
  private String salamtLd;
  //今日销售金额
  private String salamtTd;
  //时间
  private String timeStr;
}
