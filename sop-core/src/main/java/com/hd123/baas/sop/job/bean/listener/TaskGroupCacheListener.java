package com.hd123.baas.sop.job.bean.listener;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.config.core.event.ConfigItemKey;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroup;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupService;
import com.hd123.baas.sop.config.TaskGroupJobConfig;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.taskgroup.TaskGroupJobRebuildEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.taskgroup.TaskGroupJobRebuildEvCallMsg;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.event.ConfigChangeEvent;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanghaixiao
 **/
@Slf4j
@Component
public class TaskGroupCacheListener implements ApplicationListener<ConfigChangeEvent> {

  @Value("${spring.application.name:}")
  protected String stack;

  @Autowired
  private EvCallEventPublisher publisher;

  @Autowired
  private TaskGroupService taskGroupService;

  @Autowired
  private BaasConfigClient baasConfigClient;

  @Override
  public void onApplicationEvent(ConfigChangeEvent configRefreshEvent) {
    log.info("{}收到配置中心刷新事件,判断是否为taskGroup配置", stack);
    ConfigItemKey configItemKey = configRefreshEvent.getKeys()
        .stream()
        .filter(i -> i.getKey().equals(TaskGroupJobConfig.CORN_EXPRESSION))
        .findAny()
        .orElse(null);
    if (configItemKey == null) {
      return;
    }
    String tenant = configItemKey.getTenant();
    TaskGroupJobConfig config = baasConfigClient.getConfig(tenant, TaskGroupJobConfig.class);
    int size = config.getSize();
    int page = 0;
    while (true) {
      QueryDefinition qd = new QueryDefinition(page,size);
      QueryResult<TaskGroup> query = taskGroupService.query(tenant, qd);
      List<TaskGroup> list = query.getRecords();
      if (CollectionUtils.isEmpty(list)) {
        break;
      }
      page++;
      List<String> groupIDs = list.stream().map(TaskGroup::getUuid).collect(Collectors.toList());
      TaskGroupJobRebuildEvCallMsg msg = new TaskGroupJobRebuildEvCallMsg();
      msg.setGroupIds(groupIDs);
      msg.setTenant(tenant);
      publisher.publishForNormal(TaskGroupJobRebuildEvCallExecutor.TASK_GROUP_JOB_REBUILD_EXECUTOR_ID,msg);
    }
  }
}
