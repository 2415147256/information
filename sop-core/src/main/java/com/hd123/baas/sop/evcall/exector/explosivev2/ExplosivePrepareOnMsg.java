package com.hd123.baas.sop.evcall.exector.explosivev2;

import com.hd123.baas.sop.service.api.explosivev2.ExplosiveActionV2;
import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shenmin
 */
@Getter
@Setter
public class ExplosivePrepareOnMsg extends AbstractTenantEvCallMessage {
  private String uuid;
  private ExplosiveActionV2 action;
  private OperateInfo operateInfo;
}
