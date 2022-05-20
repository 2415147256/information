package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
@BcGroup(name = "门店停止订货配置")
public class ShopStopOrderConfig {

  @BcKey(name = "当天停止订货时间")
  private String todayStopTime = "16:00:00";
}
