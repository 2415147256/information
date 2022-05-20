package com.hd123.baas.sop.service.api;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsKey {
  private String gid;
  private BigDecimal qpc;

  public GoodsKey(String gid, BigDecimal qpc) {
    this.gid = gid.toUpperCase();
    this.qpc = new BigDecimal(qpc.stripTrailingZeros().toPlainString());
  }
}
