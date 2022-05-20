package com.hd123.baas.sop.service.impl.sku.publishplan;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.Inv.InvService;
import com.hd123.baas.sop.service.api.Inv.StoreType;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlan;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanLine;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanScope;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanService;
import com.hd123.baas.sop.service.api.sku.publishplan.SkuPublishPlanState;
import com.hd123.baas.sop.service.dao.sku.publishplan.SkuPublishPlanDaoBof;
import com.hd123.baas.sop.service.dao.sku.publishplan.SkuPublishPlanLineDaoBof;
import com.hd123.baas.sop.service.dao.sku.publishplan.SkuPublishPlanScopeDaoBof;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.sku.publishplan.SkuPublishPlanAutoCreateEvCallEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.sku.publishplan.SkuPublishPlanAutoCreateEvCallMsg;
import com.hd123.baas.sop.evcall.exector.sku.publishplan.SkuPublishPlanAutoOffEvCallEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.sku.publishplan.SkuPublishPlanAutoOffEvCallMsg;
import com.hd123.baas.sop.evcall.exector.sku.publishplan.SkuPublishPlanToH6EvCallExecutor;
import com.hd123.baas.sop.evcall.exector.sku.publishplan.SkuPublishPlanToH6EvCallMsg;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.inv.AvailableInv;
import com.hd123.baas.sop.remote.rsh6sop.inv.AvailableInvFilter;
import com.hd123.baas.sop.remote.rsh6sop.sku.publishplan.AvgReqQty;
import com.hd123.baas.sop.remote.rsh6sop.sku.publishplan.AvgReqQtyFilter;
import com.hd123.baas.sop.remote.rsh6sop.store.SimpleStore;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.mpa.api.common.ObjectNodeUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author liuhaoxin
 * @since 2021-11-24
 */
@Service
@Slf4j
public class SkuPublishPlanServiceImpl implements SkuPublishPlanService {
  private static final ThreadLocal<DateFormat> SDF = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

  @Autowired
  private SkuPublishPlanDaoBof skuPublishPlanDao;
  @Autowired
  private SkuPublishPlanLineDaoBof skuPublishPlanLineDao;
  @Autowired
  private SkuPublishPlanScopeDaoBof skuPublishPlanScopeDao;
  @Autowired
  private SkuBillNumberMgr skuBillNumberMgr;
  @Autowired
  private H6TaskService h6TaskService;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private InvService invService;

  @Override
  @Tx
  public String saveNew(String tenant, SkuPublishPlan skuPublishPlan, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.notNull(skuPublishPlan, "商品上下架信息");
    //名称不能重复
    checkRepeatNamePlan(tenant, skuPublishPlan);
    // 一品一规 商品行不能重复
    List<SkuPublishPlanLine> repeatLines = checkLines(tenant, skuPublishPlan.getLines());
    if (CollectionUtils.isNotEmpty(repeatLines)) {
      throw new BaasException("有重复商品,无法保存");
    }

    buildPublishPlan(tenant, skuPublishPlan);

    skuPublishPlanDao.insert(tenant, skuPublishPlan, operateInfo);
    skuPublishPlanLineDao.batchInsert(tenant, skuPublishPlan.getLines());
    skuPublishPlanScopeDao.batchInsert(tenant, skuPublishPlan.getScopes());
    return skuPublishPlan.getUuid();

  }

  @Override
  @Tx
  public String saveModify(String tenant, SkuPublishPlan skuPublishPlan, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.notNull(skuPublishPlan, "商品上下架信息");
    //名称不能重复
    checkRepeatNamePlan(tenant, skuPublishPlan);
    // 一品一规 商品行不能重复
    List<SkuPublishPlanLine> repeatLines = checkLines(tenant, skuPublishPlan.getLines());
    if (CollectionUtils.isNotEmpty(repeatLines)) {
      throw new BaasException("有重复商品,无法保存");
    }

    SkuPublishPlan history = skuPublishPlanDao.get(tenant, skuPublishPlan.getUuid());
    if (Objects.isNull(history)) {
      throw new BaasException("不存在上下架方案！");
    }
    if (SkuPublishPlanState.EXPIRED.equals(history.getState())) {
      throw new BaasException("该方案已过期，无法编辑!");
    }
    buildUpdatePublishPlan(tenant, history, skuPublishPlan);

    // 商品上下架方案更新
    skuPublishPlanDao.update(tenant, skuPublishPlan, operateInfo);
    // 商品行更新
    skuPublishPlanLineDao.deleteByOwner(tenant, history.getUuid());
    skuPublishPlanLineDao.batchInsert(tenant, skuPublishPlan.getLines());
    // 更新门店信息
    skuPublishPlanScopeDao.deleteByOwner(tenant, history.getUuid());
    skuPublishPlanScopeDao.batchInsert(tenant, skuPublishPlan.getScopes());
    if (SkuPublishPlanState.SUBMITTED.equals(history.getState())) {
      pushToH6Message(tenant, skuPublishPlan.getUuid(), SkuPublishPlanState.SUBMITTED, operateInfo);
      pushAutoCreateMessage(tenant, skuPublishPlan.getUuid(), operateInfo);
    }
    return skuPublishPlan.getUuid();
  }

  @Override
  @Tx
  public void on(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.hasText(uuid, "uuid");

    SkuPublishPlan plan = get(tenant, uuid, SkuPublishPlan.FETCH_SCOPE);
    if (Objects.isNull(plan)) {
      throw new BaasException("不存在上下架方案！");
    }
    if (SkuPublishPlanState.SUBMITTED.equals(plan.getState())) {
      log.info("商品上下架方案已上架。上下架方案id:{}", uuid);
      return;
    }
    if (!SkuPublishPlanState.INIT.equals(plan.getState())
        && !SkuPublishPlanState.CANCELED.equals(plan.getState())) {
      throw new BaasException("该状态无法上架！");
    }

    List<SkuPublishPlan> plans = skuPublishPlanDao.listByState(tenant, plan.getOrgId(),
        SkuPublishPlanState.SUBMITTED.name());

    // 一个叫货组织只能有一个上架方案
    if (CollectionUtils.isNotEmpty(plans)) {
      List<String> owners = plans.stream().map(SkuPublishPlan::getUuid).collect(Collectors.toList());
      List<SkuPublishPlanScope> historyScopes = skuPublishPlanScopeDao.listByOwners(tenant, owners);
      List<SkuPublishPlanScope> conflictScopes = checkPublishPlanOn(tenant, plan.getOrgId(), historyScopes,
          plan.getScopes(), plan.getScopes().get(0).getOptionType());
      if (CollectionUtils.isNotEmpty(conflictScopes)) {
        throw new BaasException("一个叫货组织只能有一个上架方案");
      }
    }

    skuPublishPlanDao.updateState(tenant, plan.getUuid(), SkuPublishPlanState.SUBMITTED.name(), operateInfo);
    pushToH6Message(tenant, uuid, SkuPublishPlanState.SUBMITTED, operateInfo);
    pushAutoCreateMessage(tenant, uuid, operateInfo);

  }

  @Override
  @Tx
  public void off(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    off(tenant, uuid, true, operateInfo);
  }

  @Override
  @Tx
  public void off(String tenant, String uuid, boolean push, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.hasText(uuid, "uuid");

    SkuPublishPlan plan = skuPublishPlanDao.get(tenant, uuid);
    if (Objects.isNull(plan)) {
      throw new BaasException("不存在上下架方案！");
    }
    if (SkuPublishPlanState.CANCELED.equals(plan.getState())) {
      return;
    }
    if (!SkuPublishPlanState.SUBMITTED.equals(plan.getState())) {
      throw new BaasException("该状态无法下架");
    }
    skuPublishPlanDao.updateState(tenant, uuid, SkuPublishPlanState.CANCELED.name(), operateInfo);
    if (push) {
      pushToH6Message(tenant, uuid, SkuPublishPlanState.CANCELED, operateInfo);
      pushAutoOffMessage(tenant, uuid, operateInfo);
    }
  }

  @Override
  @Tx
  public void saveAndOn(String tenant, String uuid, SkuPublishPlan skuPublishPlan, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.notNull(skuPublishPlan, "商品上下架信息");

    SkuPublishPlan history = skuPublishPlanDao.get(tenant, uuid);
    if (Objects.isNull(history)) {
      saveNew(tenant, skuPublishPlan, operateInfo);
    } else {
      saveModify(tenant, skuPublishPlan, operateInfo);
    }
    on(tenant, uuid, operateInfo);
  }

  @Override
  @Tx
  public void refreshLines(String tenant, String uuid) throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.notNull(uuid, "商品上下架ID");

    SkuPublishPlan plan = skuPublishPlanDao.get(tenant, uuid);
    if (Objects.isNull(plan)) {
      throw new BaasException("不存在上下架方案！");
    }
    List<SkuPublishPlanLine> lines = skuPublishPlanLineDao.listByOwner(tenant, uuid);
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    List<AvailableInv> invList = listAvailableInv(tenant, plan.getOrgId(), plan.getWrhId());
    Map<String, AvailableInv> invMap;
    if (CollectionUtils.isNotEmpty(invList)) {
      invMap = invList.stream().collect(Collectors.toMap(i -> String.valueOf(i.getGdGid()), i -> i));
    } else {
      invMap = new HashMap<>(0);
    }
    for (SkuPublishPlanLine line : lines) {
      AvailableInv inv = invMap.get(line.getSkuGid());
      ObjectNode ext = line.getExt();
      if (!Objects.isNull(inv)) {
        ext.put(SkuPublishPlanLine.Ext.WRH_QTY, inv.getQtyCount());
        ext.put(SkuPublishPlanLine.Ext.WRH_SHIPPING_QTY, inv.getWayQtyCount());
        ext.put(SkuPublishPlanLine.Ext.WRH_TOTAL_QTY, inv.getQtyCount().add(inv.getWayQtyCount()));
      } else {
        ext.put(SkuPublishPlanLine.Ext.WRH_QTY, 0);
        ext.put(SkuPublishPlanLine.Ext.WRH_SHIPPING_QTY, 0);
        ext.put(SkuPublishPlanLine.Ext.WRH_TOTAL_QTY, 0);
        line.setExt(ext);
      }
    }
    skuPublishPlanLineDao.deleteByOwner(tenant, uuid);
    skuPublishPlanLineDao.batchInsert(tenant, lines);
  }

  @Override
  @Tx
  public void remove(String tenant, String uuid) throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.notNull(uuid, "商品上下架ID");

    SkuPublishPlan plan = skuPublishPlanDao.get(tenant, uuid);
    if (Objects.isNull(plan)) {
      throw new BaasException("不存在上下架方案！");
    }
    if (!SkuPublishPlanState.INIT.equals(plan.getState())) {
      throw new BaasException("该状态无法删除！");
    }
    skuPublishPlanDao.delete(tenant, uuid);
    skuPublishPlanLineDao.deleteByOwner(tenant, uuid);
    skuPublishPlanScopeDao.deleteByOwner(tenant, uuid);
  }

  @Override
  public SkuPublishPlan get(String tenant, String uuid, String... fetchParts) {
    Assert.hasText(tenant, "租户");
    Assert.notNull(uuid, "uuid");

    SkuPublishPlan plan = skuPublishPlanDao.get(tenant, uuid);
    fetchParts(tenant, plan, fetchParts);
    return plan;
  }

  @Override
  public QueryResult<SkuPublishPlan> query(String tenant, QueryDefinition qd, String... fetchParts) {
    Assert.hasText(tenant, "租户");
    Assert.notNull(qd, "qd");

    QueryResult<SkuPublishPlan> result = skuPublishPlanDao.query(tenant, qd);

    List<SkuPublishPlan> list = result.getRecords();
    fetchParts(tenant, list, fetchParts);
    result.setRecords(list);

    return result;
  }

  @Override
  public QueryResult<SkuPublishPlanLine> queryLines(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "租户");
    Assert.notNull(qd, "qd");

    return skuPublishPlanLineDao.query(tenant, qd);
  }

  @Override
  public List<SkuPublishPlan> listByStates(String tenant, SkuPublishPlanState... states) {
    Assert.hasText(tenant, "租户");
    Assert.notNull(states, "state");
    String[] planStates = new String[states.length];
    if (ArrayUtils.isNotEmpty(states)) {
      for (int i = 0; i < states.length; i++) {
        planStates[i] = states[i].name();
      }
    }
    return skuPublishPlanDao.listByStates(tenant, planStates);
  }

  @Override
  @Tx
  public void expire(String tenant, List<String> uuids, OperateInfo operateInfo) {
    Assert.hasText(tenant, "租户");
    Assert.notNull(uuids, "uuids");

    skuPublishPlanDao.updateStateByUuid(tenant, uuids, SkuPublishPlanState.EXPIRED.name(), operateInfo);
  }

  @Override
  public List<SkuPublishPlanScope> checkScopes(String tenant, String orgId, List<SkuPublishPlanScope> scopes)
      throws BaasException {
    Assert.hasText(tenant, "租户");
    Assert.notNull(orgId, "orgId");
    Assert.notEmpty(scopes, "scopes");

    List<SkuPublishPlan> plans = skuPublishPlanDao.listByState(tenant, orgId, SkuPublishPlanState.SUBMITTED.name());

    // 一个叫货组织只能有一个上架方案
    if (CollectionUtils.isEmpty(plans)) {
      return new ArrayList<>();
    }
    List<String> owners = plans.stream().map(SkuPublishPlan::getUuid).collect(Collectors.toList());
    List<SkuPublishPlanScope> historyScopes = skuPublishPlanScopeDao.listByOwners(tenant, owners);

    return checkPublishPlanOn(tenant, orgId, historyScopes, scopes, scopes.get(0).getOptionType());
  }

  @Override
  public List<SkuPublishPlanLine> checkLines(String tenant, List<SkuPublishPlanLine> lines) {
    Assert.hasText(tenant, "租户");
    Assert.notEmpty(lines, "lines");
    // 校验是否有重复商品（通过商品唯一ID判断），有则弹窗报错保存失败，并告知哪个商品重复
    return checkRepeatLines(lines);
  }

  private List<SkuPublishPlanLine> checkRepeatLines(List<SkuPublishPlanLine> lines) {
    Map<String, List<SkuPublishPlanLine>> planMap = lines.stream().collect(Collectors.groupingBy(SkuPublishPlanLine::getSkuCode));

    List<SkuPublishPlanLine> repeatLine = new ArrayList<>();
    for (Map.Entry<String, List<SkuPublishPlanLine>> item : planMap.entrySet()) {
      List<SkuPublishPlanLine> value = item.getValue();
      if (value.size() > 1) {
        repeatLine.add(value.get(0));
      }
    }
    return repeatLine;
  }

  private void checkRepeatNamePlan(String tenant, SkuPublishPlan skuPublishPlan) throws BaasException {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuPublishPlan.Queries.ORG_ID, Cop.EQUALS, skuPublishPlan.getOrgId());
    qd.addByField(SkuPublishPlan.Queries.UUID, Cop.not(Cop.EQUALS), skuPublishPlan.getUuid());
    qd.addByField(SkuPublishPlan.Queries.NAME, Cop.EQUALS, skuPublishPlan.getName());
    QueryResult<SkuPublishPlan> repeatNamePlan = query(tenant, qd);
    if (repeatNamePlan.getRecordCount() > 0) {
      log.error("{}-上下架方案名称重复", skuPublishPlan.getName());
      throw new BaasException("上架方案名称不允许重复");
    }
  }

  private void pushAutoOffMessage(String tenant, String uuid, OperateInfo operateInfo) {
    SkuPublishPlanAutoOffEvCallMsg msg = new SkuPublishPlanAutoOffEvCallMsg();
    msg.setTenant(tenant);
    msg.setUuid(uuid);
    msg.setOperateInfo(operateInfo);
    publisher.publishForNormal(SkuPublishPlanAutoOffEvCallEvCallExecutor.SKU_PUBLISH_PLAN_AUTO_OFF_EXECUTOR_ID,
        msg);
  }

  private void pushAutoCreateMessage(String tenant, String uuid, OperateInfo operateInfo) {
    SkuPublishPlanAutoCreateEvCallMsg msg = new SkuPublishPlanAutoCreateEvCallMsg();
    msg.setTenant(tenant);
    msg.setUuid(uuid);
    msg.setOperateInfo(operateInfo);
    publisher.publishForNormal(SkuPublishPlanAutoCreateEvCallEvCallExecutor.SKU_PUBLISH_PLAN_AUTO_CREATE_EXECUTOR_ID,
        msg);
  }

  private List<SkuPublishPlanScope> checkPublishPlanOn(String tenant, String orgId,
      List<SkuPublishPlanScope> historyScopes, List<SkuPublishPlanScope> scopes, String optionType)
      throws BaasException {
    /*
     * 上架检测情况：1、历史范围选择全部，现在范围全部冲突； 2、历史范围有的，现在范围选择全部，历史范围冲突；3、历史范围有的，现在范围不能存在；
     */
    if (CollectionUtils.isEmpty(scopes)) {
      return new ArrayList<>();
    }
    List<SkuPublishPlanScope> conflictScopes = new ArrayList<>();
    // Map<OptionUuid,SkuPublishPlanScope>
    Map<String, SkuPublishPlanScope> scopeMap = scopes.stream()
        .collect(Collectors.toMap(SkuPublishPlanScope::getOptionUuid, i -> i));

    List<SkuPublishPlanScope> typeScopes;
    if ("ORG".equals(optionType)) {
      typeScopes = historyScopes.stream().filter(i -> "ORG".equals(i.getOptionType())).collect(Collectors.toList());
    } else {
      typeScopes = historyScopes.stream().filter(i -> "SHOP".equals(i.getOptionType())).collect(Collectors.toList());
    }
    List<String> scopeUuids = scopes.stream().map(SkuPublishPlanScope::getOptionUuid).collect(Collectors.toList());
    List<String> typeScopeUuids = typeScopes.stream()
        .map(SkuPublishPlanScope::getOptionUuid)
        .collect(Collectors.toList());

    // 1、历史范围选择全部、现在范围选择全部
    if (typeScopeUuids.contains("*") && scopeUuids.contains("*")) {
      QueryResult<SimpleStore> result;
      if ("ORG".equals(optionType)) {
        result = invService.storeQuery(tenant, orgId, StoreType.ORG);
      } else {
        result = invService.storeQuery(tenant, orgId, StoreType.SHOP);
      }
      for (SimpleStore item : result.getRecords()) {
        SkuPublishPlanScope scope = new SkuPublishPlanScope();
        scope.setOptionUuid(String.valueOf(item.getGid()));
        scope.setOptionCode(item.getCode());
        scope.setOptionName(item.getName());
        scope.setOptionType(optionType);
        conflictScopes.add(scope);
      }
      return conflictScopes;
    }
    // 2、历史范围选择全部，现在范围全部冲突；
    if (typeScopeUuids.contains("*")) {
      if (CollectionUtils.isNotEmpty(scopes)) {
        return scopes;
      }
    }
    // 3、历史范围有的，现在范围选择全部，历史范围冲突；
    if (scopeUuids.contains("*")) {
      if (CollectionUtils.isNotEmpty(typeScopes)) {
        return typeScopes;
      }
    }
    // 4、历史范围有的，现在范围不能存在；
    for (SkuPublishPlanScope item : typeScopes) {
      SkuPublishPlanScope scope = scopeMap.get(item.getOptionUuid());
      if (Objects.nonNull(scope)) {
        conflictScopes.add(scope);
      }
    }
    return conflictScopes;
  }

  private void buildPublishPlan(String tenant, SkuPublishPlan plan) throws BaasException {
    if (StringUtils.isBlank(plan.getUuid())) {
      plan.setUuid(IdGenUtils.buildRdUuid());
    }
    if (Objects.isNull(plan.getState())) {
      plan.setState(SkuPublishPlanState.INIT);
    }
    if (plan.getEffectiveDate() == null) {
      // 格式：2020-01-02 00:00:00
      plan.setEffectiveDate(DateUtils.truncate(new Date(), Calendar.DATE));
    }
    if (plan.getEffectiveEndDate() == null) {
      // 格式：2020-01-02 23:59:59
      Date endDate = DateUtils.addDays(new Date(), 1);
      endDate = DateUtils.truncate(endDate, Calendar.DATE);
      endDate = DateUtils.addSeconds(endDate, -1);
      plan.setEffectiveEndDate(endDate);
    }
    plan.setFlowNo(skuBillNumberMgr.generateSkuPublishPlan(tenant));
    plan.setState(plan.getState());
    plan.setExt(plan.getExt());

    // 商品上下架商品行
    setPlanLines(tenant, plan);
    // 门店范围
    List<SkuPublishPlanScope> scopes = plan.getScopes();
    for (SkuPublishPlanScope scope : scopes) {
      scope.setUuid(IdGenUtils.buildRdUuid());
      scope.setOwner(plan.getUuid());
    }
    plan.setScopes(scopes);
  }

  private void setPlanLines(String tenant, SkuPublishPlan plan) throws BaasException {
    List<SkuPublishPlanLine> lines = plan.getLines();
    if (CollectionUtils.isNotEmpty(lines)) {
      // 查询近5日日均叫货量
      List<Integer> gdGids = lines.stream().map(i -> Integer.valueOf(i.getSkuGid())).collect(Collectors.toList());
      List<AvgReqQty> avgReqQtyList = listAvgReqQty(tenant, plan.getOrgId(), plan.getScopes().get(0).getOptionType(), gdGids);
      // Map<gdGid,AvgReqQty>
      Map<String, AvgReqQty> avgReqQtyMap = new HashMap<>(0);
      if (CollectionUtils.isNotEmpty(avgReqQtyList)) {
        avgReqQtyMap = avgReqQtyList.stream().collect(Collectors.toMap(i -> String.valueOf(i.getGdGid()), i -> i));
      }
      // 查询可用库存
      List<AvailableInv> invList = listAvailableInv(tenant, plan.getOrgId(), plan.getWrhId());
      Map<String, AvailableInv> invMap = new HashMap<>(0);
      if (CollectionUtils.isNotEmpty(invList)) {
        invMap = invList.stream().collect(Collectors.toMap(i -> String.valueOf(i.getGdGid()), i -> i));
      }
      // 设置商品上下架行信息：
      for (SkuPublishPlanLine line : lines) {
        if (StringUtils.isBlank(line.getUuid())) {
          line.setUuid(IdGenUtils.buildRdUuid());
        }
        line.setOwner(plan.getUuid());
        AvgReqQty avgReqQty = avgReqQtyMap.get(line.getSkuGid());
        AvailableInv inv = invMap.get(line.getSkuGid());
        setLineExt(line, avgReqQty, inv);
      }
      plan.setLines(lines);
    }
  }

  private List<AvailableInv> listAvailableInv(String tenant, String orgId, String wrhId) throws BaasException {
    AvailableInvFilter queryFilter = new AvailableInvFilter();
//    String h6OrgId = DefaultOrgIdConvert.toH6DefOrgId(orgId, false);
//    if (Objects.nonNull(h6OrgId)) {
//      queryFilter.setOrgGidEquals(Integer.valueOf(h6OrgId));
//    }
    queryFilter.setWrhGidEquals(Integer.valueOf(wrhId));
    BaasResponse<List<AvailableInv>> invResult = getRsH6SOPClient(tenant).invQuery(tenant, queryFilter);
    if (!invResult.isSuccess()) {
      throw new BaasException("查询库存信息失败");
    }
    return invResult.getData();
  }

  private RsH6SOPClient getRsH6SOPClient(String tenant) throws BaasException {
    return feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
  }

  private List<AvgReqQty> listAvgReqQty(String tenant, String orgId, String optionType, List<Integer> gdGids) throws BaasException {
    AvgReqQtyFilter avgReqQtyFilter = new AvgReqQtyFilter();
    avgReqQtyFilter.setDaysEquals(5);
    avgReqQtyFilter.setGdGidIn(gdGids);
    avgReqQtyFilter.setOrgGidEquals(Integer.valueOf(DefaultOrgIdConvert.toH6DefOrgId(orgId)));
    // 根据orgId确认是门店叫货还是公司叫货;总公司：公司叫货 分公司：门店叫货
    if ("ORG".equals(optionType)) {
      avgReqQtyFilter.setTypeEquals(1);
    }
    BaasResponse<List<AvgReqQty>> avgReqQtyResult = getRsH6SOPClient(tenant).listAvgReqQty(tenant, avgReqQtyFilter);
    if (!avgReqQtyResult.isSuccess()) {
      log.info("查询日均交货量失败！result={}", JsonUtil.objectToJson(avgReqQtyResult));
      throw new BaasException("查询近五日均叫货量失败！");
    }
    return avgReqQtyResult.getData();
  }

  private void fetchParts(String tenant, SkuPublishPlan plan, String[] fetchParts) {
    if (Objects.isNull(plan) || ArrayUtils.isEmpty(fetchParts)) {
      return;
    }
    List<SkuPublishPlan> plans = new ArrayList<>(0);
    plans.add(plan);
    fetchParts(tenant, plans, fetchParts);
  }

  private void fetchParts(String tenant, List<SkuPublishPlan> plans, String[] fetchParts) {
    if (CollectionUtils.isEmpty(plans) || ArrayUtils.isEmpty(fetchParts)) {
      return;
    }
    if (ArrayUtils.contains(fetchParts, SkuPublishPlan.FETCH_LINE)) {
      QueryDefinition qd = new QueryDefinition();
      qd.addByField(SkuPublishPlanLine.Queries.OWNER, Cop.IN,
          plans.stream().map(SkuPublishPlan::getUuid).toArray());
      QueryResult<SkuPublishPlanLine> result = skuPublishPlanLineDao.query(tenant, qd);
      if (CollectionUtils.isNotEmpty(result.getRecords())) {
        Map<String, List<SkuPublishPlanLine>> lineMap = result.getRecords()
            .stream()
            .collect(Collectors.groupingBy(SkuPublishPlanLine::getOwner));
        plans.forEach(i -> i.setLines(lineMap.get(i.getUuid())));
      }
    }
    if (ArrayUtils.contains(fetchParts, SkuPublishPlan.FETCH_SCOPE)) {
      QueryDefinition qd = new QueryDefinition();
      qd.addByField(SkuPublishPlanScope.Queries.OWNER, Cop.IN,
          plans.stream().map(SkuPublishPlan::getUuid).toArray());
      QueryResult<SkuPublishPlanScope> result = skuPublishPlanScopeDao.query(tenant, qd);
      if (CollectionUtils.isNotEmpty(result.getRecords())) {
        Map<String, List<SkuPublishPlanScope>> scopeMap = result.getRecords()
            .stream()
            .collect(Collectors.groupingBy(SkuPublishPlanScope::getOwner));
        plans.forEach(i -> i.setScopes(scopeMap.get(i.getUuid())));
      }

    }
  }

  private void pushToH6Message(String tenant, String uuid, SkuPublishPlanState state, OperateInfo operateInfo)
      throws BaasException {
    SkuPublishPlan plan = skuPublishPlanDao.get(tenant, uuid);
    H6Task h6Task = new H6Task();
    h6Task.setOrgId(plan.getOrgId());
    h6Task.setType(H6TaskType.SKU_PUBLISH_PLAN);
    h6Task.setFlowNo(plan.getFlowNo());
    h6Task.setExecuteDate(new Date());
    String taskId = h6TaskService.init(tenant, h6Task, operateInfo);
    // 异步推送商品上下架方案
    SkuPublishPlanToH6EvCallMsg msg = new SkuPublishPlanToH6EvCallMsg();
    msg.setTenant(tenant);
    msg.setUuid(plan.getUuid());
    msg.setTaskId(taskId);
    msg.setState(state);
    msg.setOperateInfo(operateInfo);
    publisher.publishForNormal(SkuPublishPlanToH6EvCallExecutor.SKU_PUBLISH_PLAN_EXECUTOR_ID, msg);
  }

  private void buildUpdatePublishPlan(String tenant, SkuPublishPlan history, SkuPublishPlan source)
      throws BaasException {
    source.setFlowNo(history.getFlowNo());
    source.setState(history.getState());
    source.setCreateInfo(history.getCreateInfo());
    // 商品上下架商品行 需要调用H6获取近5日日均叫货量、库存总数、可用库存件数、在途库存件数
    setPlanLines(tenant, source);
    List<SkuPublishPlanScope> scopes = source.getScopes();
    for (SkuPublishPlanScope scope : scopes) {
      if (StringUtils.isBlank(scope.getUuid())) {
        scope.setUuid(IdGenUtils.buildRdUuid());
      }
      scope.setOwner(history.getUuid());
    }
    source.setScopes(scopes);
  }

  private void setLineExt(SkuPublishPlanLine line, AvgReqQty avgReqQty, AvailableInv inv) {
    ObjectNode ext = ObjectNodeUtil.createObjectNode();
    if (!Objects.isNull(avgReqQty)) {
      ext.put(SkuPublishPlanLine.Ext.AVG_ORDER_QTY, avgReqQty.getAvgReqQty());
    } else {
      ext.put(SkuPublishPlanLine.Ext.AVG_ORDER_QTY, 0);
    }
    if (!Objects.isNull(inv)) {
      ext.put(SkuPublishPlanLine.Ext.WRH_QTY, inv.getQtyCount());
      ext.put(SkuPublishPlanLine.Ext.WRH_SHIPPING_QTY, inv.getWayQtyCount());
      ext.put(SkuPublishPlanLine.Ext.WRH_TOTAL_QTY, inv.getQtyCount().add(inv.getWayQtyCount()));
    } else {
      ext.put(SkuPublishPlanLine.Ext.WRH_QTY, 0);
      ext.put(SkuPublishPlanLine.Ext.WRH_SHIPPING_QTY, 0);
      ext.put(SkuPublishPlanLine.Ext.WRH_TOTAL_QTY, 0);
    }
    line.setExt(ext);
  }
}
