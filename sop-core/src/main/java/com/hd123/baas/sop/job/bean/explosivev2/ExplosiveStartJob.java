package com.hd123.baas.sop.job.bean.explosivev2;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Service;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveAutoStartEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveSignAutoEndMsg;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.config.api.entity.ConfigItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shenmin
 */
@Component
@Slf4j
public class ExplosiveStartJob implements Job {
  public final static int DEFAULT_PAGE_SIZE = 1000;

  @Value("${sop-service.appId}")
  private String appId;

  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private ExplosiveV2Service explosiveV2Service;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("执行爆品活动开始job");
    Set<String> tenants;
    try {
      tenants = getTenants();
    } catch (Exception e) {
      log.error("查询租户信息异常，msg={}", e.getMessage(), e);
      return;
    }
    for (String tenant : tenants) {
      run(tenant);
    }
  }

  private void run(String tenant) {
    int page = 0;
    // 爆品活动开始
    while (true) {
      QueryDefinition qd = new QueryDefinition();
      qd.addByField(ExplosiveV2.Queries.START_DATE, Cop.BEFORE_OR_EQUAL, new Date());
      qd.addByField(ExplosiveV2.Queries.STATE, Cop.EQUALS, ExplosiveV2.State.ACTIVE.name());
      qd.setPageSize(DEFAULT_PAGE_SIZE);
      qd.setPage(page);
      QueryResult<ExplosiveV2> result = explosiveV2Service.query(tenant, qd);
      // 跳出循环
      if (result == null || CollectionUtils.isEmpty(result.getRecords())) {
        break;
      }

      List<String> uuids = result.getRecords().stream().map(ExplosiveV2::getUuid).collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(uuids)) {
        ExplosiveSignAutoEndMsg msg = new ExplosiveSignAutoEndMsg();
        msg.setTenant(tenant);
        msg.setUuids(uuids);
        msg.setTraceId(IdGenUtils.buildRdUuid());
        publisher.publishForNormal(ExplosiveAutoStartEvCallExecutor.EXPLOSIVE_AUTO_START_EXECUTOR_ID, msg);
      }
      page++;
    }
  }

  private Set<String> getTenants() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ConfigItem.Queries.APP_ID, Cop.EQUALS, appId);
    QueryResult<ConfigItem> result = configClient.query(qd);
    if (result != null) {
      return result.getRecords().stream().map(ConfigItem::getTenant).collect(Collectors.toSet());
    }
    return null;
  }
}
