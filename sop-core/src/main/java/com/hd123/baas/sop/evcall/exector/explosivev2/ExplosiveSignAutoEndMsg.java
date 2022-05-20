package com.hd123.baas.sop.evcall.exector.explosivev2;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author shenmin
 */
@Getter
@Setter
public class ExplosiveSignAutoEndMsg extends AbstractTenantEvCallMessage {
  private List<String> uuids;
}
