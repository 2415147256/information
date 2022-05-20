package com.hd123.baas.sop.evcall.exector.message;

import java.util.Collection;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/22.
 */
@Getter
@Setter
public class MessageReadMsg extends AbstractTenantEvCallMessage {

  private String orgId;

  private Collection<String> pks;

}
