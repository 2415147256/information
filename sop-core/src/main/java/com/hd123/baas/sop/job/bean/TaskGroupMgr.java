package com.hd123.baas.sop.job.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.TaskGroupJobConfig;
import com.hd123.baas.sop.job.JobHandlerService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/5.
 */
@Component
@Slf4j
public class TaskGroupMgr {

  @Autowired
  private JobHandlerService jobHandlerService;

  @Autowired
  private BaasConfigClient configClient;

  private String cronExpression;

  /**
   * 不存在则创建job。若存在，则判断时间表达式是否一致，一致则不重建
   */
  public void buildJob(String tenant, String groupId) throws Exception {
    this.buildJob(tenant, groupId, false, false);
  }

  public void removeJob(String tenant, String groupId) {
    String jobName = buildJobName(tenant, groupId);
    jobHandlerService.removeJob(jobName, TaskGroupJob.class);
  }

  /**
   * 创建job。并立即启动一次
   */
  public void startJob(String tenant, String groupId) throws Exception {
    this.buildJob(tenant, groupId, false, true);
  }

  /**
   * 重建
   */
  public void rebuildJob(String tenant, String groupId) throws Exception {
    this.buildJob(tenant, groupId, true, false);
  }

  private void buildJob(String tenant, String groupId, boolean rebuild, boolean runNow) throws Exception {
    String jobName = buildJobName(tenant, groupId);
    boolean build = true;
    TaskGroupJobConfig taskGroupJobConfig = configClient.getConfig(tenant, TaskGroupJobConfig.class);
    cronExpression = taskGroupJobConfig.getCronExpression();
    if (jobHandlerService.contain(jobName, TaskGroupJob.class)) {
      if (!rebuild) {
        List<? extends Trigger> triggers = jobHandlerService.getTriggersOfJob(jobName, TaskGroupJob.class);
        if (CollectionUtils.isNotEmpty(triggers) && triggers.size() == 1) {
          Trigger trigger = triggers.get(0);
          if (trigger instanceof CronTrigger) {
            CronTrigger cronTrigger = (CronTrigger) trigger;
            if (cronTrigger.getCronExpression().equals(cronExpression)) {
              log.info("租户<{}>，任务组<{}>，job不需要重建", tenant, groupId);
              build = false;
            }
          }
        }
      }
      if (build) {
        jobHandlerService.removeJob(jobName, TaskGroupJob.class);
      }
    }
    if (build) {
      Map<String, Object> dataMap = new HashMap<>();
      dataMap.put(TaskGroupJob.TENANT, tenant);
      dataMap.put(TaskGroupJob.GROUP_ID, groupId);
      CronTrigger cronTrigger = TriggerBuilder.newTrigger()
          .startNow()
          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
          .build();
      jobHandlerService.startJob(jobName, TaskGroupJob.class, cronTrigger, dataMap);
    }
    if (runNow) {
      // 立即启动一次
      jobHandlerService.runJob(jobName, TaskGroupJob.class, new HashMap<>());
    }
  }

  private String buildJobName(String tenant, String groupId) {
    return tenant + "_" + groupId;
  }

}
