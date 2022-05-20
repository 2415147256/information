package com.hd123.baas.sop.evcall.exector.explosive;

import com.alibaba.fastjson.JSON;
import com.hd123.baas.sop.service.api.GoodsKey;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivity;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivityLine;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivitySignJoin;
import com.hd123.baas.sop.evcall.exector.explosive.event.ExplosiveActivityAuditEvent;
import com.hd123.baas.sop.evcall.exector.explosive.event.ExplosiveActivityCancelEvent;
import com.hd123.baas.sop.evcall.exector.explosive.event.ExplosiveActivitySignEvent;
import com.hd123.baas.sop.evcall.exector.explosive.event.ExplosiveActivityStopEvent;
import com.hd123.baas.sop.service.dao.explosive.ExplosiveActivityDao;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.activity.H6ActivityType;
import com.hd123.baas.sop.remote.rsh6sop.activity.H6SopActivity;
import com.hd123.baas.sop.remote.rsh6sop.activity.H6SopActivityAbort;
import com.hd123.baas.sop.remote.rsh6sop.explosive.StoreExplosive;
import com.hd123.baas.sop.remote.rsh6sop.explosive.StoreExplosiveDetail;
import com.hd123.baas.sop.remote.rsh6sop.explosive.StoreExplosiveId;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.rumba.evcall.EvCallExecutor;
import com.hd123.rumba.evcall.EvCallManager;
import com.hd123.spms.commons.bean.OperateInfo;
import com.hd123.spms.commons.util.CollectionUtil;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component(ExplosiveForH6SOPService.BEAN_ID)
public class ExplosiveForH6SOPService implements EvCallExecutor {
  public static final String BEAN_ID = "sop-service.ExplosiveForH6SOPService";
  public static final OperateInfo operateInfo = new OperateInfo("系统");

  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private EvCallManager evCallManager;
  @Autowired
  private ExplosiveActivityDao explosiveActivityDao;

  @PostConstruct
  public void init() {
    evCallManager.addExecutor(this, BEAN_ID);
  }

  @EventListener
  public void processExplosiveActivityAuditEvent(ExplosiveActivityAuditEvent event) throws BaasException {
    ExplosiveActivity activity = event.getActivity();
    H6SopActivity target = new H6SopActivity();
    target.setUuid(event.getActivity().getUuid());
    target.setActivityCode(activity.getBillNumber());
    target.setActivityName(activity.getName());
    target.setAuditTime(activity.getLastModifyInfo().getTime());
    target.setBeginTime(activity.getDateRangeCondition().getDateRange().getBeginDate());
    target.setEndTime(activity.getDateRangeCondition().getDateRange().getEndDate());
    target.setMaterialAmount(activity.getMaterielAmount());
    target.setMemberExclusive(activity.getOnlyMember());
    target.setType(H6ActivityType.explosive);
    target.setStarterOrgUuid(DefaultOrgIdConvert.toH6DefOrgId(activity.getOrgId()));
    target.setSignBeginTime(activity.getSignRange().getBeginDate());
    target.setSignEndTime(activity.getSignRange().getEndDate());
    target.setPromChannels(CollectionUtil.toString(activity.getPromChannels()));
    target.setOverall(false);
    target.setNote(activity.getPromNote());

    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(activity.getTenant(), null, RsH6SOPClient.class);
    rsH6SOPClient.saveActivity(activity.getTenant(), target);

    ExplosiveActivityAuditEvent message = new ExplosiveActivityAuditEvent();
    ExplosiveActivity t = new ExplosiveActivity();
    t.setTenant(activity.getTenant());
    t.setUuid(activity.getUuid());
    message.setActivity(t);

    Date targetDate = DateUtils.addSeconds(activity.getSignRange().getEndDate(), 1);
    if (targetDate.after(new Date())) {
      evCallManager.submit(BEAN_ID, JsonUtil.objectToJson(message), targetDate);
    } else {
      evCallManager.submit(BEAN_ID, JsonUtil.objectToJson(message));
    }
  }

  @EventListener
  public void processExplosiveActivitySignEvent(ExplosiveActivitySignEvent event) throws BaasException {
    log.info("processExplosiveActivitySignEvent:{}", JSON.toJSONString(event));
    ExplosiveActivity activity = event.getActivity();
    ExplosiveActivitySignJoin signJoin = event.getSignJoin();

    StoreExplosive storeExplosive = new StoreExplosive();
    storeExplosive.setActivityCode(activity.getBillNumber());
    storeExplosive.setActivityName(activity.getName());
    storeExplosive.setBeginTime(activity.getDateRangeCondition().getDateRange().getBeginDate());
    storeExplosive.setEndTime(activity.getDateRangeCondition().getDateRange().getEndDate());
    storeExplosive.setStoreUuid(signJoin.getStore().getUuid());
    storeExplosive.setDetails(new ArrayList<>());

    Map<Date, List<ExplosiveActivitySignJoin.ExplosiveActivitySignLine>> signDateLinesMap = new HashMap<>();
    for (ExplosiveActivitySignJoin.ExplosiveActivitySignLine line : signJoin.getLines()) {
      signDateLinesMap.computeIfAbsent(line.getSignDate() != null ? DateUtils.truncate(line.getSignDate(), Calendar.DATE) : null, k -> new ArrayList<>()).add(line);
    }

    Date signDate = DateUtils.truncate(activity.getDateRangeCondition().getDateRange().getBeginDate(), Calendar.DATE);
    while (signDate.compareTo(DateUtils.truncate(activity.getDateRangeCondition().getDateRange().getEndDate(), Calendar.DATE)) <= 0) {
      List<ExplosiveActivitySignJoin.ExplosiveActivitySignLine> signLines = signDateLinesMap.get(signDate);
      if (signLines == null) {
        signLines = signDateLinesMap.getOrDefault(null, new ArrayList<>());
      }

      Map<GoodsKey, ExplosiveActivitySignJoin.ExplosiveActivitySignLine> signLineMap = signLines.stream()
          .collect(Collectors.toMap(
              signLien -> new GoodsKey(signLien.getEntity().getUuid(), signLien.getEntity().getQpc()),
              o -> o));
      Map<GoodsKey, StoreExplosiveDetail> explosiveDetailMap = new HashMap<>();
      for (ExplosiveActivityLine line : activity.getLines()) {
        ExplosiveActivitySignJoin.ExplosiveActivitySignLine targetLine = signLineMap.get(
            new GoodsKey(line.getEntity().getUuid(), line.getEntity().getQpc()));

        StoreExplosiveDetail detail = explosiveDetailMap.computeIfAbsent(
            new GoodsKey(line.getEntity().getUuid(), line.getEntity().getQpc().stripTrailingZeros()),
            k -> {
              StoreExplosiveDetail value = new StoreExplosiveDetail();
              value.setGdUuid(line.getEntity().getUuid());
              value.setQpc(line.getEntity().getAlcQpc());
              value.setQty(BigDecimal.ZERO);
              return value;
            });
        detail.setBizDate(signDate);
        detail.setQty(detail.getQty().add(targetLine != null ? targetLine.getSignQty() : BigDecimal.ZERO));
      }
      storeExplosive.getDetails().addAll(new ArrayList<>(explosiveDetailMap.values()));
      signDate = DateUtils.addDays(signDate, 1);
    }

    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(activity.getTenant(), null, RsH6SOPClient.class);
    log.info("调用rsH6SOPClient.createStoreExplosive促销单：{}\n{}", activity.getBillNumber(), JsonUtil.objectToJson(storeExplosive));
    rsH6SOPClient.createStoreExplosive(activity.getTenant(), storeExplosive);
  }

  @EventListener
  public void processExplosiveActivityCancelEvent(ExplosiveActivityCancelEvent event) throws BaasException {
    ExplosiveActivity activity = event.getActivity();

    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(activity.getTenant(), null, RsH6SOPClient.class);
    log.info("调用rsH6SOPClient.abortExplosive 促销单\n{}", activity.getBillNumber());
    rsH6SOPClient.abortExplosive(activity.getTenant(), new StoreExplosiveId(activity.getBillNumber()));
    rsH6SOPClient.abortActivity(activity.getTenant(), new H6SopActivityAbort(activity.getBillNumber()));
  }

  @EventListener
  public void processExplosiveActivityStopEvent(ExplosiveActivityStopEvent event) throws BaasException {
    ExplosiveActivity activity = event.getActivity();

    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(activity.getTenant(), null, RsH6SOPClient.class);
    log.info("调用rsH6SOPClient.abortExplosive 促销单\n{}", activity.getBillNumber());
    rsH6SOPClient.abortExplosive(activity.getTenant(), new StoreExplosiveId(activity.getBillNumber()));
    rsH6SOPClient.abortActivity(activity.getTenant(), new H6SopActivityAbort(activity.getBillNumber()));
  }

  @Override
  public void execute(String json, @NotNull EvCallExecutionContext evCallExecutionContext) throws Exception {
    log.info("ExplosiveForH6SOPService 爆品活动报名信息下发:{}", json);
    ExplosiveActivityAuditEvent event = JsonUtil.jsonToObject(json, ExplosiveActivityAuditEvent.class);
    if (event == null) {
      return;
    }
    ExplosiveActivity activity = event.getActivity();
    activity = explosiveActivityDao.get(
        activity.getTenant(), activity.getUuid(), ExplosiveActivity.PARTS_LINES, ExplosiveActivity.PARTS_JOIN_UNITS);
    if (activity == null) {
      return;
    }

    // 历史兼容
    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(activity.getTenant(), null, RsH6SOPClient.class);
    if (activity.getState() == ExplosiveActivity.State.canceled || activity.getState() == ExplosiveActivity.State.stopped) {
      log.info("调用rsH6SOPClient.abortExplosive 促销单\n{}", activity.getBillNumber());
      rsH6SOPClient.abortExplosive(activity.getTenant(), new StoreExplosiveId(activity.getBillNumber()));
      rsH6SOPClient.abortActivity(activity.getTenant(), new H6SopActivityAbort(activity.getBillNumber()));
    }
  }
}
