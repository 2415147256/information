package com.hd123.baas.sop.service.api.skumgr;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class DirectorySku{
  private String skuId;
  private String code;
  private String name;
  private BigDecimal qpc;
  private String unit;
  private String gid;
  private boolean channelRequired = false;
  private boolean directoryRequired = false;
}
