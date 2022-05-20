package com.hd123.baas.sop.evcall.exector.voice;

import java.util.Date;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
public class ActivityVoiceNotifyMsg extends AbstractTenantEvCallMessage {
  private Date notifyDate;
}
