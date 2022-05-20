package com.hd123.baas.sop.evcall.exector.taskgroup;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskGroupCreateEvCallMsg extends AbstractTenantEvCallMessage {
  private String groupId;
}
