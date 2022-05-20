package com.hd123.baas.sop.service.api.taskgroup;

import com.hd123.baas.sop.service.api.TenantEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskGroupShop extends TenantEntity {
  public String taskGroup;
  public String shop;
}
