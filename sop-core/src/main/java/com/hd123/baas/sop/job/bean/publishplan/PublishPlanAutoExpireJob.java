package com.hd123.baas.sop.job.bean.publishplan;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.SkuPublishPlanConfig;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlan;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanState;
import com.hd123.baas.sop.service.impl.sku.publishplan.SkuPublishPlanServiceImpl;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.config.api.entity.ConfigItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 上下架失效Job
 *
 * @author liuhaoxin
 * @date 2021-11-30
 */
@Slf4j
@Component
public class PublishPlanAutoExpireJob implements Job {
  private static final ThreadLocal<DateFormat> SDF = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

  @Value("${sop-service.appId}")
  private String appId;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private SkuPublishPlanServiceImpl skuPublishPlanService;

  @Override
  @Tx
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("执行上下架方案自动下架job");
    Set<String> tenants;
    try {
      tenants = getTenants();
      if (Objects.isNull(tenants)) {
        return;
      }
      for (String tenant : tenants) {
        autoExpire(tenant);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void autoExpire(String tenant) {
    List<SkuPublishPlan> list = skuPublishPlanService.listByStates(tenant, SkuPublishPlanState.SUBMITTED,
        SkuPublishPlanState.INIT, SkuPublishPlanState.CANCELED);
    // 过滤出当前日期大于生效日期的上下架方案
    list = list.stream()
//        .filter(i -> Objects.requireNonNull(DateUtil.toDate(SDF.get().format(new Date()))).after(i.getEffectiveDate()))
        .filter(i -> new Date().compareTo(i.getEffectiveEndDate()) > 0)
        .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(list)) {
      return;
    }
    // 一个叫货组织只能有一个上架方案，上架数量 根据组织ID确定 不会很多
    List<String> uuids = list.stream().map(SkuPublishPlan::getUuid).collect(Collectors.toList());
    skuPublishPlanService.expire(tenant, uuids, SopUtils.getSysOperateInfo());
  }

  /**
   * 获取所有租户
   *
   * @return 租户列表
   */
  private Set<String> getTenants() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ConfigItem.Queries.KEY, Cop.EQUALS, SkuPublishPlanConfig.SKU_PUBLISH_PLAN_ENABLED);
    qd.addByField(ConfigItem.Queries.APP_ID, Cop.EQUALS, appId);
    QueryResult<ConfigItem> result = configClient.query(qd);
    if (result != null) {
      return result.getRecords()
          .stream()
          .filter(i -> i.getValue().equals(Boolean.TRUE.toString()))
          .map(ConfigItem::getTenant)
          .collect(Collectors.toSet());
    }
    return null;
  }
}
