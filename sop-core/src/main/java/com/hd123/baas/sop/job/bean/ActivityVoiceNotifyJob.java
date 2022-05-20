package com.hd123.baas.sop.job.bean;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.VoiceConfig;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.voice.ActivityVoiceEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.voice.ActivityVoiceNotifyMsg;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.config.api.entity.ConfigItem;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2019/9/4.
 */
@Slf4j
@DisallowConcurrentExecution
public class ActivityVoiceNotifyJob implements Job {

  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private EvCallEventPublisher publisher;

  @Value("${sop-service.appId}")
  private String appId;

  @Override
  public void execute(JobExecutionContext context) {
    try {
      MDC.put("trace_id", UUID.randomUUID().toString());
      log.info("准备执行查询活动提醒JOB.");
      Set<String> tenants = getTenants();
      if (CollectionUtils.isEmpty(tenants)) {
        log.info("无租户配置语音配置项，忽略。");
        return;
      }
      for (String t : tenants) {
        publishVoiceNotify(t);
      }
    } catch (Throwable e) {
      log.error("activityVoiceNotify job:", e);
    }
  }

  public void publishVoiceNotify(String tenant) {
    ActivityVoiceNotifyMsg msg = new ActivityVoiceNotifyMsg();
    msg.setTenant(tenant);
    msg.setNotifyDate(new Date());
    publisher.publishForNormal(ActivityVoiceEvCallExecutor.ACTIVITY_VOICE_EXECUTOR_ID, msg);
  }

  public Set<String> getTenants() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ConfigItem.Queries.KEY, Cop.EQUALS, VoiceConfig.VOICE_ENABLED);
    qd.addByField(ConfigItem.Queries.APP_ID, Cop.EQUALS, appId);
    QueryResult<ConfigItem> result = configClient.query(qd);
    if (result != null) {
      return result.getRecords().stream().map(i -> i.getTenant()).collect(Collectors.toSet());
    }
    return null;
  }

}
