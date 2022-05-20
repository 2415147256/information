package com.hd123.baas.sop.evcall.exector.taskgroup;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskGroupJobRebuildEvCallMsg extends AbstractTenantEvCallMessage {

  private String tenant;
  private List<String> groupIds;

}
