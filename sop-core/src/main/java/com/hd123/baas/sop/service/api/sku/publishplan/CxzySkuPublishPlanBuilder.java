package com.hd123.baas.sop.service.api.sku.publishplan;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.store.SimpleStore;
import com.hd123.baas.sop.remote.rsh6sop.store.StoreFilter;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.mpa.api.common.ObjectNodeUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author liuhaoxin
 * @since 2021年12月30日
 */
@Component
@Slf4j
public class CxzySkuPublishPlanBuilder implements SkuPublishPlanBuilder {

  @Autowired
  private SkuPublishPlanService skuPublishPlanService;
  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  @Tx
  public List<SkuPublishPlan> build(String tenant, String uuid) throws BaasException {
    /* 1.查询组织下分公司虚拟仓 2.过滤组织下分公司是虚拟仓的公司:全部门店和部分门店 3.构建虚拟仓的上下架方案 */
    log.info("诚信致远-上架自动创建处理");
    SkuPublishPlan plan = skuPublishPlanService.get(tenant, uuid, SkuPublishPlan.FETCH_ALL);
    if (Objects.isNull(plan)) {
      log.info("不存在上下架计划");
      return null;
    }
    if (CollectionUtils.isEmpty(plan.getScopes())) {
      log.info("不存在商品上下架范围！");
      return null;
    }
    List<SkuPublishPlanScope> scopes = plan.getScopes()
        .stream()
        .filter(i -> "ORG".equals(i.getOptionType()))
        .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(scopes)) {
      log.info("不存在组织的商品的上下架范围！");
      return null;
    }
    // 查询总公司下的虚拟仓OrgId
    List<String> virtualWrhStoreOrgIds = getVirtualWrhStoreOrgIds(tenant, plan);

    if (CollectionUtils.isEmpty(virtualWrhStoreOrgIds)) {
      log.info("虚拟仓的分公司为空！");
      return null;
    }

    if (virtualWrhStoreOrgIds.size() > 1) {
      throw new BaasException("虚拟仓的分公司的数量大于1");
    }

    // 方案范围是虚拟仓的分公司:全部门店和部分门店
    List<String> virtualScopeOptionIds;
    if ("*".equals(scopes.get(0).getOptionUuid())) {
      virtualScopeOptionIds = virtualWrhStoreOrgIds;
    } else {
      virtualScopeOptionIds = scopes.stream().map(SkuPublishPlanScope::getOptionUuid).filter(virtualWrhStoreOrgIds::contains).collect(Collectors.toList());
    }

    // 修改发布组织自动创建的上下架方案;
    List<SkuPublishPlan> updateList = autoModifyPlanList(tenant, plan, virtualScopeOptionIds);

    // 下架并修改方案重新上架
    for (SkuPublishPlan skuPublishPlan : updateList) {
      if (SkuPublishPlanState.SUBMITTED.equals(skuPublishPlan.getState())) {
        skuPublishPlanService.off(tenant, skuPublishPlan.getUuid(), false, SopUtils.getSysOperateInfo());
        skuPublishPlan.setState(SkuPublishPlanState.CANCELED);
      }
      skuPublishPlanService.saveAndOn(tenant, skuPublishPlan.getUuid(), skuPublishPlan, SopUtils.getSysOperateInfo());
    }

    // 构建虚拟仓的上下架方案
    List<String> updateOrgIds = updateList.stream().map(SkuPublishPlan::getOrgId).distinct().collect(Collectors.toList());
    List<String> insertOrgIds = virtualScopeOptionIds.stream().filter(i -> !updateOrgIds.contains(i)).collect(Collectors.toList());
    return autoCreatePlanList(plan, insertOrgIds);
  }

  @Override
  @Tx
  public void off(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    log.info("诚信致远-下架自动下架处理");
    /*下架方案：1.查询虚拟仓下架方案  2.已经上架方案下架 3.下架方案无需处理*/
    SkuPublishPlan plan = skuPublishPlanService.get(tenant, uuid, SkuPublishPlan.FETCH_ALL);
    if (Objects.isNull(plan)) {
      log.info("不存在上下架计划");
      return;
    }
    if (CollectionUtils.isEmpty(plan.getScopes())) {
      log.info("不存在商品上下架范围！");
      return;
    }
    List<SkuPublishPlanScope> scopes = plan.getScopes()
        .stream()
        .filter(i -> "ORG".equals(i.getOptionType()))
        .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(scopes)) {
      log.info("不存在组织的商品的上下架范围！");
      return;
    }
    // 查询总公司下的虚拟仓OrgId
    List<String> virtualWrhStoreOrgIds = getVirtualWrhStoreOrgIds(tenant, plan);
    if (virtualWrhStoreOrgIds == null) {
      return;
    }
    // 方案范围是虚拟仓的分公司:全部门店和部分门店
    List<String> virtualScopeOptionIds;
    if ("*".equals(scopes.get(0).getOptionUuid())) {
      virtualScopeOptionIds = virtualWrhStoreOrgIds;
    } else {
      virtualScopeOptionIds = scopes.stream().map(SkuPublishPlanScope::getOptionUuid).filter(virtualWrhStoreOrgIds::contains).collect(Collectors.toList());
    }

    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuPublishPlan.Queries.ORG_ID, Cop.IN, virtualScopeOptionIds.toArray());
    qd.addByField(SkuPublishPlan.Queries.EFFECTIVE_DATE, Cop.EQUALS, plan.getEffectiveDate());
    QueryResult<SkuPublishPlan> result = skuPublishPlanService.query(tenant, qd);
    if (CollectionUtils.isEmpty(result.getRecords())) {
      return;
    }
    result.getRecords().stream()
        .filter(i -> "2".equals(ObjectNodeUtil.asText(i.getExt().get(SkuPublishPlan.Ext.SOURCE))))
        .forEach(i -> {
          try {
            skuPublishPlanService.off(tenant, i.getUuid(), operateInfo);
          } catch (BaasException baasException) {
            log.error("{}-方案下架失败：方案ID:{}", i.getName(), i.getUuid());
          }
        });

  }

  private List<SkuPublishPlan> autoCreatePlanList(SkuPublishPlan plan, List<String> virtualScopeOptionIds) {
    List<SkuPublishPlan> buildResult = new ArrayList<>(0);
    virtualScopeOptionIds.forEach(i -> {
      SkuPublishPlan skuPublishPlan = SkuPublishPlan.converter.convert(plan);
      skuPublishPlan.setUuid(IdGenUtils.buildRdUuid());
      skuPublishPlan.setState(SkuPublishPlanState.INIT);
      skuPublishPlan.setOrgId(i);
      ObjectNode ext = plan.getExt();
      ext.put(SkuPublishPlan.Ext.SCOPE, "全部门店");
      ext.put(SkuPublishPlan.Ext.SOURCE, "2");
      ext.put(SkuPublishPlan.Ext.SOURCE_FLOW_NO, plan.getFlowNo());
      skuPublishPlan.setExt(ext);
      skuPublishPlan.setCreateInfo(SopUtils.getSysOperateInfo());
      skuPublishPlan.setLastModifyInfo(SopUtils.getSysOperateInfo());
      skuPublishPlan.setEffectiveDate(plan.getEffectiveDate());
      skuPublishPlan.setEffectiveEndDate(plan.getEffectiveEndDate());

      List<SkuPublishPlanLine> lines = new ArrayList<>(plan.getLines().size());
      for (SkuPublishPlanLine l : plan.getLines()) {
        SkuPublishPlanLine line = SkuPublishPlanLine.converter.convert(l);
        line.setUuid(IdGenUtils.buildRdUuid());
        line.setOwner(skuPublishPlan.getUuid());
        lines.add(line);
      }
      skuPublishPlan.setLines(lines);

      List<SkuPublishPlanScope> scopes = new ArrayList<>(1);
      SkuPublishPlanScope scope = new SkuPublishPlanScope();
      scope.setOwner(skuPublishPlan.getUuid());
      scope.setOptionType("SHOP");
      scope.setOptionUuid("*");
      scope.setOptionCode("*");
      scope.setOptionName("*");
      scope.setUuid(plan.getTenant());
      scopes.add(scope);
      skuPublishPlan.setScopes(scopes);
      buildResult.add(skuPublishPlan);
    });
    return buildResult;
  }

  private List<SkuPublishPlan> autoModifyPlanList(String tenant, SkuPublishPlan plan, List<String> virtualWrhStoreOrgIds) {
    List<SkuPublishPlan> buildResult = new ArrayList<>(0);
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(SkuPublishPlan.Queries.ORG_ID, Cop.IN, virtualWrhStoreOrgIds.toArray());
    qd.addByField(SkuPublishPlan.Queries.EFFECTIVE_DATE, Cop.EQUALS, plan.getEffectiveDate());
    QueryResult<SkuPublishPlan> result = skuPublishPlanService.query(tenant, qd);
    if (CollectionUtils.isNotEmpty(result.getRecords())) {
      result.getRecords().stream().filter(i -> "2".equals(ObjectNodeUtil.asText(i.getExt().get(SkuPublishPlan.Ext.SOURCE)))).forEach(i -> {
        SkuPublishPlan skuPublishPlan = SkuPublishPlan.converter.convert(i);
        // 覆盖
        skuPublishPlan.setWrhId(plan.getWrhId());
        skuPublishPlan.setWrhCode(plan.getWrhCode());
        skuPublishPlan.setWrhName(plan.getWrhName());
        skuPublishPlan.setRemark(plan.getRemark());
        skuPublishPlan.setName(plan.getName());
        skuPublishPlan.setEffectiveDate(plan.getEffectiveDate());
        skuPublishPlan.setEffectiveEndDate(plan.getEffectiveEndDate());

        // 取总部的行数量
        ObjectNode ext = plan.getExt();
        Integer lineCnt = ObjectNodeUtil.asInt(ext.get(SkuPublishPlan.Ext.LINE_COUNT),0);
        ObjectNode targetExt = skuPublishPlan.getExt();
        targetExt.put(SkuPublishPlan.Ext.LINE_COUNT, lineCnt);
        targetExt.put(SkuPublishPlan.Ext.SCOPE, "全部门店");
        targetExt.put(SkuPublishPlan.Ext.SOURCE, "2");
        targetExt.put(SkuPublishPlan.Ext.SOURCE_FLOW_NO, plan.getFlowNo());
        skuPublishPlan.setExt(targetExt);

        skuPublishPlan.setCreateInfo(SopUtils.getSysOperateInfo());
        skuPublishPlan.setLastModifyInfo(SopUtils.getSysOperateInfo());

        skuPublishPlan.setLines(plan.getLines().stream().peek(k -> {
          k.setUuid(IdGenUtils.buildRdUuid());
          k.setOwner(skuPublishPlan.getUuid());
        }).collect(Collectors.toList()));

        List<SkuPublishPlanScope> scopes = new ArrayList<>(1);
        SkuPublishPlanScope scope = new SkuPublishPlanScope();
        scope.setTenant(tenant);
        scope.setUuid(IdGenUtils.buildRdUuid());
        scope.setOwner(skuPublishPlan.getUuid());
        scope.setOptionType("SHOP");
        scope.setOptionUuid("*");
        scope.setOptionCode("*");
        scope.setOptionName("*");
        scopes.add(scope);
        skuPublishPlan.setScopes(scopes);
        buildResult.add(skuPublishPlan);
      });
    }
    return buildResult;
  }

  private List<String> getVirtualWrhStoreOrgIds(String tenant, SkuPublishPlan plan) throws BaasException {
    // 查询组织下 虚拟仓分公司
    StoreFilter queryFilter = new StoreFilter();
    queryFilter.setTypeEquals(1);
    String orgId = DefaultOrgIdConvert.toH6DefOrgId(plan.getOrgId(), false);
    if (Objects.nonNull(orgId)) {
      queryFilter.setOrgGidEquals(Integer.valueOf(orgId));
    }
    BaasResponse<List<SimpleStore>> result = getClient(tenant).storeQuery(tenant, queryFilter);
    if (!result.isSuccess()) {
      log.error("调用h6组织/门店查询接口失败！response={}", JsonUtil.objectToJson(result));
      throw new BaasException("调用h6组织/门店查询接口失败！");
    }
    if (CollectionUtils.isEmpty(result.getData())) {
      log.info("该组织不存在分公司;orgId{}", plan.getOrgId());
      return null;
    }
    List<SimpleStore> virtualWrhStores = result.getData()
        .stream()
        .filter(i -> Boolean.TRUE.equals(i.getHasVirtualWrh()))
        .collect(Collectors.toList());
    return virtualWrhStores.stream().map(i -> String.valueOf(i.getGid())).collect(Collectors.toList());
  }

  private RsH6SOPClient getClient(String tenant) throws BaasException {
    return feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
  }

  public static void main(String[] args) {
    SkuPublishPlan plan = new SkuPublishPlan();
    plan.setScopes(new ArrayList<>());
    SkuPublishPlanScope scope = new SkuPublishPlanScope();
    scope.setOptionType("ORG");
    plan.getScopes().add(scope);

    List<SkuPublishPlanScope> scopes = plan.getScopes()
        .stream()
        .filter(i -> "ORG".equals(i.getOptionType()))
        .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(scopes)) {
      log.info("不存在组织的商品的上下架范围！");
      System.out.println("2");
    }
    System.out.println("1");
  }
}
