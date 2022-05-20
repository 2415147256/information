package com.hd123.baas.sop.evcall.exector.subsidyplan;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotion;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionService;
import com.hd123.baas.sop.service.api.subsidyplan.ActivityState;
import com.hd123.baas.sop.service.api.subsidyplan.ActivityType;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlan;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanActivityAssoc;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanState;
import com.hd123.baas.sop.service.dao.subsidyplan.ActivityAssocsDaoBof;
import com.hd123.baas.sop.service.dao.subsidyplan.SubsidyPlanDaoBof;
import com.hd123.baas.sop.service.impl.subsidyplan.ActivityStateConvert;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
import com.hd123.baas.sop.service.api.activity.PromActivityService;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.subsidyplan.StorePromPlanRelate;
import com.hd123.baas.sop.remote.rsmkhpms.RsMkhPmsClient;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BBasePricePromRes;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuhaoxin
 */
@Component
@Slf4j
public class ActivityRelationEvCallExecutor extends AbstractEvCallExecutor<ActivityRelationEvCallMsg> {
  public static final String ACTIVITY_RELATION_EXECUTOR_ID = ActivityRelationEvCallExecutor.class
      .getSimpleName();

  @Autowired
  private SubsidyPlanDaoBof subsidyPlanDao;
  @Autowired
  private ActivityAssocsDaoBof activityAssocsDao;

  @Autowired
  private PromActivityService promActivityService;
  @Autowired
  private PricePromotionService promotionService;

  @Autowired
  private FeignClientMgr feignClientMgr;

  @Autowired
  private RsMkhPmsClient rsMkhPmsClient;

  @Override
  @Tx
  protected void doExecute(ActivityRelationEvCallMsg message, EvCallExecutionContext context)
      throws Exception {
    // 推送关联关系
    ActivityType activityType = message.getActivityType();
    String tenant = message.getTenant();
    String activityId = message.getActivityId();

    if (ActivityType.PRICE_PROMOTION.equals(activityType)) {
      PricePromotion pricePromotion = promotionService.get(tenant, activityId, PricePromotion.FETCH_SHOP);
      ActivityState activityState = ActivityStateConvert.getActivityState(activityType,
          pricePromotion.getState().name());
      if (ActivityState.CANCELED.equals(activityState)) {
        log.info("活动作废无需关联！");
        return;
      }
      if (!ActivityState.AUDITED.equals(activityState)) {
        throw new BaasException("促销单不在审核状态，无法关联");
      }
      String shop = pricePromotion.getShops().get(0).getShop();
      List<SubsidyPlan> subsidyPlans = subsidyPlanDao.listByShop(tenant, pricePromotion.getOrgId(), shop, SubsidyPlanState.INIT.name(),
          SubsidyPlanState.PUBLISHED.name());
      SubsidyPlan subsidyPlan = subsidyPlans.stream()
          .filter(o -> checkIncludeTime(o.getEffectiveStartTime(), o.getEffectiveEndTime(),
              pricePromotion.getEffectiveStartDate(), pricePromotion.getEffectiveEndDate()))
          .findFirst()
          .orElse(null);
      if (Objects.isNull(subsidyPlan)) {
        log.info("补贴计划不存在");
        return;
      }
      List<SubsidyPlanActivityAssoc> assocs = activityAssocsDao.list(tenant, subsidyPlan.getUuid());
      boolean matchResult = assocs.stream().anyMatch(s -> s.getActivityId().equals(activityId));
      if (matchResult) {
        log.info("活动<{}>已关联", activityId);
        return;
      }
      SubsidyPlanActivityAssoc activityAssoc = new SubsidyPlanActivityAssoc();
      activityAssoc.setActivityType(activityType);
      activityAssoc.setActivityId(activityId);
      activityAssoc.setActivityStartTime(pricePromotion.getEffectiveStartDate());
      activityAssoc.setActivityEndTime(pricePromotion.getEffectiveEndDate());
      activityAssoc.setOwner(subsidyPlan.getUuid());
      activityAssoc.setTenant(tenant);
      activityAssoc.setUuid(UUID.randomUUID().toString());

      StorePromPlanRelate storePromPlanRelate = buildStorePromPlanRelate(message, subsidyPlan);
      RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(message.getTenant(), null, RsH6SOPClient.class);
      BaasResponse<Void> response = rsH6SOPClient.uploadOne(message.getTenant(), storePromPlanRelate);
      if (!response.isSuccess()) {
        throw new BaasException("H6关联活动下发失败");
      }
      activityAssocsDao.saveNew(tenant, activityAssoc);
    } else if (ActivityType.PRICE_PROMOTION_MODEL.equals(activityType)) {
      BaasResponse<BBasePricePromRes> response = rsMkhPmsClient.get(tenant, activityId);
      if (!response.isSuccess()) {
        throw new Exception(response.getMsg());
      }
      BBasePricePromRes pricePromRes = response.getData();
      String shop = pricePromRes.getShops().get(0).getUuid();
      List<SubsidyPlan> subsidyPlans = subsidyPlanDao.listByShop(tenant, pricePromRes.getOrgId(), shop, SubsidyPlanState.INIT.name(),
          SubsidyPlanState.PUBLISHED.name());

      SubsidyPlan subsidyPlan = subsidyPlans.stream()
          .filter(o -> checkIncludeTime(o.getEffectiveStartTime(), o.getEffectiveEndTime(),
              pricePromRes.getEffectiveStartDate(), pricePromRes.getEffectiveEndDate()))
          .findFirst()
          .orElse(null);
      if (Objects.isNull(subsidyPlan)) {
        log.info("补贴计划不存在");
        return;
      }
      List<SubsidyPlanActivityAssoc> assocs = activityAssocsDao.list(tenant, subsidyPlan.getUuid());
      boolean matchResult = assocs.stream().anyMatch(s -> s.getActivityId().equals(activityId));
      if (matchResult) {
        log.info("活动<{}>已关联", activityId);
        return;
      }
      SubsidyPlanActivityAssoc activityAssoc = new SubsidyPlanActivityAssoc();
      activityAssoc.setActivityType(activityType);
      activityAssoc.setActivityId(activityId);
      activityAssoc.setActivityStartTime(pricePromRes.getEffectiveStartDate());
      activityAssoc.setActivityEndTime(pricePromRes.getEffectiveEndDate());
      activityAssoc.setOwner(subsidyPlan.getUuid());
      activityAssoc.setTenant(tenant);
      activityAssoc.setUuid(UUID.randomUUID().toString());

      StorePromPlanRelate storePromPlanRelate = buildStorePromPlanRelate(message, subsidyPlan);

      RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(message.getTenant(), null, RsH6SOPClient.class);
      BaasResponse<Void> uploadResponse = rsH6SOPClient.uploadOne(message.getTenant(), storePromPlanRelate);
      if (!uploadResponse.isSuccess()) {
        throw new BaasException(uploadResponse.getMsg());
      }
      activityAssocsDao.saveNew(tenant, activityAssoc);
    } else if (activityType.equals(ActivityType.PROMOTE_ACTIVITY)) {
      PromActivity promActivity = promActivityService.get(tenant, activityId, PromActivity.PARTS_JOIN_UNITS);

        String shop = promActivity.getJoinUnits().getStores().get(0).getUuid();
      List<SubsidyPlan> subsidyPlans = subsidyPlanDao.listByShop(tenant, promActivity.getOrgId(), shop, SubsidyPlanState.INIT.name(),
          SubsidyPlanState.PUBLISHED.name());

      SubsidyPlan subsidyPlan = subsidyPlans.stream()
          .filter(o -> checkIncludeTime(o.getEffectiveStartTime(), o.getEffectiveEndTime(),
              promActivity.getDateRangeCondition().getDateRange().getBeginDate(),
              promActivity.getDateRangeCondition().getDateRange().getEndDate()))
          .findFirst()
          .orElse(null);
      if (Objects.isNull(subsidyPlan)) {
        log.info("补贴计划不存在");
        return;
      }
      List<SubsidyPlanActivityAssoc> assocs = activityAssocsDao.list(tenant, subsidyPlan.getUuid());
      boolean matchResult = assocs.stream().anyMatch(s -> s.getActivityId().equals(activityId));
      if (matchResult) {
        log.info("活动<{}>已关联", activityId);
        return;
      }
      SubsidyPlanActivityAssoc activityAssoc = new SubsidyPlanActivityAssoc();
      activityAssoc.setActivityType(activityType);
      activityAssoc.setActivityId(activityId);
      activityAssoc.setActivityStartTime(promActivity.getDateRangeCondition().getDateRange().getBeginDate());
      activityAssoc.setActivityEndTime(promActivity.getDateRangeCondition().getDateRange().getEndDate());
      activityAssoc.setOwner(subsidyPlan.getUuid());
      activityAssoc.setTenant(tenant);
      activityAssoc.setUuid(UUID.randomUUID().toString());
      StorePromPlanRelate storePromPlanRelate = buildStorePromPlanRelate(message, subsidyPlan);
      RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(message.getTenant(), null, RsH6SOPClient.class);
      BaasResponse<Void> response = rsH6SOPClient.uploadOne(message.getTenant(), storePromPlanRelate);
      if (!response.isSuccess()) {
        throw new BaasException(response.getMsg());
      }
      activityAssocsDao.saveNew(tenant, activityAssoc);
    }
  }

  private StorePromPlanRelate buildStorePromPlanRelate(ActivityRelationEvCallMsg message, SubsidyPlan subsidyPlan) {
    StorePromPlanRelate relate = new StorePromPlanRelate();
    relate.setPlanId(subsidyPlan.getUuid());
    relate.setPromType(message.getActivityType().name());
    relate.setPromId(message.getActivityId());
    return relate;
  }

  private boolean checkIncludeTime(Date planStart, Date planEnd, Date compareStart, Date compareEnd) {
    // 生效时间包含关系
    return !(planStart.after(compareStart) || planEnd.before(compareEnd));
  }

  @Override
  protected ActivityRelationEvCallMsg decodeMessage(String arg) throws BaasException {
    log.info("推送关联关系");
    return JsonUtil.jsonToObject(arg, ActivityRelationEvCallMsg.class);
  }
}
