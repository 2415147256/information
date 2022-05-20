package com.hd123.baas.sop.evcall.exector.assignable;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.taskplan.TaskPlan;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.job.bean.TaskPlanAssignableJob;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author guyahui
 * @Since
 */
@Slf4j
@Component
public class AssignableTaskPlanPublishEvCallExecutor extends AbstractEvCallExecutor<AssignableTaskPlanMsg> {

  public static final String ASSIGNABLE_TASK_PLAN_PUBLISH_EXECUTOR_ID = AssignableTaskPlanPublishEvCallExecutor.class
      .getSimpleName();

  @Autowired
  private TaskPlanAssignableJob taskPlanAssignableJob;

  @Override
  @Tx
  protected void doExecute(AssignableTaskPlanMsg msg, EvCallExecutionContext context) throws Exception {

    TaskPlan taskPlan = msg.getTaskPlan();
    if (taskPlan == null || StringUtils.isEmpty(taskPlan.getCycle())) {
      return;
    }
    taskPlanAssignableJob.publishTaskPlan(msg.getTenant(), msg.getTaskPlan(), TaskPlanAssignableJob.SOURCE_HAND);
  }

  @Override
  protected AssignableTaskPlanMsg decodeMessage(String msg) throws BaasException {
    log.info("收到TaskPlanMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, AssignableTaskPlanMsg.class);
  }
}
