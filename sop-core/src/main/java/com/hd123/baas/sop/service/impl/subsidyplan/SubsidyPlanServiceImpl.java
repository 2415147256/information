package com.hd123.baas.sop.service.impl.subsidyplan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.qianfan123.baas.common.util.JSONUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.config.SubsidyMessageConfig;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotion;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionService;
import com.hd123.baas.sop.service.api.subsidyplan.*;
import com.hd123.baas.sop.service.dao.subsidyplan.ActivityAssocsDaoBof;
import com.hd123.baas.sop.service.dao.subsidyplan.DeductionRecordDaoBof;
import com.hd123.baas.sop.service.dao.subsidyplan.SubsidyPlanDaoBof;
import com.hd123.baas.sop.service.dao.subsidyplan.SubsidyPlanLogDaoBof;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.subsidyplan.*;
import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
import com.hd123.baas.sop.service.api.activity.PromActivityService;
import com.hd123.baas.sop.redis.RedisService;
import com.hd123.baas.sop.remote.dingtalk.DingTalkLinkMsg;
import com.hd123.baas.sop.remote.rsmkhpms.RsMkhPmsClient;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BBasePricePromListReq;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BBasePricePromRes;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BasePricePromotion;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.mpa.api.common.ObjectNodeUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.Converter;
import com.hd123.rumba.commons.util.converter.ConverterBuilder;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuhaoxin
 */
@Slf4j
@Service
public class SubsidyPlanServiceImpl implements SubsidyPlanService {

  private static final ThreadLocal<DateFormat> SDF = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
  private final static Converter<SubsidyPlan, SubsidyPlan> CONVERTER = ConverterBuilder
      .newBuilder(SubsidyPlan.class, SubsidyPlan.class)
      .build();

  public static final int CHECK_EXCEPTION_CODE = 5000;

  @Autowired
  private SubsidyPlanDaoBof subsidyPlanDao;
  @Autowired
  private SubsidyPlanLogDaoBof subsidyPlanLogDao;
  @Autowired
  private DeductionRecordDaoBof deductionRecordDao;

  @Autowired
  private ActivityAssocsDaoBof activityAssocsDao;
  @Autowired
  private PromActivityService activityService;
  @Autowired
  private PricePromotionService promotionService;

  @Autowired
  private RedisService redisService;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private RsMkhPmsClient rsMkhPmsClient;

  @Autowired
  private StoreService storeService;

  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @Tx
  public void saveNew(String tenant, List<SubsidyPlan> subsidyPlans, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(subsidyPlans, "补贴计划列表");

    // 开店时间不存在提示
    List<ExceptionPlan> exceptionPlans = new ArrayList<>();
    List<SubsidyPlan> noExistHourPlan = subsidyPlans.stream()
        .filter(o -> Objects.isNull(o.getBusinessHour()))
        .collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(noExistHourPlan)) {
      for (SubsidyPlan subsidyPlan : noExistHourPlan) {
        ExceptionPlan bussinessHourExceptionPlan = buildExceptionByBussinessHour(subsidyPlan);
        exceptionPlans.add(bussinessHourExceptionPlan);
      }
    }
    // 去除不存在开店时间的计划
    subsidyPlans = subsidyPlans.stream().filter(o -> !Objects.isNull(o.getBusinessHour())).collect(Collectors.toList());
    for (SubsidyPlan subsidyPlan : subsidyPlans) {
      initSubsidy(tenant, subsidyPlan);
      if (subsidyPlan.getEffectiveStartTime().after(subsidyPlan.getEffectiveEndTime())) {
        throw new BaasException("开始时间不能小于结束时间!");
      }
    }
    // 时间没有选择未来时间
    for (SubsidyPlan subsidyPlan : subsidyPlans) {
      Date currentDate = getCurrentDate();
      if (currentDate.after(subsidyPlan.getEffectiveStartTime())) {
        ExceptionPlan bussinessHourExceptionPlan = buildExceptionByEffectiveTime(subsidyPlan);
        exceptionPlans.add(bussinessHourExceptionPlan);
      }
    }
    for (SubsidyPlan subsidyPlan : subsidyPlans) {
      Date currentDate = getCurrentDate();
      if(currentDate.compareTo(subsidyPlan.getEffectiveStartTime()) == 0 && getNoonDate().before(new Date())) {
        ExceptionPlan startEffectiveTimeExceptionPlan = buildExceptionByStartEffectiveTime(subsidyPlan);
        exceptionPlans.add(startEffectiveTimeExceptionPlan);
      }
    }
    // 通过门店查询计划列表,时间段冲突
    List<String> shops = subsidyPlans.stream().map(SubsidyPlan::getShop).distinct().collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(shops)) {
      String orgId = subsidyPlans.get(0).getOrgId();
      List<SubsidyPlan> existPlans = subsidyPlanDao.listByShops(tenant, orgId, shops, SubsidyPlanState.INIT.name(),
          SubsidyPlanState.PUBLISHED.name());
      if (CollectionUtils.isNotEmpty(existPlans)) {
        List<ExceptionPlan> conflictExceptionPlans = listConflictPlan(subsidyPlans, existPlans);
        exceptionPlans.addAll(conflictExceptionPlans);
      }
    }
    if (CollectionUtils.isNotEmpty(exceptionPlans)) {
      List<ExceptionPlan> shopException = new ArrayList<>(0);
      Map<String, ExceptionPlan> exceptionPlanMap = exceptionPlans.stream().collect(Collectors.toMap(o -> {
        StringBuilder sb = new StringBuilder();
        return sb.append(o.getShopCode()).append(o.getReason()).toString();
      }, o -> o));

      for (Map.Entry<String, ExceptionPlan> entry : exceptionPlanMap.entrySet()) {
        shopException.add(entry.getValue());
      }

      String redisKey = UUID.randomUUID().toString();
      redisService.set(redisKey, shopException, 30, TimeUnit.MINUTES);
      throw new BaasException(CHECK_EXCEPTION_CODE, redisKey);
    }

    subsidyPlanDao.batchSave(tenant, subsidyPlans, operateInfo);
    //如果计划活动的生效时间小于当前时间,更新计划状态
    for (SubsidyPlan subsidyPlan : subsidyPlans) {
      if (new Date().after(subsidyPlan.getEffectiveStartTime())) {
        effect(tenant, subsidyPlan.getUuid(), operateInfo);
      }
    }
    List<String> uuids = subsidyPlans.stream().map(SubsidyPlan::getUuid).collect(Collectors.toList());
    // 创建门店异常和导入门店异常
    ExceptionPlanEvCallMsg exceptionPlanEvCallMsg = new ExceptionPlanEvCallMsg();
    exceptionPlanEvCallMsg.setTenant(tenant);
    exceptionPlanEvCallMsg.setUuids(uuids);
    publisher.publishForNormal(ExceptionPlanEvCallExecutor.PLAN_EXCEPTION_EXECUTOR_ID, exceptionPlanEvCallMsg);

    SubsidyMessageConfig subsidyMessageConfig = configClient.getConfig(tenant, SubsidyMessageConfig.class);
    for (SubsidyPlan plan : subsidyPlans) {
      DingTalkLinkMsg.Link link = new DingTalkLinkMsg.Link();
      link.setTitle("新到补贴计划通知");
      String text = subsidyMessageConfig.getCreateText().replace("X", plan.getPlanName());
      link.setText(text);
      link.setMessageUrl(subsidyMessageConfig.getJumpUrl() + "?uuid=" + plan.getUuid());
      link.setPicUrl(subsidyMessageConfig.getPicUrl());
      sendSubsidyEvent(tenant, plan.getOrgId(), plan.getShop(), link);
    }
  }

  @Override
  public void delete(String tenant, String uuid) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    SubsidyPlan plan = subsidyPlanDao.get(tenant, uuid);
    if (plan == null) {
      return;
    }
    if (!SubsidyPlanState.INIT.name().equals(plan.getState())) {
      throw new BaasException("无法删除当前补贴计划");
    }
    subsidyPlanDao.delete(tenant, uuid);
  }

  @Override
  @Tx
  public void deductionSaveNew(String tenant, List<DeductionRecord> deductionRecords) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(deductionRecords, "活动信息");

    deductionRecordDao.batchSaveNew(tenant, deductionRecords, SopUtils.getSysOperateInfo());
  }

  @Override
  public void logSaveNew(String tenant, SubsidyPlan newSubsidyPlan, SubsidyPlan oldSubsidyPlan, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(newSubsidyPlan, "修改后补贴计划");
    Assert.notNull(oldSubsidyPlan, "修改前补贴计划");

    // 记录调整记录 名称/补贴金额/生效日期
    List<LogExt> logExts = buildLogExts(newSubsidyPlan, oldSubsidyPlan);
    if (CollectionUtils.isNotEmpty(logExts)) {
      SubsidyPlanLog subsidyPlanLog = buildSubsidyPlanLog(tenant, newSubsidyPlan, logExts, operateInfo);
      subsidyPlanLogDao.save(tenant, subsidyPlanLog, operateInfo);
    }
  }

  @Override
  public void relation(String tenant, ActivityType type, List<String> activityIds) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(type, "type");
    Assert.notEmpty(activityIds, "activityIds");

    for (String activityId : activityIds) {
      ActivityRelationEvCallMsg msg = new ActivityRelationEvCallMsg();
      msg.setTenant(tenant);
      msg.setActivityId(activityId);
      msg.setActivityType(type);
      publisher.publishForNormal(ActivityRelationEvCallExecutor.ACTIVITY_RELATION_EXECUTOR_ID, msg);
    }
  }

  @Override
  @Tx
  public void saveModify(String tenant, SubsidyPlan subsidyPlan, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(subsidyPlan, "补贴计划列表");
    Assert.notNull(subsidyPlan.getAmount(), "补贴金额");

    ObjectNode planTime = (ObjectNode) subsidyPlan.getExpend().get(SubsidyPlan.Ext.PLAN_TIME);
    if (PlanTimeMode.DELAY.name().equalsIgnoreCase(ObjectNodeUtil.asText(planTime.get(SubsidyPlan.Ext.MODE)))
        && Objects.isNull(subsidyPlan.getBusinessHour())) {
      ExceptionPlan exceptionPlan = buildExceptionByBussinessHour(subsidyPlan);
      List<ExceptionPlan> exceptionPlans = Collections.singletonList(exceptionPlan);
      String redisKey = UUID.randomUUID().toString();
      redisService.set(redisKey, exceptionPlans, 30, TimeUnit.MINUTES);
      throw new BaasException(CHECK_EXCEPTION_CODE, redisKey);
    }
    SubsidyPlan oldSubsidyPlan = subsidyPlanDao.get(tenant, subsidyPlan.getUuid());
    if (Objects.isNull(oldSubsidyPlan)) {
      throw new BaasException("不存在补贴计划!");
    }
    if (oldSubsidyPlan.getUsedAmount().compareTo(subsidyPlan.getAmount()) > 0) {
      throw new BaasException("输入补贴金额小于已用金额!");
    }
    // 设置修改补贴计划信息
    SubsidyPlan newSubsidyPlan = buildNewSubsidyPlan(subsidyPlan, oldSubsidyPlan);
    Date curDate = getCurrentDate();
    // 校验待生效不可以选择过去时间
    if (curDate.after(newSubsidyPlan.getEffectiveStartTime())) {
      if (SubsidyPlanState.INIT.name().equals(newSubsidyPlan.getState())) {
        throw new BaasException("不能选择包括今天的以前日期");
      }
    }
    // 校验生效中的补贴计划时间
    if (curDate.before(newSubsidyPlan.getEffectiveStartTime()) || curDate.after(newSubsidyPlan.getEffectiveEndTime())) {
      if (SubsidyPlanState.PUBLISHED.name().equals(newSubsidyPlan.getState())) {
        throw new BaasException("开始时间不能大于当天,且结束时间不能小于当天!");
      }
    }
    if(curDate.compareTo(subsidyPlan.getEffectiveStartTime()) == 0 && getNoonDate().before(new Date())) {
      if (SubsidyPlanState.INIT.name().equals(newSubsidyPlan.getState())) {
        throw new BaasException("当前时间已超过12:00，起始日期不能选择当日");
      }
    }
    if (newSubsidyPlan.getEffectiveStartTime().after(newSubsidyPlan.getEffectiveEndTime())) {
      throw new BaasException("开始时间不能小于结束时间!");
    }
    // 校验补贴计划时间重复
    List<SubsidyPlan> existPlans = subsidyPlanDao.listByShop(tenant, oldSubsidyPlan.getOrgId(),
        oldSubsidyPlan.getShop(), SubsidyPlanState.INIT.name(), SubsidyPlanState.PUBLISHED.name());
    if (CollectionUtils.isNotEmpty(existPlans)) {
      existPlans = existPlans.stream()
          .filter(o -> !o.getUuid().equals(subsidyPlan.getUuid()))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(existPlans)) {
        List<ExceptionPlan> exceptionPlans = listConflictPlan(newSubsidyPlan, existPlans);
        if (CollectionUtils.isNotEmpty(exceptionPlans)) {
          String redisKey = UUID.randomUUID().toString();
          redisService.set(redisKey, exceptionPlans, 30, TimeUnit.MINUTES);
          throw new BaasException(CHECK_EXCEPTION_CODE, redisKey);
        }
      }
    }
    subsidyPlanDao.update(tenant, newSubsidyPlan, operateInfo);
    //如果计划活动的生效时间小于当前时间,更新计划状态
    if (new Date().after(newSubsidyPlan.getEffectiveStartTime())) {
      effect(tenant, newSubsidyPlan.getUuid(), operateInfo);
    }
    // 生效中：推送修改补贴计划且重新关联活动(包含不变,不包含删除关联并关联异常补贴计划)
    if (SubsidyPlanState.PUBLISHED.name().equals(newSubsidyPlan.getState())
        || SubsidyPlanState.INIT.name().equals(newSubsidyPlan.getState())) {
      alterRelationActivity(tenant, newSubsidyPlan.getUuid(), newSubsidyPlan.getShop(),
          newSubsidyPlan.getEffectiveStartTime(), newSubsidyPlan.getEffectiveEndTime(), operateInfo);

      if (SubsidyPlanState.PUBLISHED.name().equals(newSubsidyPlan.getState())) {
        PlanPushEvCallMsg pushEvCallMsg = buildPlanPushEvCallMsg(tenant, newSubsidyPlan);
        publisher.publishForNormal(PlanPushEvCallExecutor.PLAN_PUSH_EXECUTOR_ID, pushEvCallMsg);
      }
    }
    // 保存补贴计划调整记录
    logSaveNew(tenant, newSubsidyPlan, oldSubsidyPlan, operateInfo);
    // 金额变动 给扣款记录加一条数据 记录扣款记录
    if (newSubsidyPlan.getAmount().compareTo(oldSubsidyPlan.getAmount()) != 0) {
      DeductionRecord deductionRecord = buildHeadDeductionRecord(oldSubsidyPlan, newSubsidyPlan);
      deductionRecordDao.saveNew(tenant, deductionRecord, operateInfo);
    }
    SubsidyMessageConfig config = configClient.getConfig(tenant, SubsidyMessageConfig.class);
    DingTalkLinkMsg.Link link = new DingTalkLinkMsg.Link();
    link.setTitle(config.getModifyTitle());
    link.setText(config.getModifyText().replace("X", newSubsidyPlan.getPlanName()));
    link.setPicUrl(config.getPicUrl());
    link.setMessageUrl(config.getJumpUrl() + "?uuid=" + subsidyPlan.getUuid());
    sendSubsidyEvent(tenant, subsidyPlan.getOrgId(), newSubsidyPlan.getShop(), link);
  }

  @Override
  @Tx
  public void terminate(String tenant, String uuid, TerminateType operation, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "补贴计划Id");
    Assert.notNull(operation, "操作类型");

    SubsidyPlan subsidyPlan = subsidyPlanDao.get(tenant, uuid);
    if (Objects.isNull(subsidyPlan)) {
      throw new BaasException("补贴计划不存在!");
    }
    // 幂等性处理
    if (SubsidyPlanState.TERMINATED.name().equals(subsidyPlan.getState())) {
      log.info("已经终止,无需继续终止");
      if (!TerminateType.SUBSIDY.equals(operation)) {
        terminateActivity(tenant, uuid, operation, operateInfo);
      }
      return;
    }
    if (!SubsidyPlanState.PUBLISHED.name().equals(subsidyPlan.getState())) {
      throw new BaasException("该计划不是生效中");
    }
    subsidyPlan.setState(SubsidyPlanState.TERMINATED.name());
    subsidyPlan.setLastModifyInfo(operateInfo);
    subsidyPlanDao.update(tenant, subsidyPlan, operateInfo);
    // 终止计划和活动
    terminatePlan(tenant, subsidyPlan.getUuid());
    if (!TerminateType.SUBSIDY.equals(operation)) {
      terminateActivity(tenant, uuid, operation, operateInfo);
    }
    // 活动关联到异常
    activityAssocsException(tenant, uuid, subsidyPlan.getShop());

    SubsidyMessageConfig config = configClient.getConfig(tenant, SubsidyMessageConfig.class);
    DingTalkLinkMsg.Link link = new DingTalkLinkMsg.Link();
    link.setTitle(config.getTerminateTitle());
    link.setText(config.getTerminateText().replace("X", subsidyPlan.getPlanName()));
    link.setPicUrl(config.getPicUrl());
    link.setMessageUrl(config.getJumpUrl() + "?uuid=" + subsidyPlan.getUuid());
    sendSubsidyEvent(tenant, subsidyPlan.getOrgId(), subsidyPlan.getShop(), link);
  }

  private void activityAssocsException(String tenant, String uuid, String shop) {
    // 终止计划的时候，活动没有完全关联到异常计划,需要把没关联的活动，关联到异常补贴计划中
    log.info("终止活动异常关联中。。。");
    List<SubsidyPlanActivityAssoc> activityAssocs = activityAssocsDao.list(tenant, uuid);

    if (CollectionUtils.isEmpty(activityAssocs)) {
      log.info("不存在补贴计划无需进行异常关联!");
      return;
    }

    List<String> activityIds = activityAssocs.stream()
        .map(SubsidyPlanActivityAssoc::getActivityId)
        .collect(Collectors.toList());

    SubsidyPlan exceptionPlan = subsidyPlanDao.getByShop(tenant, shop, SubsidyPlanState.EXCEPTION.name());

    List<SubsidyPlanActivityAssoc> exceptActivityAssocs = activityAssocsDao.listByOwnerAndActivitys(tenant,
        exceptionPlan.getUuid(), activityIds);
    if (CollectionUtils.isEmpty(exceptActivityAssocs)) {
      for (SubsidyPlanActivityAssoc activityAssoc : activityAssocs) {
        activityAssoc.setUuid(UUID.randomUUID().toString());
        activityAssoc.setOwner(exceptionPlan.getUuid());
      }
      activityAssocsDao.batchSave(tenant, activityAssocs);
      return;
    }
    List<String> exceptActivityIds = exceptActivityAssocs.stream()
        .map(SubsidyPlanActivityAssoc::getActivityId)
        .collect(Collectors.toList());
    activityAssocs = activityAssocs.stream()
        .filter(o -> !exceptActivityIds.contains(o.getActivityId()))
        .collect(Collectors.toList());

    for (SubsidyPlanActivityAssoc activityAssoc : activityAssocs) {
      activityAssoc.setUuid(UUID.randomUUID().toString());
    }
    activityAssocsDao.batchSave(tenant, activityAssocs);
    log.info("终止活动关联成功");
  }

  @Override
  public QueryResult<SubsidyPlanLog> logQuery(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "qd");

    return subsidyPlanLogDao.query(tenant, qd);
  }

  @Override
  public SubsidyPlan get(String tenant, String planId, String... fetchParts) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(planId, "补贴计划Id");

    return subsidyPlanDao.get(tenant, planId);
  }

  @Override
  public void checkShopExistPlan(String tenant, List<String> shops, Date effectiveStartTime, Date effectiveEndTime)
      throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shops, "shops");
    Assert.notNull(effectiveStartTime, "effectiveStartTime");
    Assert.notNull(effectiveEndTime, "effectiveEndTime");

    checkShopExistPlan(tenant, null, shops, effectiveStartTime, effectiveEndTime);
  }

  @Override
  public void checkShopExistPlan(String tenant, String orgId, List<String> shops, Date effectiveStartTime,
      Date effectiveEndTime) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shops, "shops");
    Assert.notNull(effectiveStartTime, "effectiveStartTime");
    Assert.notNull(effectiveEndTime, "effectiveEndTime");

    if (effectiveStartTime.after(effectiveEndTime)) {
      throw new BaasException("开始时间不能小于结束时间!");
    }

    List<SubsidyPlan> subsidyPlans = subsidyPlanDao.listByShops(tenant, orgId, shops, SubsidyPlanState.INIT.name(),
        SubsidyPlanState.PUBLISHED.name());
    subsidyPlans = subsidyPlans.stream()
        .filter(o -> {
          // 时间存在包含
          return checkIncludeTime(o.getEffectiveStartTime(), o.getEffectiveEndTime(), effectiveStartTime,
              effectiveEndTime);
        })
        .collect(Collectors.toList());
    List<String> noPlanShop;
    if (CollectionUtils.isNotEmpty(subsidyPlans)) {
      Set<String> existPlanShops = subsidyPlans.stream().map(SubsidyPlan::getShop).collect(Collectors.toSet());
      noPlanShop = shops.stream().filter(s -> !existPlanShops.contains(s)).collect(Collectors.toList());
    } else {
      noPlanShop = shops;
    }
    if (CollectionUtils.isNotEmpty(noPlanShop)) {
      // 获取商户信息
      StoreFilter filter = new StoreFilter();
      filter.setIdIn(noPlanShop);
      filter.setPage(0);
      filter.setPageSize(0);
      QueryResult<Store> storesResult = storeService.query(tenant, filter);
      List<Store> stores = storesResult.getRecords();
      List<ActivityException> exceptions = new ArrayList<>();
      for (Store store : stores) {
        ActivityException activityException = new ActivityException();
        activityException.setShopCode(store.getCode());
        activityException.setShopName(store.getName());
        activityException.setReason("无可对应的补贴计划");
        exceptions.add(activityException);
      }
      String redisKey = UUID.randomUUID().toString();
      redisService.set(redisKey, exceptions, 30, TimeUnit.MINUTES);
      throw new BaasException(CHECK_EXCEPTION_CODE, redisKey);
    }
  }

  @Override
  @Tx
  public void updateEffectSubsidyPlanByDate(String tenant, Date date, String... state) {
    Assert.notNull(tenant, "租户");

    subsidyPlanDao.effectSubsidyPlanByDate(tenant, date, state);
  }

  @Override
  @Tx
  public void updateExpireSubsidyPlanByDate(String tenant, Date date, String... state) {
    Assert.notNull(tenant, "租户");

    subsidyPlanDao.expireSubsidyPlanByDate(tenant, date, SopUtils.getSysOperateInfo(), state);
  }

  @Override
  public QueryResult<SubsidyPlanActivityAssoc> activityQuery(String tenant, String owner,
      SubsidyActivityState subsidyActivityState, List<ActivityType> activityType) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(owner, "补贴计划id");

    QueryResult<SubsidyPlanActivityAssoc> queryResult = new QueryResult<>();
    List<SubsidyPlanActivityAssoc> activityAssocs = activityAssocsDao.list(tenant, owner, activityType);

    Map<String, SubsidyPlanActivityAssoc> activityAssocMap = buildActivityAssocMap(tenant, owner);
    if (!Objects.isNull(activityAssocMap)) {
      for (SubsidyPlanActivityAssoc activityAssoc : activityAssocs) {
        SubsidyPlanActivityAssoc newActivityAssoc = activityAssocMap.get(activityAssoc.getUuid());
        if (Objects.isNull(newActivityAssoc)) {
          continue;
        }
        activityAssoc.setActivityType(newActivityAssoc.getActivityType());
        activityAssoc.setTimePeriodCondition(newActivityAssoc.getTimePeriodCondition());
        activityAssoc.setActivityId(newActivityAssoc.getActivityId());
        activityAssoc.setActivityName(newActivityAssoc.getActivityName());
        activityAssoc.setActivityState(newActivityAssoc.getActivityState());
        activityAssoc.setActivityStartTime(newActivityAssoc.getActivityStartTime());
        activityAssoc.setActivityEndTime(newActivityAssoc.getActivityEndTime());
        activityAssoc.setTimeCycle(newActivityAssoc.getTimeCycle());
        activityAssoc.setCreated(newActivityAssoc.getCreated());
        activityAssoc.setCreatorId(newActivityAssoc.getCreatorId());
        activityAssoc.setCreatorName(newActivityAssoc.getCreatorName());
        activityAssoc.setLastModified(newActivityAssoc.getLastModified());
        activityAssoc.setLastModifierId(newActivityAssoc.getLastModifierId());
        activityAssoc.setLastModifierName(newActivityAssoc.getLastModifierName());
        activityAssoc.setPromotions(newActivityAssoc.getPromotions());
      }
    }
    // 活动状态过滤
    if (!Objects.isNull(subsidyActivityState)) {
      if (SubsidyActivityState.READY.equals(subsidyActivityState)) {
        activityAssocs = activityAssocs.stream().filter(o -> {
          if (ActivityState.INIT.equals(o.getActivityState()) || ActivityState.CONFIRMED.equals(o.getActivityState())
              || ActivityState.AUDITED.equals(o.getActivityState())) {
            return true;
          }
          return false;
        }).collect(Collectors.toList());
      } else if (SubsidyActivityState.RUNNING.equals(subsidyActivityState)) {
        activityAssocs = activityAssocs.stream().filter(o -> {
          if (ActivityState.PUBLISHED.equals(o.getActivityState())) {
            return true;
          }
          return false;
        }).collect(Collectors.toList());
      } else {
        activityAssocs = activityAssocs.stream().filter(o -> {
          if (ActivityState.CANCELED.equals(o.getActivityState()) || ActivityState.EXPIRED.equals(o.getActivityState())
              || ActivityState.TERMINATED.equals(o.getActivityState())) {
            return true;
          }
          return false;
        }).collect(Collectors.toList());
      }
    }

    queryResult.setRecords(activityAssocs);
    return queryResult;
  }

  @Override
  public QueryResult<DeductionRecord> deductionQuery(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "补贴计划id");

    qd.addByField(DeductionRecord.Queries.STATE, Cop.EQUALS, DeductionState.SUCCESS.name());
    return deductionRecordDao.query(tenant, qd);
  }

  @Override
  public List<SubsidyPlan> listByEffectiveDateScope(String tenant, Date effectDate, String... state) {
    Assert.notNull(tenant, "租户");

    return subsidyPlanDao.listByEffectiveDateScope(tenant, effectDate, state);
  }

  @Override
  public QueryResult<SubsidyPlan> query(String tenant, QueryDefinition qd, String... fetchParts) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "qd");

    return subsidyPlanDao.query(tenant, qd);
  }

  @Override
  public List<ActivityRelationException> activityEditCheck(String tenant, SubsidyPlan subsidyPlan)
      throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(subsidyPlan, "补贴计划列表");

    SubsidyPlan oldSubsidyPlan = subsidyPlanDao.get(tenant, subsidyPlan.getUuid());
    if (Objects.isNull(oldSubsidyPlan)) {
      throw new BaasException("不存在补贴计划!");
    }
    SubsidyPlan newSubsidyPlan = buildNewSubsidyPlan(subsidyPlan, oldSubsidyPlan);

    Map<String, SubsidyPlanActivityAssoc> activityAssocMap = buildActivityAssocMap(tenant, newSubsidyPlan.getUuid());
    if (Objects.isNull(activityAssocMap)) {
      return null;
    }
    List<SubsidyPlanActivityAssoc> activityAssocs = activityAssocsDao.list(tenant, newSubsidyPlan.getUuid());
    activityAssocs = activityAssocs.stream()
        .filter(o -> (!checkIncludeTime(newSubsidyPlan.getEffectiveStartTime(), newSubsidyPlan.getEffectiveEndTime(),
            o.getActivityStartTime(), o.getActivityEndTime())))
        .collect(Collectors.toList());

    List<ActivityRelationException> activityRelationExceptions = new ArrayList<>(activityAssocs.size());
    for (SubsidyPlanActivityAssoc activityAssoc : activityAssocs) {
      SubsidyPlanActivityAssoc activityAssocInfo = activityAssocMap.get(activityAssoc.getUuid());

      ActivityRelationException activityRelationException = buildActivityRelationException(activityAssocInfo);
      activityRelationExceptions.add(activityRelationException);
    }

    // 过滤已过期/已终止/已作废
    activityRelationExceptions = activityRelationExceptions.stream().filter(o -> {
      if (ActivityState.CANCELED.equals(o.getActivityState())) {
        return false;
      }
      if (ActivityState.TERMINATED.equals(o.getActivityState())) {
        return false;
      }
      if (ActivityState.EXPIRED.equals(o.getActivityState())) {
        return false;
      }
      return true;
    }).collect(Collectors.toList());
    return activityRelationExceptions;
  }

  @Tx
  @Override
  public void effect(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    log.info("查询待生效补贴计划,tenant={},uuid={}", tenant, uuid);
    SubsidyPlan plan = subsidyPlanDao.get(tenant, uuid);
    List<String> inValidStates = Arrays.asList(SubsidyPlanState.TERMINATED.name(), SubsidyPlanState.EXCEPTION.name(), SubsidyPlanState.EXPIRED.name());
    if (inValidStates.contains(plan.getState())) {
      throw new BaasException("补贴计划状态无效,uuid={},state={}", plan.getUuid(), plan.getState());
    }
    if (SubsidyPlanState.PUBLISHED.name().equals(plan.getState())) {
      log.warn("补贴计划已生效,忽略,tenant={},uuid={}", tenant, uuid);
      return;
    }
    if (new Date().before(plan.getEffectiveStartTime())) {
      throw new BaasException("补贴计划还未到生效时间,uuid={},effectStartTime={}", uuid, plan.getEffectiveStartTime());
    }
    plan.setState(SubsidyPlanState.PUBLISHED.name());
    subsidyPlanDao.update(tenant, plan, operateInfo);

    // 生效中：推送修改补贴计划且重新关联活动(包含不变,不包含删除关联并关联异常补贴计划)
    alterRelationActivity(tenant, plan.getUuid(), plan.getShop(),
        plan.getEffectiveStartTime(), plan.getEffectiveEndTime(), operateInfo);
    PlanPushEvCallMsg pushEvCallMsg = buildPlanPushEvCallMsg(tenant, plan);
    publisher.publishForNormal(PlanPushEvCallExecutor.PLAN_PUSH_EXECUTOR_ID, pushEvCallMsg);

    log.info("补贴计划已生效,tenant={},uuid={}", tenant, uuid);
  }

  @Override
  public void effect(String tenant, List<String> uuids, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(uuids, "uuids");
    try {
      log.info("批量查询待生效补贴计划,tenant={},uuids={}", tenant, JSONUtil.safeToJson(uuids));
    } catch (BaasException e) {
      log.error("json转换失败");
    }
    List<SubsidyPlan> plans = subsidyPlanDao.listByUuids(tenant, uuids);
    List<String> inValidStates = Arrays.asList(SubsidyPlanState.TERMINATED.name(), SubsidyPlanState.EXCEPTION.name(), SubsidyPlanState.EXPIRED.name());
    for (SubsidyPlan plan : plans) {
      if (inValidStates.contains(plan.getState())) {
        log.error("补贴计划状态无效,uuid={},state={}", plan.getUuid(), plan.getState());
        continue;
      }
      if (SubsidyPlanState.PUBLISHED.name().equals(plan.getState())) {
        log.warn("补贴计划已生效,忽略,tenant={},uuid={}", tenant, plan.getUuid());
        continue;
      }
      if (new Date().before(plan.getEffectiveStartTime())) {
        log.error("补贴计划还未到生效时间,uuid={},effectStartTime={}", plan.getUuid(), plan.getEffectiveStartTime());
        continue;
      }
      plan.setState(SubsidyPlanState.PUBLISHED.name());
      subsidyPlanDao.update(tenant, plan, operateInfo);

      // 生效中：推送修改补贴计划且重新关联活动(包含不变,不包含删除关联并关联异常补贴计划)
      alterRelationActivity(tenant, plan.getUuid(), plan.getShop(),
          plan.getEffectiveStartTime(), plan.getEffectiveEndTime(), operateInfo);
      PlanPushEvCallMsg pushEvCallMsg = buildPlanPushEvCallMsg(tenant, plan);
      publisher.publishForNormal(PlanPushEvCallExecutor.PLAN_PUSH_EXECUTOR_ID, pushEvCallMsg);
      log.info("补贴计划已生效,tenant={},uuid={}", tenant, plan.getUuid());
    }
  }

  /**
   * 获取异常计划 不存在则初始化
   *
   * @param tenant
   *     租户id
   * @param shop
   *     门店id
   * @param operateInfo
   *     操作信息
   * @return 补贴计划信息
   */
  private SubsidyPlan getExceptionPlan(String tenant, String shop, OperateInfo operateInfo){
    Assert.notNull(tenant, "租户");
    Assert.notNull(shop, "门店ID");

    SubsidyPlan exceptionPlan = subsidyPlanDao.getByShop(tenant, shop, SubsidyPlanState.EXCEPTION.name());
    return exceptionPlan;
  }

  private SubsidyPlan buildNewSubsidyPlan(SubsidyPlan subsidyPlan, SubsidyPlan oldSubsidyPlan) {
    SubsidyPlan newSubsidyPlan = CONVERTER.convert(oldSubsidyPlan);
    newSubsidyPlan.setPlanName(subsidyPlan.getPlanName());
    newSubsidyPlan.setAmount(subsidyPlan.getAmount());
    newSubsidyPlan.setEffectiveStartTime(subsidyPlan.getEffectiveStartTime());
    newSubsidyPlan.setEffectiveEndTime(subsidyPlan.getEffectiveEndTime());

    // 通过开店时间获取生效时间
    ObjectNode planTime = (ObjectNode) subsidyPlan.getExpend().get(SubsidyPlan.Ext.PLAN_TIME);
    if (PlanTimeMode.DELAY.name().equalsIgnoreCase(ObjectNodeUtil.asText(planTime.get(SubsidyPlan.Ext.MODE)))) {
      Date OpenTime = subsidyPlan.getBusinessHour();
      newSubsidyPlan.setEffectiveStartTime(
          getDateAfter(OpenTime, ObjectNodeUtil.asInt(planTime.get(SubsidyPlan.Ext.DELAY_BEGIN_DAY), 0)));
      newSubsidyPlan.setEffectiveEndTime(
          getEndDate(OpenTime, ObjectNodeUtil.asInt(planTime.get(SubsidyPlan.Ext.DELAY_END_DAY), 0)));
    }
    newSubsidyPlan.setExt(subsidyPlan.getExpend().toString());
    return newSubsidyPlan;
  }

  private PlanPushEvCallMsg buildPlanPushEvCallMsg(String tenant, SubsidyPlan newSubsidyPlan) {
    PlanPushEvCallMsg pushEvCallMsg = new PlanPushEvCallMsg();
    pushEvCallMsg.setTenant(tenant);
    pushEvCallMsg.setPlanId(newSubsidyPlan.getUuid());
    pushEvCallMsg.setPlanName(newSubsidyPlan.getPlanName());
    pushEvCallMsg.setStoreGid(Integer.valueOf(newSubsidyPlan.getShop()));
    pushEvCallMsg.setState(newSubsidyPlan.getState());
    pushEvCallMsg.setAmount(newSubsidyPlan.getAmount());
    pushEvCallMsg.setEffectiveStartTime(newSubsidyPlan.getEffectiveStartTime());
    pushEvCallMsg.setEffectiveEndTime(newSubsidyPlan.getEffectiveEndTime());
    return pushEvCallMsg;
  }

  /**
   * 修改补贴计划的活动关联关系
   *
   * @param tenant
   *     租户
   * @param uuid
   *     计划id
   * @param shop
   *     门店id
   * @param startTime
   *     开始时间
   * @param endTime
   *     结束时间
   * @param operateInfo
   *     操作信息
   */
  private void alterRelationActivity(String tenant, String uuid, String shop, Date startTime, Date endTime,
      OperateInfo operateInfo) {
    List<SubsidyPlanActivityAssoc> exceptionActivityAssocs = new ArrayList<>(0);
    List<String> removeAssocIds = new ArrayList<>();
    List<SubsidyPlanActivityAssoc> activityAssocs = activityAssocsDao.list(tenant, uuid);

    SubsidyPlan exceptionPlan = getExceptionPlan(tenant, shop, operateInfo);
    // 批量新增异常关联和删除不包含关联
    for (SubsidyPlanActivityAssoc activityAssoc : activityAssocs) {
      if (!checkIncludeTime(startTime, endTime, activityAssoc.getActivityStartTime(),
          activityAssoc.getActivityEndTime())) {
        SubsidyPlanActivityAssoc activityExceptionAssoc = buildActivityExceptionAssoc(activityAssoc, exceptionPlan);
        exceptionActivityAssocs.add(activityExceptionAssoc);
        if (checkOutScopeTime(startTime, endTime, activityAssoc.getActivityStartTime(),
            activityAssoc.getActivityEndTime())) {
          removeAssocIds.add(activityAssoc.getUuid());
        }
      }
    }
    // 去除新增异常重复的数据
    List<SubsidyPlanActivityAssoc> planActivityAssocs = activityAssocsDao.list(tenant, exceptionPlan.getUuid());
    if (CollectionUtils.isNotEmpty(planActivityAssocs)) {
      List<String> excepionActivityIds = planActivityAssocs.stream()
          .map(SubsidyPlanActivityAssoc::getActivityId)
          .collect(Collectors.toList());
      exceptionActivityAssocs = exceptionActivityAssocs.stream()
          .filter(o -> !excepionActivityIds.contains(o.getActivityId()))
          .collect(Collectors.toList());
    }

    if (exceptionActivityAssocs.size() > 0) {
      activityAssocsDao.batchSave(tenant, exceptionActivityAssocs);
    }
    if (removeAssocIds.size() > 0) {
      activityAssocsDao.batchRemove(tenant, removeAssocIds);
      // 生效中 正常推送补贴计划修改后的活动关联关系
      SubsidyPlan subsidyPlan = subsidyPlanDao.get(tenant, uuid);
      if (SubsidyPlanState.PUBLISHED.name().equals(subsidyPlan.getState())) {
        ActivityRelationToH6EvCallMsg activityRelationToH6EvCallMsg = new ActivityRelationToH6EvCallMsg();
        activityRelationToH6EvCallMsg.setTenant(tenant);
        activityRelationToH6EvCallMsg.setOwners(Collections.singletonList(uuid));
        publisher.publishForNormal(ActivityRelationToH6EvCallExecutor.ACTIVITY_RELATION_TOH6_EXECUTOR_ID,
            activityRelationToH6EvCallMsg);
      }
    }
  }

  private DeductionRecord buildHeadDeductionRecord(SubsidyPlan oldSubsidyPlan, SubsidyPlan newSubsidyPlan) {
    DeductionRecord deductionRecord = new DeductionRecord();
    deductionRecord.setOwner(newSubsidyPlan.getUuid());
    deductionRecord.setType(DeductionType.LEADER_ADJUSTMENT);
    deductionRecord.setUuid(UUID.randomUUID().toString());
    deductionRecord.setState(DeductionState.SUCCESS);
    deductionRecord.setDeductionTime(new Date());
    deductionRecord.setAmount(newSubsidyPlan.getAmount().subtract(oldSubsidyPlan.getAmount()));
    deductionRecord.setActivityName("总部额度调整");
    return deductionRecord;
  }

  private ActivityRelationException buildActivityRelationException(SubsidyPlanActivityAssoc activityAssocInfo) {
    ActivityRelationException activityRelationException = new ActivityRelationException();
    activityRelationException.setActivityId(activityAssocInfo.getActivityId());
    activityRelationException.setActivityType(activityAssocInfo.getActivityType());
    activityRelationException.setActivityName(activityAssocInfo.getActivityName());
    activityRelationException.setActivityState(activityAssocInfo.getActivityState());
    activityRelationException.setActivityStartTime(activityAssocInfo.getActivityStartTime());
    activityRelationException.setActivityEndTime(activityAssocInfo.getActivityEndTime());
    return activityRelationException;
  }

  private void terminateActivity(String tenant, String planId, TerminateType operation, OperateInfo operateInfo) {
    List<SubsidyPlanActivityAssoc> assocs = activityAssocsDao.list(tenant, planId);
    Date executeDate = getActivityTerminateDate(operation);

    for (SubsidyPlanActivityAssoc assoc : assocs) {
      ActivityTerminatedEvCallMsg subsidyPlanEvCallMsg = new ActivityTerminatedEvCallMsg();
      subsidyPlanEvCallMsg.setActivityType(assoc.getActivityType());
      subsidyPlanEvCallMsg.setTenant(tenant);
      subsidyPlanEvCallMsg.setActivityId(assoc.getActivityId());
      subsidyPlanEvCallMsg.setOperateInfo(operateInfo);
      publisher.publishForNormal(ActivityTerminatedEvCallExecutor.ACTIVITY_TERMINATED_EXECUTOR_ID, subsidyPlanEvCallMsg,
          executeDate);
    }
  }

  private void terminatePlan(String tenant, String uuid) {
    TerminatedEvCallMsg terminatedEvCallMsg = new TerminatedEvCallMsg();
    terminatedEvCallMsg.setTenant(tenant);
    terminatedEvCallMsg.setPlanId(uuid);
    publisher.publishForNormal(TerminatedEvCallExecutor.TERMINATED_EXECUTOR_ID, terminatedEvCallMsg);
  }

  /**
   * 得到活动对应的终止时间
   *
   * @param operation
   *     终止操作类型
   * @return 活动终止时间
   */
  private Date getActivityTerminateDate(TerminateType operation) {
    if (TerminateType.SUBSIDY_NEXT_DAY_ACTIVITY.equals(operation)) {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      return calendar.getTime();
    }
    return null;
  }

  /**
   * 调用营销活动,价格促销和促销模型详情补全活动关联信息 并返回关联活动的信息详细信息
   *
   * @param tenant
   *     租户
   * @param owner
   *     计划id
   * @return 构造<关联id, 活动详情>Map信息
   */
  private Map<String, SubsidyPlanActivityAssoc> buildActivityAssocMap(String tenant, String owner)
      throws BaasException {
    List<SubsidyPlanActivityAssoc> activityAssocs = activityAssocsDao.list(tenant, owner);
    if (Objects.isNull(activityAssocs)) {
      return null;
    }
    Map<ActivityType, List<SubsidyPlanActivityAssoc>> assicMaps = activityAssocs.stream()
        .collect(Collectors.groupingBy(SubsidyPlanActivityAssoc::getActivityType));
    // 查询营销活动,到店价促销,到店价促销模型基本信息
    List<SubsidyPlanActivityAssoc> promoteActivityAssocs = assicMaps.get(ActivityType.PROMOTE_ACTIVITY);
    List<SubsidyPlanActivityAssoc> pricePromotionAssocs = assicMaps.get(ActivityType.PRICE_PROMOTION);
    List<SubsidyPlanActivityAssoc> pricePromotionModeAssocs = assicMaps.get(ActivityType.PRICE_PROMOTION_MODEL);

    List<PromActivity> promActivitys = null;
    if (CollectionUtils.isNotEmpty(promoteActivityAssocs)) {
      List<String> activityId = promoteActivityAssocs.stream()
          .distinct()
          .map(SubsidyPlanActivityAssoc::getActivityId)
          .collect(Collectors.toList());
      promActivitys = activityService.list(tenant, activityId, PromActivity.ALL_PARTS);
    }

    List<PricePromotion> pricePromotions = null;
    if (CollectionUtils.isNotEmpty(pricePromotionAssocs)) {
      List<String> pricePromotionIds = pricePromotionAssocs.stream()
          .distinct()
          .map(SubsidyPlanActivityAssoc::getActivityId)
          .collect(Collectors.toList());
      pricePromotions = promotionService.list(tenant, pricePromotionIds, PricePromotion.FETCH_ALL);
    }

    // 到店价促销模型赋值
    List<BBasePricePromRes> pricePromotionModes = null;
    if (CollectionUtils.isNotEmpty(pricePromotionModeAssocs)) {
      List<String> pricePromotionModeIds = pricePromotionModeAssocs.stream()
          .distinct()
          .map(SubsidyPlanActivityAssoc::getActivityId)
          .collect(Collectors.toList());

      BBasePricePromListReq req = new BBasePricePromListReq();
      req.setUuids(pricePromotionModeIds);
      req.setFetchParts(BasePricePromotion.FETCH_ALL);
      BaasResponse<List<BBasePricePromRes>> response = rsMkhPmsClient.list(tenant, req);
      pricePromotionModes = response.getData();
    }
    Map<String, PromActivity> promActivityMap = null;
    Map<String, PricePromotion> pricePromotionMap = null;
    Map<String, BBasePricePromRes> pricePromModeMap = null;
    if (CollectionUtils.isNotEmpty(promActivitys)) {
      promActivityMap = promActivitys.stream().collect(Collectors.toMap(PromActivity::getUuid, o -> o));
    }
    if (CollectionUtils.isNotEmpty(pricePromotions)) {
      pricePromotionMap = pricePromotions.stream().collect(Collectors.toMap(PricePromotion::getUuid, o -> o));
    }

    if (CollectionUtils.isNotEmpty(pricePromotionModes)) {
      pricePromModeMap = pricePromotionModes.stream().collect(Collectors.toMap(BBasePricePromRes::getUuid, o -> o));
    }

    for (SubsidyPlanActivityAssoc activityAssoc : activityAssocs) {
      setSubsidyPlanActivityAssoc(promActivityMap, pricePromotionMap, pricePromModeMap, activityAssoc);
    }
    return activityAssocs.stream().collect(Collectors.toMap(SubsidyPlanActivityAssoc::getUuid, o -> o));
  }

  private void setSubsidyPlanActivityAssoc(Map<String, PromActivity> promActivityMap,
      Map<String, PricePromotion> pricePromotionMap, Map<String, BBasePricePromRes> pricePromModeMap,
      SubsidyPlanActivityAssoc activityAssoc) {
    if (ActivityType.PROMOTE_ACTIVITY.equals(activityAssoc.getActivityType()) && !Objects.isNull(promActivityMap)) {
      PromActivity promActivity = promActivityMap.get(activityAssoc.getActivityId());
      if (Objects.isNull(promActivity)) {
        return;
      }
      activityAssoc.setActivityName(promActivity.getName());
      activityAssoc.setActivityStartTime(promActivity.getDateRangeCondition().getDateRange().getBeginDate());
      activityAssoc.setActivityEndTime(promActivity.getDateRangeCondition().getDateRange().getEndDate());
      activityAssoc.setTimeCycle(promActivity.getDateRangeCondition().getTimeCycle());
      activityAssoc.setTimePeriodCondition(promActivity.getTimePeriodCondition());
      activityAssoc.setPromotions(promActivity.getPromotions());
      setActivityAssocOperation(activityAssoc, promActivity.getCreateInfo(), promActivity.getLastModifyInfo());
      activityAssoc.setActivityState(
          ActivityStateConvert.getActivityState(activityAssoc.getActivityType(), promActivity.getFrontState()));
      return;
    }
    if (ActivityType.PRICE_PROMOTION.equals(activityAssoc.getActivityType()) && !Objects.isNull(pricePromotionMap)) {
      PricePromotion pricePromotion = pricePromotionMap.get(activityAssoc.getActivityId());
      if (Objects.isNull(pricePromotion)) {
        return;
      }
      activityAssoc.setActivityName(pricePromotion.getFlowNo());
      activityAssoc.setActivityStartTime(pricePromotion.getEffectiveStartDate());
      activityAssoc.setActivityEndTime(pricePromotion.getEffectiveEndDate());
      setActivityAssocOperation(activityAssoc, pricePromotion.getCreateInfo(), pricePromotion.getLastModifyInfo());
      activityAssoc.setActivityState(
          ActivityStateConvert.getActivityState(activityAssoc.getActivityType(), pricePromotion.getState().name()));
      return;
    }
    if (ActivityType.PRICE_PROMOTION_MODEL.equals(activityAssoc.getActivityType())
        && !Objects.isNull(pricePromModeMap)) {
      BBasePricePromRes pricePromMode = pricePromModeMap.get(activityAssoc.getActivityId());
      if (Objects.isNull(pricePromMode)) {
        return;
      }
      activityAssoc.setActivityName(pricePromMode.getName());
      activityAssoc.setActivityStartTime(pricePromMode.getEffectiveStartDate());
      activityAssoc.setActivityEndTime(pricePromMode.getEffectiveEndDate());
      setActivityAssocOperation(activityAssoc, pricePromMode.getCreateInfo(), pricePromMode.getLastModifyInfo());
      activityAssoc.setActivityState(
          ActivityStateConvert.getActivityState(activityAssoc.getActivityType(), pricePromMode.getState()));
    }
  }

  private void setActivityAssocOperation(SubsidyPlanActivityAssoc activityAssoc, OperateInfo createInfo,
      OperateInfo lastModifyInfo) {
    activityAssoc.setCreatorId(createInfo.getOperator().getId());
    activityAssoc.setCreated(createInfo.getTime());
    activityAssoc.setCreatorName(createInfo.getOperator().getFullName());
    activityAssoc.setLastModifierId(lastModifyInfo.getOperator().getId());
    activityAssoc.setLastModifierName(lastModifyInfo.getOperator().getFullName());
    activityAssoc.setLastModified(lastModifyInfo.getTime());
  }

  private boolean checkIncludeTime(Date planStart, Date planEnd, Date compareStart, Date compareEnd) {
    // 生效时间包含关系
    return !(planStart.after(compareStart) || planEnd.before(compareEnd));
  }

  private boolean checkOutScopeTime(Date start, Date end, Date compareStart, Date compareEnd) {
    // 生效时间不相交
    return (start.after(compareEnd) || end.before(compareStart));
  }

  private SubsidyPlanActivityAssoc buildActivityExceptionAssoc(SubsidyPlanActivityAssoc activityAssoc,
      SubsidyPlan exceptionPlan) {
    SubsidyPlanActivityAssoc activityExceptionAssoc = new SubsidyPlanActivityAssoc();
    activityExceptionAssoc.setUuid(UUID.randomUUID().toString());
    activityExceptionAssoc.setOwner(exceptionPlan.getUuid());
    activityExceptionAssoc.setActivityType(activityAssoc.getActivityType());
    activityExceptionAssoc.setActivityId(activityAssoc.getActivityId());
    activityExceptionAssoc.setActivityStartTime(activityAssoc.getActivityStartTime());
    activityExceptionAssoc.setActivityEndTime(activityAssoc.getActivityEndTime());
    return activityExceptionAssoc;
  }

  private SubsidyPlanLog buildSubsidyPlanLog(String tenant, SubsidyPlan newSubsidyPlan, List<LogExt> logExts,
      OperateInfo operateInfo) {
    SubsidyPlanLog subsidyPlanLog = new SubsidyPlanLog();
    subsidyPlanLog.setTenant(tenant);
    subsidyPlanLog.setAction("saveModify");
    subsidyPlanLog.setOwner(newSubsidyPlan.getUuid());
    subsidyPlanLog.setContent(JSONObject.toJSONString(logExts));
    subsidyPlanLog.setUuid(UUID.randomUUID().toString());
    subsidyPlanLog.setCreated(operateInfo.getTime());
    subsidyPlanLog.setCreatorNS(operateInfo.getOperator().getNamespace());
    subsidyPlanLog.setCreatorId(operateInfo.getOperator().getId());
    subsidyPlanLog.setCreatorName(operateInfo.getOperator().getFullName());
    return subsidyPlanLog;
  }

  private List<ExceptionPlan> listConflictPlan(SubsidyPlan subsidyPlan, List<SubsidyPlan> existPlans) {
    List<SubsidyPlan> subsidyPlans = new ArrayList<>(1);
    subsidyPlans.add(subsidyPlan);
    return listConflictPlan(subsidyPlans, existPlans);
  }

  private List<LogExt> buildLogExts(SubsidyPlan newSubsidyPlan, SubsidyPlan oldSubsidyPlan) {
    List<LogExt> logExts = new ArrayList<>(3);
    LogExt amountExt = new LogExt();
    LogExt startTimeExt = new LogExt();
    LogExt endTimeExt = new LogExt();

    amountExt.setField(SubsidyPlanLogField.amount.name());
    amountExt.setOldValue(String.valueOf(oldSubsidyPlan.getAmount()));
    amountExt.setNewValue(String.valueOf(newSubsidyPlan.getAmount()));

    startTimeExt.setField(SubsidyPlanLogField.effectiveStartTime.name());
    startTimeExt.setOldValue(SDF.get().format(oldSubsidyPlan.getEffectiveStartTime()));
    startTimeExt.setNewValue(SDF.get().format(newSubsidyPlan.getEffectiveStartTime()));

    endTimeExt.setField(SubsidyPlanLogField.effectiveEndTime.name());
    endTimeExt.setOldValue(SDF.get().format(oldSubsidyPlan.getEffectiveEndTime()));
    endTimeExt.setNewValue(SDF.get().format(newSubsidyPlan.getEffectiveEndTime()));

    logExts.add(startTimeExt);
    logExts.add(endTimeExt);
    logExts.add(amountExt);
    return logExts;
  }

  private void initSubsidy(String tenant, SubsidyPlan subsidyPlan) {
    subsidyPlan.setUuid(UUID.randomUUID().toString());
    subsidyPlan.setTenant(tenant);
    subsidyPlan.setState(SubsidyPlanState.INIT.name());
    ObjectNode planTime = (ObjectNode) subsidyPlan.getExpend().get(SubsidyPlan.Ext.PLAN_TIME);
    if (PlanTimeMode.DELAY.name().equalsIgnoreCase(ObjectNodeUtil.asText(planTime.get(SubsidyPlan.Ext.MODE)))) {
      Date openTime = subsidyPlan.getBusinessHour();
      subsidyPlan.setEffectiveStartTime(
          getDateAfter(openTime, ObjectNodeUtil.asInt(planTime.get(SubsidyPlan.Ext.DELAY_BEGIN_DAY), 0)));
      subsidyPlan.setEffectiveEndTime(
          getEndDate(openTime, ObjectNodeUtil.asInt(planTime.get(SubsidyPlan.Ext.DELAY_END_DAY), 0)));
    }
    subsidyPlan.setExt(subsidyPlan.getExpend().toString());
  }

  private Date getCurrentDate() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  private List<ExceptionPlan> listConflictPlan(List<SubsidyPlan> subsidyPlans, List<SubsidyPlan> existPlans) {
    List<ExceptionPlan> exceptionPlans = new ArrayList<>(0);
    Map<String, List<SubsidyPlan>> subsidyMap = existPlans.stream()
        .collect(Collectors.groupingBy(SubsidyPlan::getShop));
    // 创建时间冲突处理
    for (SubsidyPlan subsidyPlan : subsidyPlans) {
      Calendar start = Calendar.getInstance();
      Calendar end = Calendar.getInstance();
      start.setTime(subsidyPlan.getEffectiveStartTime());
      end.setTime(subsidyPlan.getEffectiveEndTime());
      List<SubsidyPlan> existPlanList = subsidyMap.get(subsidyPlan.getShop());
      if (Objects.isNull(existPlanList)) {
        continue;
      }
      // 门店存在的补贴计划
      for (SubsidyPlan plan : existPlanList) {
        Calendar planStart = Calendar.getInstance();
        Calendar planEnd = Calendar.getInstance();
        planStart.setTime(plan.getEffectiveStartTime());
        planEnd.setTime(plan.getEffectiveEndTime());
        if (start.equals(planStart) || start.equals(planEnd) || end.equals(planStart) || end.equals(planEnd)) {
          ExceptionPlan exceptionPlan = buildExceptionByTime(subsidyPlan, plan);
          exceptionPlans.add(exceptionPlan);
          continue;
        }
        if (start.before(planEnd) && start.after(planStart)) {
          ExceptionPlan exceptionPlan = buildExceptionByTime(subsidyPlan, plan);
          exceptionPlans.add(exceptionPlan);
          continue;
        }
        if (end.before(planEnd) && end.after(planStart)) {
          ExceptionPlan exceptionPlan = buildExceptionByTime(subsidyPlan, plan);
          exceptionPlans.add(exceptionPlan);
          continue;
        }
        if (start.before(planStart) && end.after(planEnd)) {
          ExceptionPlan exceptionPlan = buildExceptionByTime(subsidyPlan, plan);
          exceptionPlans.add(exceptionPlan);
        }
      }
    }
    return exceptionPlans;
  }

  private ExceptionPlan buildExceptionByTime(SubsidyPlan subsidyPlan, SubsidyPlan existPlans) {
    ExceptionPlan exceptionPlan = new ExceptionPlan();
    exceptionPlan.setShopCode(existPlans.getShopCode());
    exceptionPlan.setShopName(existPlans.getShopName());
    exceptionPlan.setPlanId(existPlans.getUuid());
    exceptionPlan.setPlanName(existPlans.getPlanName());
    exceptionPlan.setOldEffectiveStartTime(existPlans.getEffectiveStartTime());
    exceptionPlan.setOldEffectiveEndTime(existPlans.getEffectiveEndTime());
    exceptionPlan.setNewEffectiveStartTime(subsidyPlan.getEffectiveStartTime());
    exceptionPlan.setNewEffectiveEndTime(subsidyPlan.getEffectiveEndTime());
    exceptionPlan.setReason("与已有补贴计划时间重叠");
    return exceptionPlan;
  }

  private ExceptionPlan buildExceptionByBussinessHour(SubsidyPlan subsidyPlan) {
    ExceptionPlan exceptionPlan = new ExceptionPlan();
    exceptionPlan.setShopCode(subsidyPlan.getShopCode());
    exceptionPlan.setShopName(subsidyPlan.getShopName());
    exceptionPlan.setPlanId(subsidyPlan.getUuid());
    exceptionPlan.setReason("不存在开店时间");
    return exceptionPlan;
  }

  private ExceptionPlan buildExceptionByEffectiveTime(SubsidyPlan subsidyPlan) {
    ExceptionPlan exceptionPlan = new ExceptionPlan();
    exceptionPlan.setShopCode(subsidyPlan.getShopCode());
    exceptionPlan.setShopName(subsidyPlan.getShopName());
    exceptionPlan.setPlanId(subsidyPlan.getUuid());
    exceptionPlan.setReason("不能选择包括今天的以前日期");
    return exceptionPlan;
  }

  private ExceptionPlan buildExceptionByStartEffectiveTime(SubsidyPlan subsidyPlan) {
    ExceptionPlan exceptionPlan = new ExceptionPlan();
    exceptionPlan.setShopCode(subsidyPlan.getShopCode());
    exceptionPlan.setShopName(subsidyPlan.getShopName());
    exceptionPlan.setPlanId(subsidyPlan.getUuid());
    exceptionPlan.setReason("当前时间已超过12:00，起始日期不能选择当日");
    return exceptionPlan;
  }

  private static Date getDateAfter(Date d, int day) {
    Calendar now = Calendar.getInstance();
    now.setTime(d);
    now.add(Calendar.DATE, day);
    return now.getTime();
  }

  private Date getNoonDate() {
    Calendar now = Calendar.getInstance();
    now.setTime(getCurrentDate());
    now.add(Calendar.HOUR, 12);
    return now.getTime();
  }

  private static Date getEndDate(Date d, int day) {
    Calendar now = Calendar.getInstance();
    now.setTime(d);
    now.add(Calendar.DATE, day);
    now.set(Calendar.HOUR_OF_DAY, 23);
    now.set(Calendar.MINUTE, 59);
    now.set(Calendar.SECOND, 59);
    now.set(Calendar.MILLISECOND, 59);
    return now.getTime();
  }

  private void sendSubsidyEvent(String tenant, String orgId, String shop, DingTalkLinkMsg.Link link) {
    SubsidyPlanMsg msg = new SubsidyPlanMsg();
    msg.setShop(shop);
    msg.setTenant(tenant);
    msg.setTraceId(MDC.get("trace_id"));
    msg.setOrgId(orgId);
    DingTalkLinkMsg talkLinkMsg = new DingTalkLinkMsg();
    talkLinkMsg.setLink(link);
    msg.setDingTalkLinkMsg(talkLinkMsg);
    publisher.publishForNormal(SubsidyPlanCallExecutor.SUBSIDY_PLAN_EXECUTOR_ID, msg);
  }
}
