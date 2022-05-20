package com.hd123.baas.sop.job.bean.subsidy;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.SubsidyPlanConfig;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanService;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanState;
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
public class SubsidyPlanExpireJob implements Job {

  @Value("${sop-service.appId}")
  private String appId;

  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private SubsidyPlanService subsidyPlanService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("执行补贴计划失效job");
    Set<String> tenants;
    try {
      tenants = getTenants();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    try {
      for (String tenant : tenants) {
        expireSubsidyPlan(tenant);
      }
    } catch (Exception e) {
      log.error("执行补贴计划失效job：{}" + JsonUtil.objectToJson(e));
    }
  }

  private void expireSubsidyPlan(String tenant) {
    subsidyPlanService.updateExpireSubsidyPlanByDate(tenant, getCurrentDate(), SubsidyPlanState.PUBLISHED.name());
  }

  private Date getCurrentDate() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
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
}
