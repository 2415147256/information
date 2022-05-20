package com.hd123.baas.sop.job.bean.explosive;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.voice.ActivityVoiceNotifyMsg;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.config.api.entity.ConfigItem;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author maodapeng
 * @Since
 */
@DisallowConcurrentExecution
@Slf4j
public class ExplosiveActivityVoiceNotifyJob implements Job {

  @Autowired
  private BaasConfigClient client;
  @Value("${baas-config.currentAppId:${spring.application.name}}")
  private String currentAppId;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("开始爆品活动提醒，事件");
    Set<String> tenants;
    try {
      tenants = getTenants();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    for (String tenant : tenants) {
      publishActivityVoiceEvCall(tenant);
    }
  }

  private void publishActivityVoiceEvCall(String tenant) {
    ActivityVoiceNotifyMsg msg = new ActivityVoiceNotifyMsg();
    msg.setTenant(tenant);
    msg.setNotifyDate(new Date());
    publisher.publishForNormal(tenant,msg);
  }

  /**
   * 获取所有租户
   *
   * @return 租户列表
   */
  private Set<String> getTenants() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    /**
     * {@link com.hd123.baas.sop.config.H6TaskConfig}
     */
    qd.addByField(ConfigItem.Queries.KEY, Cop.EQUALS, "voice.enabled");
    qd.addByField(ConfigItem.Queries.VALUE, Cop.EQUALS, "true");
    qd.addByField(ConfigItem.Queries.APP_ID, Cop.EQUALS, currentAppId);
    QueryResult<ConfigItem> result = client.query(qd);
    if (CollectionUtils.isEmpty(result.getRecords())) {
      return new HashSet<>();
    }
    return result.getRecords().stream().map(ConfigItem::getTenant).collect(Collectors.toSet());
  }
}
