package com.hd123.baas.sop.service.api.subsidyplan;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class ActivityException {
  /** 门店CODE */
  private String shopCode;
  /** 门店名称 */
  private String shopName;

  /** 失效原因 */
  private String reason;
}
