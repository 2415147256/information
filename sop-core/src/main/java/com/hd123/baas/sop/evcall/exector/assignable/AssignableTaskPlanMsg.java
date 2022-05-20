package com.hd123.baas.sop.evcall.exector.assignable;

import com.hd123.baas.sop.service.api.taskplan.TaskPlan;
import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author guyahui
 * @date 2021/5/18 21:33
 */
@Getter
@Setter
public class AssignableTaskPlanMsg extends AbstractTenantEvCallMessage {

  private TaskPlan taskPlan;

}
