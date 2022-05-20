package com.hd123.baas.sop.evcall.exector.sku.publishplan;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskState;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlan;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanLine;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanScope;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanScopeType;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanState;
import com.hd123.baas.sop.service.impl.sku.publishplan.SkuPublishPlanServiceImpl;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.sku.publishplan.ShelveScheme;
import com.hd123.baas.sop.remote.rsh6sop.sku.publishplan.ShelveSchemeDtl;
import com.hd123.baas.sop.remote.rsh6sop.sku.publishplan.ShelveSchemeOff;
import com.hd123.baas.sop.remote.rsh6sop.sku.publishplan.ShelveSchemeStore;
import com.hd123.baas.sop.utils.RedisDistributedLocker;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 上下架方案推送
 * 
 * @author liuhaoxin on 2021/11/25.
 */
@Slf4j
@Component
public class SkuPublishPlanToH6EvCallExecutor extends AbstractEvCallExecutor<SkuPublishPlanToH6EvCallMsg> {

  public static final String SKU_PUBLISH_PLAN_EXECUTOR_ID = SkuPublishPlanToH6EvCallExecutor.class.getSimpleName();
  public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##########.##########");

  @Autowired
  private SkuPublishPlanServiceImpl skuPublishPlanService;
  @Autowired
  private H6TaskService h6TaskService;
  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private RedisDistributedLocker redisDistributedLocker;

  @Override
  @Tx
  protected void doExecute(SkuPublishPlanToH6EvCallMsg message, EvCallExecutionContext context) throws Exception {
    log.info("推送商品上架消息->h6");
    String uuid = message.getUuid();
    String tenant = message.getTenant();
    String taskId = message.getTaskId();
    SkuPublishPlanState state = message.getState();
    long sequenceNo = System.currentTimeMillis();

    String lockId = null;
    String key = tenant + uuid + "push";
    try {
      try {
        lockId = redisDistributedLocker.lock(key, RedisDistributedLocker.LockPolicy.wait);
      } catch (Exception e) {
        context.giveBack().delay(1000);
        return;
      }
      SkuPublishPlan plan = skuPublishPlanService.get(tenant, uuid, SkuPublishPlan.FETCH_ALL);
      if (Objects.isNull(plan)) {
        throw new BaasException("不存在商品上下架方案！");
      }
      // 上下架推送判断
      if ((!SkuPublishPlanState.SUBMITTED.equals(state)) && !SkuPublishPlanState.CANCELED.equals(state)) {
        log.info("状态传递错误,必须是上下架状态推送！该状态为{}", state);
        return;
      }
      if (!plan.getState().equals(state)) {
        log.info("用户一致性处理：状态已变更为下架,状态无需推送！上下架状态：{},推送操作状态：{}", plan.getState(), state);
        return;
      }
      if (SkuPublishPlanState.SUBMITTED.equals(state)) {
        ShelveScheme scheme = buildShelveScheme(plan);
        BaasResponse<Void> result = getH6Client(tenant).publishOn(tenant, scheme);
        if (!result.isSuccess()) {
          log.error("商品上架推送H6失败！result={}", JsonUtil.objectToJson(result));
          throw new BaasException("商品上架推送给H6失败！");
        }
      }
      if (SkuPublishPlanState.CANCELED.equals(state)) {
        ShelveSchemeOff off = new ShelveSchemeOff();
        off.setLstupdTime(message.getOperateInfo().getTime());
        off.setSchemeNo(plan.getFlowNo());
        off.setSequenceNo(sequenceNo);
        BaasResponse<Void> result = getH6Client(tenant).publishOff(tenant, off);
        if (!result.isSuccess()) {
          log.error("商品下架推送H6失败！result={}", JsonUtil.objectToJson(result));
          throw new BaasException("商品下架推送给H6失败！");
        }
      }
      h6TaskService.updateState(tenant, taskId, H6TaskState.FINISHED, message.getOperateInfo());
    } catch (Exception e) {
      log.error("SkuPublishPlanToH6EvCallExecutor错误", e);
      throw e;
    } finally {
      if (lockId != null) {
        redisDistributedLocker.unlock(key, lockId);
      }
    }
  }

  private ShelveScheme buildShelveScheme(SkuPublishPlan plan) {
    ShelveScheme scheme = new ShelveScheme();
    scheme.setSchemeNo(plan.getFlowNo());
    scheme.setBeginDate(plan.getEffectiveDate());
    scheme.setEndDate(plan.getEffectiveEndDate());
    scheme.setOrgGid(plan.getOrgId());
    scheme.setLstupdTime(new Date());
    scheme.setSequenceNo(System.currentTimeMillis());

    if (CollectionUtils.isNotEmpty(plan.getScopes())) {
      if (SkuPublishPlanScopeType.SHOP.name().equals(plan.getScopes().get(0).getOptionType())) {
        scheme.setType(0);
      } else {
        scheme.setType(1);
      }
      if ("*".equals(plan.getScopes().get(0).getOptionCode())) {
        scheme.setScopeType(0);
      } else {
        scheme.setScopeType(1);
      }
      List<ShelveSchemeStore> storeDetails = new ArrayList<>(plan.getScopes().size());
      for (SkuPublishPlanScope scope : plan.getScopes()) {
        ShelveSchemeStore schemeStore = new ShelveSchemeStore();
        schemeStore.setStoreGid(scope.getOptionUuid());
        storeDetails.add(schemeStore);
      }
      scheme.setStoreDetails(storeDetails);
    }

    if (CollectionUtils.isNotEmpty(plan.getLines())) {
      List<ShelveSchemeDtl> details = new ArrayList<>();
      for (SkuPublishPlanLine line : plan.getLines()) {
        ShelveSchemeDtl shelveSchemeDtl = new ShelveSchemeDtl();
        shelveSchemeDtl.setGdGid(line.getSkuGid());
        shelveSchemeDtl.setQpc(line.getSkuQpc());
        shelveSchemeDtl.setQpcStr("1*" + DECIMAL_FORMAT.format(line.getSkuQpc()));
        shelveSchemeDtl.setMunit(line.getSkuUnit());
        shelveSchemeDtl.setWrhGid(plan.getWrhId());
        shelveSchemeDtl.setStoreSinglePrice(line.getPriceByShop());
        shelveSchemeDtl.setStorePrice(line.getSpecPriceByShop());
        shelveSchemeDtl.setWrhSinglePrice(line.getPriceByWrh());
        shelveSchemeDtl.setWrhPrice(line.getSpecPriceByWrh());
        if (line.getLimitQty().compareTo(BigDecimal.ZERO) ==0) {
          shelveSchemeDtl.setIsLimit(0);
        } else {
          shelveSchemeDtl.setIsLimit(1);
        }
        shelveSchemeDtl.setLimitQty(line.getLimitQty().multiply(line.getSkuQpc()));
        shelveSchemeDtl.setOrigin(line.getSkuOrigin());
        shelveSchemeDtl.setNote(line.getRemark());
        details.add(shelveSchemeDtl);
      }
      scheme.setDetails(details);
    }

    return scheme;
  }

  @Override
  protected SkuPublishPlanToH6EvCallMsg decodeMessage(String msg) throws BaasException {
    log.info("商品上架推送消息SubsidyPlanMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, SkuPublishPlanToH6EvCallMsg.class);
  }

  private RsH6SOPClient getH6Client(String tenant) throws BaasException {
    return feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
  }
}
