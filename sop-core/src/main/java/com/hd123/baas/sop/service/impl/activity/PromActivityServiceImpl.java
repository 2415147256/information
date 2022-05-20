package com.hd123.baas.sop.service.impl.activity;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.utils.OrgUtils;
import com.hd123.baas.sop.service.api.subsidyplan.ActivityType;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanService;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.annotation.PmsTx;
import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
import com.hd123.baas.sop.service.api.activity.event.PromActivityAuditEvent;
import com.hd123.baas.sop.service.api.activity.event.PromActivityCancelEvent;
import com.hd123.baas.sop.service.api.activity.event.PromActivityStopEvent;
import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.api.pms.rule.event.PromRuleCreateEvent;
import com.hd123.baas.sop.service.api.pms.rule.event.PromRuleStopEvent;
import com.hd123.baas.sop.service.api.pms.template.PromTemplate;
import com.hd123.baas.sop.service.dao.activity.PromActivityDao;
import com.hd123.baas.sop.service.dao.rule.PromRuleDao;
import com.hd123.baas.sop.service.dao.template.PromTemplateDao;
import com.hd123.baas.sop.service.impl.pomdata.PromotionBillGenerateFactory;
import com.hd123.baas.sop.service.api.promotion.FavorSharing;
import com.hd123.baas.sop.service.api.promotion.Promotion;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.service.api.promotion.PromotionType;
import com.hd123.baas.sop.service.api.activity.PromActivityService;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.Converter;
import com.hd123.rumba.commons.util.converter.ConverterBuilder;
import com.hd123.rumba.snowflake.Iid;
import com.hd123.spms.commons.calendar.DateRange;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author maodapeng
 * @Since
 */
@Service
@Slf4j
public class PromActivityServiceImpl implements PromActivityService {
  @Autowired
  private PromActivityDao promActivityDao;
  @Autowired
  private ApplicationEventPublisher publisher;
  @Autowired
  private PromRuleDao promRuleDao;
  @Autowired
  private PromTemplateDao promTemplateDao;
  @Autowired
  private SubsidyPlanService subsidyPlanService;
  @Autowired
  private StoreService storeService;
  @Autowired
  private PromotionBillGenerateFactory billGenerateFactory;

  private static final Converter<PromActivity, PromActivity> ACTIVITY_CONVERTER = ConverterBuilder
      .newBuilder(PromActivity.class, PromActivity.class)
      .build();

  @Override
  public PromActivity get(String tenant, String uuid, String... parts) {
    return promActivityDao.get(tenant, uuid, parts);
  }

  @Override
  public List<PromActivity> list(String tenant, Collection<String> uuids, String... fetchParts) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(uuids, "uuids");
    return promActivityDao.list(tenant, uuids, fetchParts);
  }

  @Override
  @PmsTx
  public void stopped(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    PromActivity target = promActivityDao.get(tenant, uuid, PromActivity.PARTS_PROMOTION);
    if (target == null) {
      throw new BaasException("指定的促销活动不存在或不可用");
    }

    DateRange dateRange = target.getDateRangeCondition().getDateRange();
    if (target.getState().equals(PromActivity.State.audited) == false && dateRange.include(new Date()) == false) {
      throw new BaasException("指定的促销活动当前未上架，不能终止");
    }
    target.setState(PromActivity.State.stopped);
    target.setLastModifyInfo(operateInfo);
    promActivityDao.update(target);
    stopRules(target);
    publisher.publishEvent(new PromActivityStopEvent(target));
  }

  @Override
  @PmsTx
  public void cancel(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    PromActivity target = promActivityDao.get(tenant, uuid, PromActivity.PARTS_PROMOTION);
    if (target == null) {
      throw new BaasException("指定的促销活动不存在或不可用");
    }
    if (target.getState().equals(PromActivity.State.audited) == false) {
      throw new BaasException("指定的促销活动不是已审核状态，无法作废");
    }
    if (target.getDateRangeCondition().getDateRange().include(new Date())) {
      throw new BaasException("指定的促销活动已上架，无法作废");
    }
    target.setState(PromActivity.State.canceled);
    target.setLastModifyInfo(operateInfo);
    promActivityDao.update(target);
    stopRules(target);
    publisher.publishEvent(new PromActivityCancelEvent(target));
  }

  @Override
  @PmsTx
  public void audit(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    PromActivity target = promActivityDao.get(tenant, uuid, PromActivity.ALL_PARTS);
    if (target == null) {
      throw new BaasException("指定的促销活动不存在或不可用");
    }
    if (target.getState() != PromActivity.State.initial) {
      throw new BaasException("指定的促销活动不是未审核状态，无法审核");
    }
    target.setState(PromActivity.State.canceled);
    target.setLastModifyInfo(operateInfo);
    promActivityDao.update(target);
    splitActivities(tenant, target);
  }

  @Override
  public void autoAudit(String tenant, String uuid, OperateInfo operateInfo) {
    PromActivity target = promActivityDao.get(tenant, uuid, PromActivity.ALL_PARTS);
    List<FavorSharing> favorSharings = target.getFavorSharings();
    FavorSharing storeFavorSharing = favorSharings.stream()
        .filter(s -> s.getTargetUnit().getCode().equalsIgnoreCase(FavorSharing.TypeEnum.STORE.name()))
        .findFirst()
        .orElse(null);
    FavorSharing superFavorSharing = favorSharings.stream()
        .filter(s -> s.getTargetUnit().getCode().equalsIgnoreCase(FavorSharing.TypeEnum.SUPER.name()))
        .findFirst()
        .orElse(null);
    BigDecimal storeRate = storeFavorSharing == null ? BigDecimal.ZERO : storeFavorSharing.getRate();
    BigDecimal superRate = superFavorSharing == null ? BigDecimal.ZERO : superFavorSharing.getRate();
    if (storeRate.add(superRate).compareTo(BigDecimal.ONE) == 0) {
      try {
        audit(tenant, uuid, operateInfo);
      } catch (Exception e) {
        log.info("促销活动uuid={}无法自动审核，失败原因：{}", uuid, com.hd123.baas.sop.utils.JsonUtil.objectToJson(e));
      }
    }
  }

  @Override
  public List<PromActivity> query(String tenant, String shopCode, Date effectDate) {
    List<PromActivity> promActivities = promActivityDao.listByShopCodeAndEffectDate(tenant, shopCode, effectDate,
        PromActivity.ALL_PARTS);
    return promActivities;
  }

  /**
   * 终止促销规则
   *
   * @param promActivity
   *          促销活动
   */
  private void stopRules(PromActivity promActivity) {
    promRuleDao.updateByActivity(promActivity.getTenant(), promActivity.getUuid(), PromRule.State.stopped);
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PromRule.Queries.TENANT, Cop.EQUALS, promActivity.getTenant());
    qd.addByField(PromRule.Queries.ACTIVITY_UUID, Cop.EQUALS, promActivity.getUuid());
    QueryResult<PromRule> promRules = promRuleDao.query(promActivity.getTenant(), qd,
        Arrays.asList(PromRule.ALL_PARTS));
    if (CollectionUtils.isEmpty(promRules.getRecords())) {
      return;
    }
    // 循环终止
    for (PromRule promRule : promRules.getRecords()) {
      billGenerateFactory.abort(promRule.getTenant(), promRule.getUuid(), promRule.getLastModifyInfo().getTime());
      publisher.publishEvent(new PromRuleStopEvent(promRule));
    }
  }

  private void splitActivities(String tenant, PromActivity activity) throws BaasException {
    List<PromotionJoinUnits.JoinUnit> joinUnits = activity.getJoinUnits().getStores();
    if (activity.getJoinUnits().getAllUnit()) {
      joinUnits = queryAllUnits(activity.getTenant(), activity.getOrgId());
    }
    if (joinUnits == null) {
      return;
    }
    // 1.将活动按照门店进行拆分
    List<PromActivity> newActivities = new ArrayList<>();

    for (PromotionJoinUnits.JoinUnit joinUnit : joinUnits) {
      PromActivity newActivity = ACTIVITY_CONVERTER.convert(activity);
      newActivity.setUuid(UUID.randomUUID().toString());
      newActivity.setOrgId(activity.getOrgId());
      newActivity.setBillNumber(String.valueOf(Iid.next()));
      newActivity.setState(PromActivity.State.audited);
      newActivity.setMarketReason(activity.getMarketReason());
      PromotionJoinUnits promotionJoinUnits = new PromotionJoinUnits();
      promotionJoinUnits.setAllUnit(false);
      promotionJoinUnits.setStores(Lists.newArrayList(joinUnit));
      newActivity.setJoinUnits(promotionJoinUnits);
      newActivities.add(newActivity);
    }
    if (CollectionUtils.isNotEmpty(activity.getFavorSharings())){
      // 存在督导费用承担 关联补贴计划
      FavorSharing superFavorSharing = activity.getFavorSharings()
          .stream()
          .filter(s -> s.getTargetUnit().getCode().equals(FavorSharing.TypeEnum.SUPER.name()))
          .findFirst()
          .orElse(null);
      if (superFavorSharing != null && superFavorSharing.getRate().compareTo(BigDecimal.ZERO) > 0) {
        // 2.补贴计划关联
        relationPlan(tenant, activity, newActivities);
      }
    }

    // 3.保存拆分的补贴计划
    promActivityDao.batchSaveNew(newActivities);

    // 4.生成促销规则
    for (PromActivity newActivity : newActivities) {
      createdRules(newActivity);
      publisher.publishEvent(new PromActivityAuditEvent(newActivity));
    }
  }

  /**
   * 检验计划关联的门店是否存在补贴计划，若存在则进行补贴计划关联
   *
   * @param tenant
   * @param originActivity
   * @param newActivities
   * @throws BaasException
   */
  private void relationPlan(String tenant, PromActivity originActivity, List<PromActivity> newActivities)
      throws BaasException {
    List<PromotionJoinUnits.JoinUnit> joinUnits = newActivities.stream()
        .flatMap(s -> s.getJoinUnits().getStores().stream())
        .collect(Collectors.toList());
    List<String> shops = joinUnits.stream().map(PromotionJoinUnits.JoinUnit::getUuid).collect(Collectors.toList());
    Date beginDate = originActivity.getDateRangeCondition().getDateRange().getBeginDate();
    Date endDate = originActivity.getDateRangeCondition().getDateRange().getEndDate();
    if (OrgUtils.isNotAllScope(tenant, originActivity.getOrgId())) {
      subsidyPlanService.checkShopExistPlan(tenant, originActivity.getOrgId(), shops, beginDate, endDate);
    } else {
      subsidyPlanService.checkShopExistPlan(tenant, shops, beginDate, endDate);
    }

    List<String> activityIds = newActivities.stream().map(PromActivity::getUuid).collect(Collectors.toList());
    subsidyPlanService.relation(tenant, ActivityType.PROMOTE_ACTIVITY, activityIds);
  }

  private List<PromotionJoinUnits.JoinUnit> queryAllUnits(String tenant, String orgId) throws BaasException {
    StoreFilter filter = new StoreFilter();
    filter.setOrgIdEq(DefaultOrgIdConvert.toMasDefOrgId(orgId));
    return storeService.query(tenant, filter)
        .getRecords()
        .stream()
        .map(store -> new PromotionJoinUnits.JoinUnit(store.getId(), store.getCode(), store.getName()))
        .collect(Collectors.toList());
  }

  /**
   * 生成促销规则
   *
   * @param promActivity
   *          促销活动
   */
  private void createdRules(PromActivity promActivity) throws BaasException {
    if (CollectionUtils.isEmpty(promActivity.getPromotions())) {
      return;
    }
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PromTemplate.Queries.TENANT, Cop.EQUALS, promActivity.getTenant());
    qd.addByField(PromTemplate.Queries.PREDEFINE, Cop.EQUALS, Boolean.TRUE);
    List<PromTemplate> templates = promTemplateDao.query(promActivity.getTenant(), qd).getRecords();
    if (CollectionUtils.isEmpty(templates)) {
      throw new BaasException("未初始化预定义模板");
    }
    Map<PromotionType, PromTemplate> templateMap = templates.stream()
        .collect(Collectors.toMap(p -> p.getPromotion().getPromotionType(), o -> o));
    String orgId = promActivity.getOrgId();
    // 循环创建
    for (Promotion promotion : promActivity.getPromotions()) {
      PromRule promRule = new PromRule();
      promRule.setTenant(promActivity.getTenant());
      promRule.setOrgId(orgId);
      promRule.setStarterOrgUuid("-");
      promRule.setName(promActivity.getName() + "-" + promotion.getPromotionType().getCaption());

      PromTemplate promTemplate = templateMap.get(promotion.getPromotionType());
      promRule.setTemplate(UCN.newInstance(promTemplate));
      promRule.setState(PromRule.State.effect);
      promRule.setBillNumber(String.valueOf(Iid.next()));
      promRule.setActivity(new UCN(promActivity.getUuid(), promActivity.getBillNumber(), promActivity.getName()));
      promRule.setJoinUnits(promActivity.getJoinUnits());
      promRule.setOnlyMember(promActivity.getOnlyMember());
      promRule.setPromChannels(promActivity.getPromChannels());
      promRule.setDateRangeCondition(promActivity.getDateRangeCondition());
      promRule.setTimePeriodCondition(promActivity.getTimePeriodCondition());
      promRule.setPromotion(promotion);
      promRule.setPromNote(promActivity.getPromNote());
      promRule.setFavorSharings(promActivity.getFavorSharings());
      // OperateInfo operateInfo = new OperateInfo(new Operator("System", "系统自动创建"));
      OperateInfo operateInfo = promActivity.getCreateInfo();
      promRule.setCreateInfo(operateInfo);
      promRule.setLastModifyInfo(operateInfo);
      promRule.setMarketReason(promActivity.getMarketReason());
      promRuleDao.create(promRule);
      publisher.publishEvent(new PromRuleCreateEvent(promRule));
    }
  }

}
