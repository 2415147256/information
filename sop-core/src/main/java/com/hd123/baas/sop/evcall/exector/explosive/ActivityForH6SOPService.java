package com.hd123.baas.sop.evcall.exector.explosive;

import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
import com.hd123.baas.sop.service.api.activity.event.PromActivityAuditEvent;
import com.hd123.baas.sop.service.api.activity.event.PromActivityCancelEvent;
import com.hd123.baas.sop.service.api.activity.event.PromActivityStopEvent;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.activity.H6ActivityType;
import com.hd123.baas.sop.remote.rsh6sop.activity.H6SopActivity;
import com.hd123.baas.sop.remote.rsh6sop.activity.H6SopActivityAbort;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.spms.commons.bean.OperateInfo;
import com.hd123.spms.commons.util.CollectionUtil;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(ActivityForH6SOPService.BEAN_ID)
public class ActivityForH6SOPService {
  public static final String BEAN_ID = "sop-service.ActivityForH6SOPService";
  public static final OperateInfo operateInfo = new OperateInfo("系统");

  @Autowired
  private FeignClientMgr feignClientMgr;

  @EventListener
  public void processPromActivityAuditEvent(PromActivityAuditEvent event) throws BaasException {
    PromActivity activity = event.getActivity();
    H6SopActivity target = new H6SopActivity();
    target.setUuid(activity.getUuid());
    target.setActivityCode(activity.getBillNumber());
    target.setActivityName(activity.getName());
    target.setAuditTime(activity.getLastModifyInfo().getTime());
    target.setBeginTime(activity.getDateRangeCondition().getDateRange().getBeginDate());
    target.setEndTime(activity.getDateRangeCondition().getDateRange().getEndDate());
    target.setMaterialAmount(activity.getMaterielAmount());
    target.setMemberExclusive(activity.getOnlyMember());
    target.setType(H6ActivityType.promotion);
    target.setStarterOrgUuid(DefaultOrgIdConvert.toH6DefOrgId(activity.getOrgId()));
    target.setPromChannels(CollectionUtil.toString(activity.getPromChannels()));
    target.setOverall(false);
    target.setNote(activity.getPromNote());

    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(activity.getTenant(), null, RsH6SOPClient.class);
    log.info("调用rsH6SOPClient.saveActivity 促销单\n{}", JsonUtil.objectToJson(activity));
    rsH6SOPClient.saveActivity(activity.getTenant(), target);
  }

  @EventListener
  public void processPromActivityCancelEvent(PromActivityCancelEvent event) throws BaasException {
    PromActivity activity = event.getActivity();
    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(activity.getTenant(), null, RsH6SOPClient.class);
    log.info("调用rsH6SOPClient.abortActivity 促销单\n{}", activity.getBillNumber());
    rsH6SOPClient.abortActivity(activity.getTenant(), new H6SopActivityAbort(activity.getBillNumber()));
  }

  @EventListener
  public void processPromActivityStopEvent(PromActivityStopEvent event) throws BaasException {
    PromActivity activity = event.getActivity();
    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(activity.getTenant(), null, RsH6SOPClient.class);
    log.info("调用rsH6SOPClient.abortActivity 促销单\n{}", activity.getBillNumber());
    rsH6SOPClient.abortActivity(activity.getTenant(), new H6SopActivityAbort(activity.getBillNumber()));
  }
}
