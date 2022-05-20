package com.hd123.baas.sop.service.api.entity;

import java.math.BigDecimal;

import com.qianfan123.baas.common.entity.BEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 价格管理，用到的统一uuid + name + value 模型
 * 
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Getter
@Setter
public class PUnv extends BEntity {
  private String name;
  private BigDecimal value;
}
