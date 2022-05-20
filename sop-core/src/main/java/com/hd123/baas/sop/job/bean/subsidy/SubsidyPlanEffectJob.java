package com.hd123.baas.sop.job.bean.subsidy;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.SubsidyPlanConfig;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlan;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanService;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanState;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.config.api.entity.ConfigItem;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuhaoxin
 */
@Slf4j
@Component
public class SubsidyPlanEffectJob implements Job {

  @Value("${sop-service.appId}")
  private String appId;

  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private SubsidyPlanService subsidyPlanService;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @Tx
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("执行补贴计划生效job");
    Set<String> tenants;
    try {
      tenants = getTenants();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    try {
      for (String tenant : tenants) {
        effectSubsidyPlan(tenant);
      }
    } catch (Exception e) {
      log.error("执行补贴计划生效job：{}" + JsonUtil.objectToJson(e));
    }
  }

  private void effectSubsidyPlan(String tenant) {
    List<SubsidyPlan> subsidyPlans = subsidyPlanService.listByEffectiveDateScope(tenant, new Date(),
        SubsidyPlanState.INIT.name());
    List<String> uuids = subsidyPlans.stream().map(SubsidyPlan::getUuid).collect(Collectors.toList());
    subsidyPlanService.effect(tenant, uuids, getSysOperateInfo());
  }


  /**
   * 获取所有租户
   *
   * @return 租户列表
   */
  private Set<String> getTenants() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ConfigItem.Queries.KEY, Cop.EQUALS, SubsidyPlanConfig.SUBSIDY_PLAN_ENABLED);
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
