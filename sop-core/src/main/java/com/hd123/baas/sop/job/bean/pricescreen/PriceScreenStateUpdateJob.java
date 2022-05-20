package com.hd123.baas.sop.job.bean.pricescreen;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.baas.sop.utils.SopUtils;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.PriceScreenConfig;
import com.hd123.baas.sop.service.api.screen.PriceScreen;
import com.hd123.baas.sop.service.api.screen.PriceScreenService;
import com.hd123.baas.sop.service.api.screen.PriceScreenState;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.pricescreen.PriceScreenEffectedEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.pricescreen.PriceScreenEffectedMsg;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.config.api.entity.ConfigItem;

import lombok.extern.slf4j.Slf4j;

/**
 * 价格屏生状态更新JOB
 * 
 * @author liuhaoxin
 */
@Slf4j
@Component
public class PriceScreenStateUpdateJob implements Job {
  @Value("${sop-service.appId}")
  private String appId;

  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private PriceScreenService priceScreenService;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @Tx
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("执行价格屏状态更改Job");
    Set<String> tenants;
    Date currentDate = new Date();
    try {
      tenants = getTenants();
      if (tenants == null) {
        return;
      }
      for (String tenant : tenants) {
        effectPriceScreen(tenant, currentDate);
        expirePriceScreen(tenant, currentDate);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  private void effectPriceScreen(String tenant, Date date) {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PriceScreen.Queries.STATE, Cop.EQUALS, PriceScreenState.CONFIRMED.name());
    qd.addByField(PriceScreen.Queries.EFFECTIVE_START_TIME, Cop.LESS_OR_EQUALS, date);
    qd.addByField(PriceScreen.Queries.EFFECTIVE_END_TIME, Cop.GREATER_OR_EQUALS, date);
    QueryResult<PriceScreen> result = priceScreenService.query(tenant, qd);
    log.info("生效的价格屏方案：{}", JsonUtil.objectToJson(result.getRecords()));
    if (CollectionUtils.isNotEmpty(result.getRecords())) {
      priceScreenService.effect(tenant, date, SopUtils.getSysOperateInfo());
      result.getRecords().forEach(s -> sendPriceScreenEffectedMsg(tenant, s));
    }
  }

  private void expirePriceScreen(String tenant, Date date) {
    priceScreenService.expire(tenant, date, SopUtils.getSysOperateInfo());
  }

  private void sendPriceScreenEffectedMsg(String tenant, PriceScreen priceScreen) {
    PriceScreenEffectedMsg msg = new PriceScreenEffectedMsg();
    msg.setTenant(tenant);
    msg.setUuid(priceScreen.getUuid());
    msg.setTraceId(MDC.get("trace_id"));
    publisher.publishForNormal(PriceScreenEffectedEvCallExecutor.PRICE_SCREEN_EFFECTED_EXECUTOR_ID, msg);
  }

  /**
   * 获取开启配置的用户
   *
   * @return 租户列表
   */
  private Set<String> getTenants() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ConfigItem.Queries.KEY, Cop.EQUALS, PriceScreenConfig.PRICE_SCREEN_ENABLED);
    qd.addByField(ConfigItem.Queries.APP_ID, Cop.EQUALS, appId);
    QueryResult<ConfigItem> result = configClient.query(qd);
    if (result != null) {
      return result.getRecords()
          .stream()
          .filter(o -> "true".equals(o.getValue()))
          .map(ConfigItem::getTenant)
          .collect(Collectors.toSet());
    }
    return null;
  }
}
