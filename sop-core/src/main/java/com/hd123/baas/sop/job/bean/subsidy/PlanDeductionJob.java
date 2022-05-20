package com.hd123.baas.sop.job.bean.subsidy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.hd123.baas.sop.service.api.subsidyplan.DeductionType;
import com.hd123.baas.sop.utils.CommonsUtilsV2;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.SubsidyPlanConfig;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotion;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionService;
import com.hd123.baas.sop.service.api.subsidyplan.ActivityType;
import com.hd123.baas.sop.service.api.subsidyplan.DeductionRecord;
import com.hd123.baas.sop.service.api.subsidyplan.DeductionState;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlan;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanActivityAssoc;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanService;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanState;
import com.hd123.baas.sop.service.api.subsidyplan.TerminateType;
import com.hd123.baas.sop.service.dao.subsidyplan.ActivityAssocsDaoBof;
import com.hd123.baas.sop.service.dao.subsidyplan.DeductionRecordDaoBof;
import com.hd123.baas.sop.service.dao.subsidyplan.SubsidyPlanDaoBof;
import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
import com.hd123.baas.sop.service.api.activity.PromActivityService;
import com.hd123.baas.sop.remote.rsmkhpms.RsMkhPmsClient;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BBasePricePromListReq;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BBasePricePromRes;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import com.qianfan123.baas.config.api.entity.ConfigItem;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuhaoxin
 */
@Slf4j
@Component
public class PlanDeductionJob implements Job {
  @Value("${sop-service.appId}")
  private String appId;

  @Autowired
  private BaasConfigClient configClient;

  @Autowired
  private DeductionRecordDaoBof deductionRecordDao;

  @Autowired
  private SubsidyPlanDaoBof subsidyPlanDao;
  @Autowired
  private ActivityAssocsDaoBof activityAssocsDao;

  @Autowired
  private SubsidyPlanService subsidyPlanService;

  @Autowired
  private RsMkhPmsClient rsMkhPmsClient;
  @Autowired
  private PromActivityService promActivityService;
  @Autowired
  private PricePromotionService promotionService;

  public static final String PRE_DISCOUNT_ACTIVITY_ID = "PRE_DISCOUNT";

  @Override
  @Tx
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    long sts = System.currentTimeMillis();
    log.info("执行补贴计划扣款job");
    Set<String> tenants;
    try {
      tenants = getTenants();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    try {
      for (String tenant : tenants) {
        planDeduction(tenant);
      }
    } catch (Exception e) {
      log.error("执行补贴计划扣款job错误：{}" + JsonUtil.objectToJson(e));
      throw new RuntimeException(e);
    }
    //记录执行耗时日志
    CommonsUtilsV2.outTs("PlanDeductionJob#execute", sts);
  }

  private void planDeduction(String tenant) throws BaasException {
    List<DeductionRecord> deductionRecords = deductionRecordDao.listByState(tenant, DeductionState.PREPARE);
    if (CollectionUtils.isEmpty(deductionRecords)) {
      log.info("不存在补贴计划扣款记录！");
      return;
    }
    // 营销活动 价格促销、价格促销模型信息补全
    setActivityDetail(tenant, deductionRecords);
    // 计划全额扣款
    List<String> owners = deductionRecords.stream()
        .distinct()
        .map(DeductionRecord::getOwner)
        .collect(Collectors.toList());
    List<SubsidyPlan> subsidyPlans = subsidyPlanDao.listByUuids(tenant, owners);
    Map<String, List<DeductionRecord>> planDeductionMap = deductionRecords.stream()
        .collect(Collectors.groupingBy(DeductionRecord::getOwner));

    List<DeductionRecord> successDeductions = new ArrayList<>();
    for (SubsidyPlan subsidyPlan : subsidyPlans) {
      BigDecimal usedAmount = subsidyPlan.getUsedAmount();
      BigDecimal preQuota = subsidyPlan.getPreQuota();
      List<DeductionRecord> deductionInfos = planDeductionMap.get(subsidyPlan.getUuid());
      for (DeductionRecord deductionInfo : deductionInfos) {
        if (!deductionInfo.getType().equals(DeductionType.PRE_ACTIVITY_DISCOUNT)) {
          usedAmount = usedAmount.subtract(deductionInfo.getAmount());
        } else {
          preQuota = preQuota.subtract(deductionInfo.getAmount());
        }
        deductionInfo.setState(DeductionState.SUCCESS);
        successDeductions.add(deductionInfo);
      }
      subsidyPlan.setUsedAmount(usedAmount);
      subsidyPlan.setPreQuota(preQuota);
    }
    subsidyPlanDao.batchUpdate(tenant, subsidyPlans);
    // 扣款额度为负数终止补贴计划
    for (SubsidyPlan subsidyPlan : subsidyPlans) {
      if (!SubsidyPlanState.EXCEPTION.name().equals(subsidyPlan.getState())
          && !SubsidyPlanState.EXPIRED.name().equals(subsidyPlan.getState())
          && subsidyPlan.getAmount().compareTo(subsidyPlan.getUsedAmount().add(subsidyPlan.getPreQuota())) <= 0
          && subsidyPlan.getPreQuota().compareTo(BigDecimal.ZERO) == 0) {
        subsidyPlanService.terminate(tenant, subsidyPlan.getUuid(), TerminateType.SUBSIDY_ACTIVITY,
            SopUtils.getSysOperateInfo());
      }
    }
    deductionRecordDao.batchUpdate(tenant, successDeductions, SopUtils.getSysOperateInfo());

    // 异常扣款，如果活动没有关联异常，需要重新关联异常
    List<SubsidyPlan> exceptPlans = subsidyPlans.stream()
        .filter(o -> SubsidyPlanState.EXCEPTION.name().equals(o.getState()))
        .collect(Collectors.toList());
    for (SubsidyPlan exceptPlan : exceptPlans) {
      List<SubsidyPlanActivityAssoc> exceptPlanAssocs = activityAssocsDao.list(tenant, exceptPlan.getUuid());
      List<String> activityIds = exceptPlanAssocs.stream()
          .map(SubsidyPlanActivityAssoc::getActivityId)
          .collect(Collectors.toList());
      List<DeductionRecord> exceptDeductions = successDeductions.stream().filter(o -> {
        if (exceptPlan.getUuid().equals(o.getOwner()) && !activityIds.contains(o.getActivityId()) && !PRE_DISCOUNT_ACTIVITY_ID.equals(o.getActivityId())) {
          return true;
        }
        return false;
      }).collect(Collectors.toList());

      List<SubsidyPlanActivityAssoc> activityAssocs = new ArrayList<>();
      for (DeductionRecord exceptDeduction : exceptDeductions) {
        SubsidyPlanActivityAssoc planActivityAssoc = new SubsidyPlanActivityAssoc();
        planActivityAssoc.setOwner(exceptPlan.getUuid());
        planActivityAssoc.setActivityId(exceptDeduction.getActivityId());
        planActivityAssoc.setActivityType(exceptDeduction.getActivityType());
        planActivityAssoc.setActivityStartTime(exceptDeduction.getActivityStartTime());
        planActivityAssoc.setActivityEndTime(exceptDeduction.getActivityEndTime());
        activityAssocs.add(planActivityAssoc);
      }
      if (CollectionUtils.isNotEmpty(activityAssocs)) {
        activityAssocs = activityAssocs.stream().distinct().collect(Collectors.toList());
        for (SubsidyPlanActivityAssoc activityAssoc : activityAssocs) {
          activityAssoc.setUuid(UUID.randomUUID().toString());
        }
        activityAssocsDao.batchSave(tenant, activityAssocs);
      }
    }
  }

  private void setActivityDetail(String tenant, List<DeductionRecord> deductionRecords) throws BaasException {
    Map<ActivityType, List<DeductionRecord>> deductionsMap = deductionRecords.stream()
        .filter(i -> null!=i.getActivityId() && !PRE_DISCOUNT_ACTIVITY_ID.equals(i.getActivityId()))
        .collect(Collectors.groupingBy(DeductionRecord::getActivityType));

    List<DeductionRecord> promoteActivityDeductions = deductionsMap.get(ActivityType.PROMOTE_ACTIVITY);
    List<DeductionRecord> pricePromotionDeductions = deductionsMap.get(ActivityType.PRICE_PROMOTION);
    List<DeductionRecord> pricePromModeDeductions = deductionsMap.get(ActivityType.PRICE_PROMOTION_MODEL);

    Map<String, PromActivity> promActivityMap = null;
    Map<String, PricePromotion> pricePromotionMap = null;
    Map<String, BBasePricePromRes> pricePromResMap = null;
    if (CollectionUtils.isNotEmpty(promoteActivityDeductions)) {
      List<String> activityIds = getActivityIds(promoteActivityDeductions);
      List<PromActivity> promActivities = promActivityService.list(tenant, activityIds);
      promActivityMap = promActivities.stream().collect(Collectors.toMap(PromActivity::getUuid, o -> o));
    }
    if (CollectionUtils.isNotEmpty(pricePromotionDeductions)) {
      List<String> activityIds = getActivityIds(pricePromotionDeductions);
      List<PricePromotion> pricePromotions = promotionService.list(tenant, activityIds, PricePromotion.FETCH_ALL);
      pricePromotionMap = pricePromotions.stream().collect(Collectors.toMap(PricePromotion::getUuid, o -> o));
    }
    if (CollectionUtils.isNotEmpty(pricePromModeDeductions)) {
      List<String> activityIds = getActivityIds(pricePromModeDeductions);
      BBasePricePromListReq req = new BBasePricePromListReq();
      req.setUuids(activityIds);
      BaasResponse<List<BBasePricePromRes>> response;
      try {
        response = rsMkhPmsClient.list(tenant, req);
      } catch (BaasException e) {
        log.info("价格促销模型查询失败！");
        throw new BaasException("价格促销模型查询失败");
      }
      if (!response.isSuccess()) {
        log.info("价格促销模型查询失败");
      }
      List<BBasePricePromRes> basePricePromRes = response.getData();
      pricePromResMap = basePricePromRes.stream().collect(Collectors.toMap(BBasePricePromRes::getUuid, o -> o));
    }
    // 赋值活动信息
    for (DeductionRecord deductionRecord : deductionRecords) {
      if (ActivityType.PROMOTE_ACTIVITY.equals(deductionRecord.getActivityType())) {
        if (Objects.isNull(promActivityMap)) {
          continue;
        }
        PromActivity promActivity = promActivityMap.get(deductionRecord.getActivityId());
        if (Objects.isNull(promActivity)) {
          continue;
        }
        deductionRecord.setActivityName(promActivity.getName());
        deductionRecord.setActivityCreator(promActivity.getCreateInfo().getOperator().getId());
        deductionRecord.setActivityCreatorName(promActivity.getCreateInfo().getOperator().getFullName());
        deductionRecord.setActivityStartTime(promActivity.getDateRangeCondition().getDateRange().getBeginDate());
        deductionRecord.setActivityEndTime(promActivity.getDateRangeCondition().getDateRange().getEndDate());
      } else if (ActivityType.PRICE_PROMOTION_MODEL.equals(deductionRecord.getActivityType())) {
        if (Objects.isNull(pricePromResMap)) {
          continue;
        }
        BBasePricePromRes pricePromRes = pricePromResMap.get(deductionRecord.getActivityId());
        if (Objects.isNull(pricePromRes)) {
          continue;
        }
        deductionRecord.setActivityName(pricePromRes.getFlowNo());
        deductionRecord.setActivityCreator(pricePromRes.getCreateInfo().getOperator().getId());
        deductionRecord.setActivityCreatorName(pricePromRes.getCreateInfo().getOperator().getFullName());
        deductionRecord.setActivityStartTime(pricePromRes.getEffectiveStartDate());
        deductionRecord.setActivityEndTime(pricePromRes.getEffectiveEndDate());
      } else if (ActivityType.PRICE_PROMOTION.equals(deductionRecord.getActivityType())) {
        if (Objects.isNull(pricePromotionMap)) {
          continue;
        }
        PricePromotion pricePromotion = pricePromotionMap.get(deductionRecord.getActivityId());
        if (Objects.isNull(pricePromotion)) {
          continue;
        }
        deductionRecord.setActivityName(pricePromotion.getFlowNo());
        deductionRecord.setActivityCreator(pricePromotion.getCreateInfo().getOperator().getId());
        deductionRecord.setActivityCreatorName(pricePromotion.getCreateInfo().getOperator().getFullName());
        deductionRecord.setActivityStartTime(pricePromotion.getEffectiveStartDate());
        deductionRecord.setActivityEndTime(pricePromotion.getEffectiveEndDate());
      }

    }
  }

  List<String> getActivityIds(List<DeductionRecord> list) {
    return list.stream().map(DeductionRecord::getActivityId).collect(Collectors.toList());
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
