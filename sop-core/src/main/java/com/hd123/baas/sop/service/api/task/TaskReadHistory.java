package com.hd123.baas.sop.service.api.task;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskReadHistory extends TenantStandardEntity {

  private static final long serialVersionUID = 4708017263915201741L;

  public String plan;
  public String planPeriod;
  public String operatorId;
  public String type;
}
