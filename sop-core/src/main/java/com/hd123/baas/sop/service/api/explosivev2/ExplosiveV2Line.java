package com.hd123.baas.sop.service.api.explosivev2;

import com.hd123.rumba.commons.biz.entity.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 爆品活动行
 */
@Getter
@Setter
public class ExplosiveV2Line extends Entity {

  //租户
  private String tenant;
  //活动ID
  private String owner;
  //活动行号
  private Integer lineNo;
  //商品ID
  private String skuId;
  //商品代码
  private String skuCode;
  //商品GID
  private String skuGid;
  //商品名称
  private String skuName;
  //商品规格
  private BigDecimal skuQpc;
  //商品单位
  private String skuUnit;
  //订货价
  private BigDecimal inPrice;
  //限量数
  private BigDecimal limitQty;
  //起订量
  private BigDecimal minQty;
  //已订货量
  private BigDecimal usedLimit = BigDecimal.ZERO;
  //备注
  private String remark;
}
