package com.hd123.baas.sop.evcall.exector.announcement;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/20.
 */
@Getter
@Setter
public class AnnouncementMsg extends AbstractTenantEvCallMessage {

  private String pk;
  private String orgId;

}
