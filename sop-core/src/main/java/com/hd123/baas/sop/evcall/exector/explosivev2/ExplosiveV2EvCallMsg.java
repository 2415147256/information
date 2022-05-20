package com.hd123.baas.sop.evcall.exector.explosivev2;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 爆品活动推送消息
 *
 * @author liuhaoxin
 * @since 2021-12-7
 */
@Setter
@Getter
public class ExplosiveV2EvCallMsg extends AbstractTenantEvCallMessage {
  /**
   * 爆品活动id
   */
  private String explosiveId;
  /**
   * 爆品活动报名Ids
   */
  private List<String> explosiveSignIds;

}
