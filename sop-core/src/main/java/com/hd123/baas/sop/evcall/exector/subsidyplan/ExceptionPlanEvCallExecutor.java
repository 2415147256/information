package com.hd123.baas.sop.evcall.exector.subsidyplan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlan;
import com.hd123.baas.sop.service.api.subsidyplan.SubsidyPlanState;
import com.hd123.baas.sop.service.dao.subsidyplan.SubsidyPlanDaoBof;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuhaoxin
 */
@Component
@Slf4j
public class ExceptionPlanEvCallExecutor extends AbstractEvCallExecutor<ExceptionPlanEvCallMsg> {
  public static final String PLAN_EXCEPTION_EXECUTOR_ID = ExceptionPlanEvCallExecutor.class.getSimpleName();

  @Autowired
  private SubsidyPlanDaoBof subsidyPlanDao;

  @Autowired
  private StoreService storeService;

  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @Tx
  protected void doExecute(ExceptionPlanEvCallMsg message, EvCallExecutionContext context) throws Exception {
    String tenant = message.getTenant();
    List<String> uuids = message.getUuids();
    List<SubsidyPlan> subsidyPlans = subsidyPlanDao.listByUuids(tenant, uuids);
    String orgId = subsidyPlans.get(0).getOrgId();
    List<String> shops = subsidyPlans.stream().map(SubsidyPlan::getShop).collect(Collectors.toList());

    List<SubsidyPlan> exceptionPlans = subsidyPlanDao.listByShops(tenant, orgId, shops,
        SubsidyPlanState.EXCEPTION.name());

    List<String> excepShops = exceptionPlans.stream().map(SubsidyPlan::getShop).collect(Collectors.toList());
    List<String> createShops = shops.stream().filter(o -> !excepShops.contains(o)).collect(Collectors.toList());

    if (CollectionUtils.isNotEmpty(createShops)) {
      StoreFilter filter = new StoreFilter();
      filter.setIdIn(createShops);
      filter.setPage(0);
      filter.setPageSize(0);
      QueryResult<Store> storesResult = storeService.query(message.getTenant(), filter);
      List<Store> stores = storesResult.getRecords();
      if (CollectionUtils.isNotEmpty(stores)) {
        List<SubsidyPlan> createExceptionPlans = new ArrayList<>(stores.size());
        for (Store store : stores) {
          SubsidyPlan exceptionPlan = buildExceptionPlan(message.getTenant(), orgId, store);
          createExceptionPlans.add(exceptionPlan);
        }
        subsidyPlanDao.batchSave(tenant, createExceptionPlans, SopUtils.getSysOperateInfo());
        for (SubsidyPlan subsidyPlan : createExceptionPlans) {
          // 推送异常计划
          PlanPushEvCallMsg pushEvCallMsg = new PlanPushEvCallMsg();
          pushEvCallMsg.setPlanId(subsidyPlan.getUuid());
          pushEvCallMsg.setPlanName(subsidyPlan.getPlanName());
          pushEvCallMsg.setTenant(message.getTenant());
          pushEvCallMsg.setStoreGid(Integer.valueOf(subsidyPlan.getShop()));
          pushEvCallMsg.setState(subsidyPlan.getState());
          pushEvCallMsg.setAmount(BigDecimal.ZERO);
          publisher.publishForNormal(PlanPushEvCallExecutor.PLAN_PUSH_EXECUTOR_ID, pushEvCallMsg);
        }

      }
    }

  }

  private SubsidyPlan buildExceptionPlan(String tenant, String orgId, Store store) {
    SubsidyPlan exceptionPlan = new SubsidyPlan();
    exceptionPlan.setOrgId(orgId);
    exceptionPlan.setTenant(tenant);
    exceptionPlan.setAmount(BigDecimal.ZERO);
    exceptionPlan.setUsedAmount(BigDecimal.ZERO);
    exceptionPlan.setUuid(UUID.randomUUID().toString());
    exceptionPlan.setPlanName(store.getName());
    exceptionPlan.setShop(store.getId());
    exceptionPlan.setShopCode(store.code);
    exceptionPlan.setShopName(store.getName());
    exceptionPlan.setShopType(store.getType());
    if (!Objects.isNull(store.getArea())) {
      exceptionPlan.setArea(store.getArea().getUuid());
      exceptionPlan.setAreaCode(store.getArea().getCode());
      exceptionPlan.setAreaName(store.getArea().getName());
    }
    exceptionPlan.setState(SubsidyPlanState.EXCEPTION.name());
    return exceptionPlan;
  }

  @Override
  protected ExceptionPlanEvCallMsg decodeMessage(String arg) throws BaasException {
    return JsonUtil.jsonToObject(arg, ExceptionPlanEvCallMsg.class);
  }
}
