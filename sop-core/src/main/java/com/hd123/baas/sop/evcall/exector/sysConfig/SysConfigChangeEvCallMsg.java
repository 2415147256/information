package com.hd123.baas.sop.evcall.exector.sysConfig;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class SysConfigChangeEvCallMsg extends AbstractTenantEvCallMessage {
  /** spec **/
  private String spec = "def";
  /** 配置key **/
  private String cfgKey;
}
