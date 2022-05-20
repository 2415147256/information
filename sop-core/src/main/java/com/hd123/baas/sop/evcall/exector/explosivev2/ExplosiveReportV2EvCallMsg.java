package com.hd123.baas.sop.evcall.exector.explosivev2;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * 爆品活动详情报表
 *
 * @author liuhaoxin
 * @since 2021-12-7
 */
@Setter
@Getter
public class ExplosiveReportV2EvCallMsg extends AbstractTenantEvCallMessage {
  /** 组织id */
  private String orgId;
  /** 活动报名日志ID */
  private String explosiveLogId;
  /** 操作人 */
  private OperateInfo operateInfo;
}
