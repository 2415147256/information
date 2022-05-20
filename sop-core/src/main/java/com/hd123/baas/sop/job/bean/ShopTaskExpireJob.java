package com.hd123.baas.sop.job.bean;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.hd123.baas.sop.config.ShopTaskConfig;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.task.PlanSummary;
import com.hd123.baas.sop.service.api.task.ShopTaskSummaryService;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.shoptask.PlanAction;
import com.hd123.baas.sop.evcall.exector.shoptask.PlanStateChangeEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.shoptask.PlanStateChangeMsg;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.config.api.entity.ConfigItem;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author maodapeng
 * @Since
 */
@Slf4j
@Component
public class ShopTaskExpireJob implements Job {

  @Value("${sop-service.appId}")
  private String appId;

  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private ShopTaskSummaryService shopTaskSummaryService;
  @Autowired
  private BaasConfigClient configClient;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("执行门店任务过期job");
    Set<String> tenants;
    try {
      tenants = getTenants();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    for (String tenant : tenants) {
      expirePlan(tenant);
    }
  }

  public void expirePlan(String tenant) {
    List<PlanSummary> planSummaries = shopTaskSummaryService.listExpirePlan(tenant);
    if (CollectionUtils.isNotEmpty(planSummaries)) {
      for (PlanSummary record : planSummaries) {
        PlanStateChangeMsg msg = new PlanStateChangeMsg();
        msg.setTenant(tenant);
        msg.setPlanPeriodCode(record.getPeriodCode());
        msg.setOperateInfo(getSysOperateInfo());
        msg.setAction(PlanAction.EXPIRE);
        msg.setPlan(record.getPlan());
        msg.setTraceId(record.getPeriodCode());
        publisher.publishForNormal(PlanStateChangeEvCallExecutor.PLAN_STATE_CHANGE_EXECUTOR_ID, msg);
      }
    }
  }

  /**
   * 获取所有租户
   *
   * @return 租户列表
   */
  public Set<String> getTenants() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ConfigItem.Queries.KEY, Cop.EQUALS, ShopTaskConfig.SHOP_TASK_ENABLED);
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
