package com.hd123.baas.sop.job.bean.explosivev2;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveActionV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosivePrepareOnOffEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosivePrepareOnMsg;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.config.api.entity.ConfigItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shenmin
 */
@Slf4j
public abstract class AbstractExplosiveJob implements Job {
  public final static int DEFAULT_PAGE_SIZE = 1000;

  @Value("${sop-service.appId}")
  private String appId;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private EvCallEventPublisher publisher;

  protected abstract QueryResult<ExplosiveV2> query(String tenant, int page);

  @Tx
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    MDC.put("trace_id", IdGenUtils.buildIidAsString());
    log.info("执行爆品活动上/下架job");
    Set<String> tenants = new HashSet<>();
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


  public void run(String tenant) {
    int page = 0;
    while (true) {
      QueryResult<ExplosiveV2> result = query(tenant, page);
      // 跳出循环
      if (result == null || CollectionUtils.isEmpty(result.getRecords())) {
        break;
      }

      for (ExplosiveV2 item : result.getRecords()) {
        ExplosivePrepareOnMsg msg = new ExplosivePrepareOnMsg();
        msg.setTenant(tenant);
        msg.setUuid(item.getUuid());
        if (ExplosiveV2.State.AUDITED.equals(item.getState())) {
          msg.setAction(ExplosiveActionV2.ON);
        } else if (ExplosiveV2.State.ACTIVE.equals(item.getState())) {
          msg.setAction(ExplosiveActionV2.OFF);
        } else {
          // nothing
          continue;
        }
        msg.setOperateInfo(getSysOperateInfo());
        msg.setTraceId(IdGenUtils.buildRdUuid());
        publisher.publishForNormal(ExplosivePrepareOnOffEvCallExecutor.EXECUTOR_ID, msg);
      }
      page++;
    }
  }

  private Set<String> getTenants() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ConfigItem.Queries.APP_ID, Cop.EQUALS, appId);
    QueryResult<ConfigItem> result = configClient.query(qd);
    if (result != null) {
      return result.getRecords().stream().map(i -> i.getTenant()).collect(Collectors.toSet());
    }
    return null;
  }

  protected OperateInfo getSysOperateInfo() {
    OperateInfo operateInfo = new OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("系统用户");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }
}
