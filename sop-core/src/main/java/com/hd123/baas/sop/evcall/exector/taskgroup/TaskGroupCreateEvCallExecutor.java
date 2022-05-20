package com.hd123.baas.sop.evcall.exector.taskgroup;

import com.hd123.baas.sop.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.job.bean.TaskGroupMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.rumba.evcall.EvCallExecutionContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TaskGroupCreateEvCallExecutor extends AbstractEvCallExecutor<TaskGroupCreateEvCallMsg> {

  public static final String TASK_GROUP_CREATE_EXECUTOR_ID = TaskGroupCreateEvCallExecutor.class.getSimpleName();

  @Autowired
  TaskGroupMgr taskGroupMgr;

  @Override
  protected void doExecute(TaskGroupCreateEvCallMsg message, EvCallExecutionContext context) throws Exception {
    String tenant = message.getTenant();
    String groupId = message.getGroupId();
    log.info("监听到任务组新建事件,租户:{},任务组ID:{}", tenant, groupId);
    taskGroupMgr.buildJob(tenant, groupId);
  }

  @Override
  protected TaskGroupCreateEvCallMsg decodeMessage(String arg) {
    return JsonUtil.jsonToObject(arg, TaskGroupCreateEvCallMsg.class);
  }
}
