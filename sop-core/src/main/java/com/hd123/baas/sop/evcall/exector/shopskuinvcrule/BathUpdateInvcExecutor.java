/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	transfer 文件名：	GoodsExecutor.java 模块说明： 修改历史： 2020/7/17 - seven - 创建。
 */
package com.hd123.baas.sop.evcall.exector.shopskuinvcrule;

import com.hd123.baas.sop.utils.ApplicationContextUtils;
import com.hd123.baas.sop.service.api.basedata.shopskuinvcrule.ShopSkuInvRuleBatchUpdateRequest;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.shopsku.ShopSkuMsg;
import com.hd123.baas.sop.remote.rsmas.RsIdCodeName;
import com.hd123.baas.sop.remote.rsmas.RsMasClient;
import com.hd123.baas.sop.remote.rsmas.RsMasPageResponse;
import com.hd123.baas.sop.remote.rsmas.RsMasRequest;
import com.hd123.baas.sop.remote.rsmas.RsMasResponse;
import com.hd123.baas.sop.remote.rsmas.RsParameter;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSku;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSkuFilter;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsBatchSaveShopSkuInvRule;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsShopRange;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsShopSkuInvRule;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsShopSkuInvRuleCond;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsShopSkuInvRuleDef;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsShopSkuInvRuleFilter;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsSkuRange;
import com.hd123.baas.sop.remote.rsmas.sku.RsSku;
import com.hd123.baas.sop.remote.rsmas.sku.RsSkuFilter;
import com.hd123.baas.sop.remote.rsmas.store.RsStore;
import com.hd123.baas.sop.remote.rsmas.store.RsStoreFilter;
import com.hd123.baas.sop.remote.rsmas.task.ReportType;
import com.hd123.baas.sop.remote.rsmas.task.RsExecuteResult;
import com.hd123.baas.sop.remote.rsmas.task.RsTask;
import com.hd123.baas.sop.remote.rsmas.task.RsTaskExecuteReport;
import com.hd123.baas.sop.remote.rsmas.task.RsTaskStatus;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.spms.commons.json.JsonUtil;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author seven
 */
@Slf4j
@Component
public class BathUpdateInvcExecutor extends AbstractEvCallExecutor<ShopSkuMsg> {
  public static final String SHOP_SKU_EXECUTOR_ID = BathUpdateInvcExecutor.class.getSimpleName();

  private final static String PARAM_REQUESTBODY = "requestBosy";
  private static final String QTY = "qty";
  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final String DEFAULT_TYPE = "fixedShopSkuQt"; //非初期库存类型
  private static final String DEFAULT_NAME = "每日库存";
  private static final Boolean DEFAULT_ENABLED = true;
  private static final String DEFAULT_LIMIT_TYPE = "include";
  private static final Boolean DEFAULT_LIMIT = true;

  @Override
  protected ShopSkuMsg decodeMessage(String msg) throws BaasException {
    log.info("收到ShopSkuMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ShopSkuMsg.class);
  }

  @Override
  public void doExecute(ShopSkuMsg msg, @NotNull EvCallExecutionContext context) throws Exception {
    try {
      RsTask task = getTask(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId());
      if (task == null) {
        log.error("租户:{},获取任务{}详情不存在", msg.getTenant(), msg.getTaskId());
        throw new IllegalArgumentException(
            "租户" + msg.getTenant() + "获取任务" + msg.getTaskId() + "详情不存在");
      }
      if (task.getStatus().equals(RsTaskStatus.canceled)
          || task.getStatus().equals(RsTaskStatus.ended)) {
        log.error("租户:{},获取任务{}已结束或者已取消{}", msg.getTenant(), msg.getTaskId(),
            task.getStatus().name());
        return;
      }
      reportTaskStart(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId(), msg.getOperator());
      ShopSkuInvRuleBatchUpdateRequest batchUpdateRequest = null;
      for (RsParameter parameter : task.getParameters()) {
        if (PARAM_REQUESTBODY.equals(parameter.getName())) {
          batchUpdateRequest = JsonUtil.jsonToObject(parameter.getValue(), ShopSkuInvRuleBatchUpdateRequest.class);
        }
      }
      if (batchUpdateRequest == null) {
        log.error("租户{}-任务{}参数{}不存在", msg.getTenant(), msg.getTaskId(), PARAM_REQUESTBODY);
        throw new IllegalArgumentException(
            "租户" + msg.getTenant() + "-任务" + msg.getTaskId() + "参数" + PARAM_REQUESTBODY + "不存在");
      }
      doExecuteShopSkuInvc(msg, batchUpdateRequest);
      reportTaskProcessFinish(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId(), msg.getOperator());
    } catch (Exception e) {
      log.error("租户:{},消息处理异常：{}", msg.getTenant(), e.getMessage(), e);
      try {
        reportTaskException(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId(), e.getMessage(), msg.getOperator());
      } catch (Exception em) {
        log.error("租户:{},报告任务{}异常失败:{}", msg.getTenant(), msg.getTraceId(), em.getMessage(), em);
      }
    }
  }

  private void doExecuteShopSkuInvc(ShopSkuMsg msg, ShopSkuInvRuleBatchUpdateRequest batchUpdateRequest)
      throws Exception {
    Set<String> shopIds = getShopIds(msg, batchUpdateRequest);
    if (CollectionUtils.isEmpty(shopIds)) {
      log.info("租户:{},shopIds为空", msg.getTenant());
      return;
    }

    Set<String> skuIds = getSkuIds(msg, batchUpdateRequest);
    if (CollectionUtils.isEmpty(skuIds)) {
      log.info("租户:{},skuIds为空", msg.getTenant());
      return;
    }

    RsShopSkuFilter rsShopSkuFilter = new RsShopSkuFilter();
    rsShopSkuFilter.setSkuIdIn(new ArrayList<>(skuIds));
    rsShopSkuFilter.setShopIdIn(new ArrayList<>(shopIds));
    rsShopSkuFilter.setDeletedEq(Boolean.FALSE);
    RsMasPageResponse<List<RsShopSku>> shopSkuQuery = getClient().shopSkuQuery(msg.getTenant(), rsShopSkuFilter);
    if (CollectionUtils.isEmpty(shopSkuQuery.getData())) {
      log.info("租户:{},门店商品为空", msg.getTenant());
      return;
    }

    RsShopSkuInvRuleDef definition = new RsShopSkuInvRuleDef();
    List<RsParameter> parameters = new ArrayList<>();
    parameters.add(new RsParameter(QTY, String.valueOf(batchUpdateRequest.getQty())));
    parameters.add(new RsParameter(RsShopSkuInvRuleDef.PARAMETER_NAME_START_DATE, SDF.format(batchUpdateRequest.getStartEffectTime())));
    definition.setParameters(parameters);

    List<RsShopSkuInvRule> items = new ArrayList<>();
    Set<String> shopIdSkuIdKey = new HashSet<>();
    for (RsShopSku rsShopSku : shopSkuQuery.getData()) {
      if (rsShopSku.getShop() == null || rsShopSku.getSku() == null) {
        continue;
      }
      shopIdSkuIdKey.add(rsShopSku.getShop().getId() + "," + rsShopSku.getSku().getId());
      RsShopSkuInvRule shopSkuInvRule = new RsShopSkuInvRule();
      BeanUtils.copyProperties(batchUpdateRequest, shopSkuInvRule);
      shopSkuInvRule.setOrgId(msg.getOrgId());
      shopSkuInvRule.setOrgType(msg.getOrgType());
      shopSkuInvRule.setName(DEFAULT_NAME);
      shopSkuInvRule.setEnabled(DEFAULT_ENABLED);
      shopSkuInvRule.setType(DEFAULT_TYPE);
      shopSkuInvRule.setDefinition(definition);

      RsShopSkuInvRuleCond condition = new RsShopSkuInvRuleCond();
      RsShopRange shopRange = new RsShopRange();
      shopRange.setLimitType(DEFAULT_LIMIT_TYPE);
      shopRange.setLimit(DEFAULT_LIMIT);
      shopRange.setShops(Collections.singletonList(rsShopSku.getShop()));
      condition.setShopRange(shopRange);

      RsSkuRange skuRange = new RsSkuRange();
      skuRange.setLimit(DEFAULT_LIMIT);
      skuRange.setLimitType(DEFAULT_LIMIT_TYPE);
      RsIdCodeName skuInfo = new RsIdCodeName(rsShopSku.getSku().getId(), rsShopSku.getSku().getCode(), rsShopSku.getSku().getName());
      skuRange.setSkus(Collections.singletonList(skuInfo));
      condition.setSkuRange(skuRange);

      shopSkuInvRule.setCondition(condition);
      items.add(shopSkuInvRule);
    }

    startReport(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId(), msg.getOperator());
    removeInvalidRule(msg, shopIds, skuIds, shopIdSkuIdKey, batchUpdateRequest.getStartEffectTime());
    doBatchUpdateShopSkuInvc(msg, items);
  }

  private void removeInvalidRule(ShopSkuMsg msg, Set<String> shopIds, Set<String> skuIds, Set<String> shopIdSkuIdKey, Date startEffectTime) throws Exception {
    Assert.notNull(startEffectTime, "startEffectTime");

    RsShopSkuInvRuleFilter shopSkuInvRuleFilter = new RsShopSkuInvRuleFilter();
    shopSkuInvRuleFilter.setTypeEq(DEFAULT_TYPE);

    shopSkuInvRuleFilter.setShopIdIn(new ArrayList<>(shopIds));
    shopSkuInvRuleFilter.setSkuIdIn(new ArrayList<>(skuIds));
    RsMasPageResponse<List<RsShopSkuInvRule>> response = getClient().shopSkuInvRuleQuery(msg.getTenant(), shopSkuInvRuleFilter);
    if (!response.isSuccess()) {
      log.error("租户:{},查询库存规则失败：{}", msg.getTenant(), response.getEchoMessage());
    }

    if (CollectionUtils.isEmpty(response.getData())) {
      return;
    }

    List<String> removeRuleIds = new ArrayList<>();
    for (RsShopSkuInvRule shopSkuInvRule : response.getData()) {
      String shopId = shopSkuInvRule.getCondition().getShopRange().getShops().get(0).getId();
      String skuId = shopSkuInvRule.getCondition().getSkuRange().getSkus().get(0).getId();
      String key = shopId + "," + skuId;
      if (!shopIdSkuIdKey.contains(key)) {
        continue;
      }

      if (invalidRule(shopSkuInvRule, startEffectTime)) {
        removeRuleIds.add(shopSkuInvRule.getId());
      }
    }

    if (CollectionUtils.isEmpty(removeRuleIds)) {
      return;
    }

    for (String removeRuleId : removeRuleIds) {
      RsMasResponse<String> removeResponse = getClient().shopSkuInvRuleDelete(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), removeRuleId, msg.getOperator());
      if (!removeResponse.isSuccess()) {
        log.error("租户:{},去除【无效的规则】失败：{}", msg.getTenant(), response.getEchoMessage());
      }
    }
  }

  private Boolean invalidRule(RsShopSkuInvRule rule, Date startEffectTime) throws Exception {
    if (rule == null || rule.getDefinition() == null
        || CollectionUtils.isEmpty(rule.getDefinition().getParameters())) {
      return true;
    }

    for (RsParameter parameter : rule.getDefinition().getParameters()) {
      if (parameter == null || parameter.getName() == null
          || !parameter.getName().equals(RsShopSkuInvRuleDef.PARAMETER_NAME_START_DATE)) {
        continue;
      }

      if (parameter.getValue() != null && SDF.parse(parameter.getValue()).compareTo(startEffectTime) < 0) {
        return false;
      }
    }
    return true;
  }


  public void startReport(String tenantId, String orgType, String orgId, String taskId, String operator) {
    try {
      RsTaskExecuteReport report = new RsTaskExecuteReport();
      report.setReportType(ReportType.start);
      RsMasResponse response = getClient().taskReport(tenantId, orgType, orgId, taskId, report, operator);
      if (response.isSuccess() == false) {
        log.error("init-报告任务出错{}", response.getEchoMessage());
      }
    } catch (Exception e) {
      log.error("init-报告任务出错:{}", e.getMessage(), e);
    }
  }

  private void doBatchUpdateShopSkuInvc(ShopSkuMsg msg, List<RsShopSkuInvRule> items) {
    RsMasRequest<RsBatchSaveShopSkuInvRule> rule = new RsMasRequest<>();
    RsBatchSaveShopSkuInvRule rsBatchSaveShopSkuInvRule = new RsBatchSaveShopSkuInvRule();
    rule.setData(rsBatchSaveShopSkuInvRule);
    rsBatchSaveShopSkuInvRule.setItems(items);

    RsMasResponse<List<String>> response = getClient().shopSkuInvRuleBatchSave(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), rule, msg.getOperator());
    if (!response.isSuccess()) {
      log.error("租户:{},shopSkuIds:{},批量保存规则失败：{}", msg.getTenant(), JsonUtil.objectToJson(items), response.getEchoMessage());
    }
    nextStep(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId(), msg.getOperator());
    log.info("租户:{},导入批次进度：{},当前处理条数:{}", msg.getTenant(), msg.getTaskId(), items.size());
  }


  private Set<String> getSkuIds(ShopSkuMsg msg, ShopSkuInvRuleBatchUpdateRequest request) {
    if (!request.getAllSku()
        && CollectionUtils.isEmpty(request.getSkuIds())) {
      log.info("租户:{},skuIds为空", msg.getTenant());
      return null;
    }
    RsSkuFilter skuFilter = new RsSkuFilter();
    if (!request.getAllSku()) {
      skuFilter.setIdIn(request.getSkuIds());
    }
    RsMasPageResponse<List<RsSku>> skuQueryResult = getClient().skuQuery(msg.getTenant(), skuFilter);
    if (CollectionUtils.isEmpty(skuQueryResult.getData())) {
      return null;
    }

    return skuQueryResult.getData().stream().map(RsSku::getId).collect(Collectors.toSet());
  }

  private Set<String> getShopIds(ShopSkuMsg msg, ShopSkuInvRuleBatchUpdateRequest request) {
    if (!request.getAllShop()
        && CollectionUtils.isEmpty(request.getShopIds())) {
      log.info("租户:{},shopIds为空", msg.getTenant());
      return null;
    }
    RsStoreFilter storeFilter = new RsStoreFilter();
    if (!request.getAllShop()) {
      storeFilter.setIdIn(request.getShopIds());
    }

    RsMasPageResponse<List<RsStore>> storeQueryResult = getClient().storeQuery(msg.getTenant(), storeFilter);
    if (CollectionUtils.isEmpty(storeQueryResult.getData())) {
      return null;
    }

    return storeQueryResult.getData().stream().map(RsStore::getId).collect(Collectors.toSet());
  }

  public void nextStep(String tenantId, String orgType, String orgId, String taskId, String operator) {
    try {
      RsTaskExecuteReport report = new RsTaskExecuteReport();
      report.setReportType(ReportType.progress);
      RsMasResponse response = getClient().taskReport(tenantId, orgType, orgId, taskId, report, operator);
      if (response.isSuccess() == false) {
        log.error("nextStep-报告任务出错,step:{},原因:{}", StringUtil.toString(taskId),
            response.getEchoMessage());
      }
    } catch (Exception e) {
      log.error("nextStep-报告任务出错：{}", e.getMessage(), e);
    }
  }

  protected RsTask getTask(String tenant, String orgType, String orgId, String taskId) throws Exception {
    RsMasResponse<RsTask> response = getClient().taskGet(tenant, orgType, orgId, taskId);
    if (!response.isSuccess()) {
      log.error("获取任务详情报错:{}", response.getEchoMessage());
      throw new IllegalArgumentException("获取任务详情报错" + response.getEchoMessage());
    }
    return response.getData();
  }

  protected void reportTaskStart(String tenant, String orgType, String orgId, String taskId, String operator) throws Exception {
    RsTaskExecuteReport report = new RsTaskExecuteReport();
    report.setReportType(ReportType.start);
    RsMasResponse response = getClient().taskReport(tenant, orgType, orgId, taskId, report, operator);
    if (!response.isSuccess()) {
      log.error("报告任务开始出错:{}", response.getEchoMessage());
    }
  }

  protected void reportTaskProcessFinish(String tenant, String orgType, String orgId, String taskId, String operator)
      throws Exception {
    RsTaskExecuteReport report = new RsTaskExecuteReport();
    report.setProgress(BigDecimal.ONE);
    report.setReportType(ReportType.result);
    report.setExecuteResult(RsExecuteResult.success);
    RsMasResponse response = getClient().taskReport(tenant, orgType, orgId, taskId, report, operator);
    if (!response.isSuccess()) {
      log.error("报告任务完成出错：{}", response.getEchoMessage());
      throw new IllegalArgumentException("报告任务完成出错" + response.getEchoMessage());
    }
  }

  protected void reportTaskException(String tenant, String orgType, String orgId, String taskId, String message, String operator)
      throws Exception {
    RsTaskExecuteReport report = new RsTaskExecuteReport();
    report.setProgress(BigDecimal.ONE);
    report.setReportType(ReportType.result);
    report.setExecuteResult(RsExecuteResult.fail);
    report.setFailReason(message);
    RsMasResponse response = getClient().taskReport(tenant, orgType, orgId, taskId, report, operator);
    if (response.isSuccess() == false) {
      log.error("报告任务出错:{}", response.getEchoMessage());
    }
  }

  protected RsMasClient getClient() {
    return ApplicationContextUtils.getBean(RsMasClient.class);
  }

}
