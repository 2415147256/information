package com.hd123.baas.sop.evcall.exector.sku.publishplan;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import com.hd123.rumba.commons.biz.entity.OperateInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * 上下架方案推送消息
 * 
 * @Author liuhaoxin
 * @Since 2021-12-7
 */
@Setter
@Getter
public class SkuPublishPlanAutoCreateEvCallMsg extends AbstractTenantEvCallMessage {
  /** 商品上下架id */
  private String uuid;
  /** 操作时间 */
  private OperateInfo operateInfo;
}
