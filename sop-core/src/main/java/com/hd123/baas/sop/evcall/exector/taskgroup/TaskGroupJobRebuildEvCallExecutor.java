package com.hd123.baas.sop.evcall.exector.taskgroup;

import com.hd123.baas.sop.job.bean.TaskGroupMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TaskGroupJobRebuildEvCallExecutor extends AbstractEvCallExecutor<TaskGroupJobRebuildEvCallMsg> {

  public static final String TASK_GROUP_JOB_REBUILD_EXECUTOR_ID = TaskGroupJobRebuildEvCallExecutor.class.getSimpleName();

  @Autowired
  private TaskGroupMgr taskGroupMgr;

  @Override
  protected void doExecute(TaskGroupJobRebuildEvCallMsg message, EvCallExecutionContext context) throws Exception {
    List<String> groupIds = message.getGroupIds();
    String tenant = message.getTenant();
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(groupIds, "任务组");
    for (String groupID : groupIds) {
      try {
        taskGroupMgr.rebuildJob(tenant, groupID);
      } catch (Exception e) {
        log.error("租户{}重建任务组失败,任务组ID：{}", tenant, groupID);
      }
    }
  }

  @Override
  protected TaskGroupJobRebuildEvCallMsg decodeMessage(String arg) throws BaasException {
    return JsonUtil.jsonToObject(arg, TaskGroupJobRebuildEvCallMsg.class);
  }
}
