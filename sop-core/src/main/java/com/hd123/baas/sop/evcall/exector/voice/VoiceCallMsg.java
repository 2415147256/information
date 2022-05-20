package com.hd123.baas.sop.evcall.exector.voice;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class VoiceCallMsg extends AbstractTenantEvCallMessage {
  // PK
  private String pk;

}
