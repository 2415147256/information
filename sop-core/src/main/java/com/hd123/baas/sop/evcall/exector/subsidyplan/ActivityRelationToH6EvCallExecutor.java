package com.hd123.baas.sop.evcall.exector.subsidyplan;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanActivityAssoc;
import com.hd123.baas.sop.service.dao.subsidyplan.ActivityAssocsDaoBof;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.subsidyplan.StorePromPlanRelate;
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
public class ActivityRelationToH6EvCallExecutor extends AbstractEvCallExecutor<ActivityRelationToH6EvCallMsg> {
  public static final String ACTIVITY_RELATION_TOH6_EXECUTOR_ID = ActivityRelationToH6EvCallExecutor.class
      .getSimpleName();

  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private ActivityAssocsDaoBof activityAssocsDao;

  @Override
  @Tx
  protected void doExecute(ActivityRelationToH6EvCallMsg message, EvCallExecutionContext context)
      throws Exception {
    List<SubsidyPlanActivityAssoc> assocs = activityAssocsDao.listByOwners(message.getTenant(), message.getOwners());

    // 推送h6终止补贴活动
    List<StorePromPlanRelate> relates = assocs.stream().map(o -> {
      StorePromPlanRelate relate = new StorePromPlanRelate();
      relate.setPlanId(o.getOwner());
      relate.setPromType(o.getActivityType().name());
      relate.setPromId(o.getActivityId());
      return relate;
    }).collect(Collectors.toList());
    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(message.getTenant(), null, RsH6SOPClient.class);
    if (CollectionUtils.isEmpty(relates)) {
      for (String owner : message.getOwners()) {
        // 清除h6的补贴计划的活动
        BaasResponse<Void> response = rsH6SOPClient.planClear(message.getTenant(), owner);
        if (!response.isSuccess()) {
          log.info("清除补贴计划活动失败");
          throw new BaasException(response.getMsg());
        }
      }
      return;
    }
    Map<String, List<StorePromPlanRelate>> relatesMap = relates.stream()
        .collect(Collectors.groupingBy(StorePromPlanRelate::getPlanId));
    for (String owner : message.getOwners()) {
      if (CollectionUtils.isEmpty(relatesMap.get(owner))) {
        // 清除h6的补贴计划的活动
        BaasResponse<Void> response = rsH6SOPClient.planClear(message.getTenant(), owner);
        if (!response.isSuccess()) {
          log.info("清除补贴计划活动失败");
          throw new BaasException(response.getMsg());
        }
      }
    }

    BaasResponse<Void> response = rsH6SOPClient.upload(message.getTenant(), relates);
    if (!response.isSuccess()) {
      throw new BaasException(response.getMsg());
    }
  }

  @Override
  protected ActivityRelationToH6EvCallMsg decodeMessage(String arg) throws BaasException {
    return JsonUtil.jsonToObject(arg, ActivityRelationToH6EvCallMsg.class);
  }
}
