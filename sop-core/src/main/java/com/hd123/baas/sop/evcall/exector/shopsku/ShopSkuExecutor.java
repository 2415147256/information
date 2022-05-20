/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	transfer 文件名：	GoodsExecutor.java 模块说明： 修改历史： 2020/7/17 - seven - 创建。
 */
package com.hd123.baas.sop.evcall.exector.shopsku;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.utils.ApplicationContextUtils;
import com.hd123.baas.sop.qcy.controller.shopsku.BShopSkuCreateRequest;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsias.RsIasClient;
import com.hd123.baas.sop.remote.rsias.RsIasClientConfig;
import com.hd123.baas.sop.remote.rsias.RsIasClientVersion;
import com.hd123.baas.sop.remote.rsias.inv.RsIasResponse;
import com.hd123.baas.sop.remote.rsias.inv.RsInvSync;
import com.hd123.baas.sop.remote.rsias.inv.RsInvSyncReq;
import com.hd123.baas.sop.remote.rsmas.RsMasClient;
import com.hd123.baas.sop.remote.rsmas.RsMasPageResponse;
import com.hd123.baas.sop.remote.rsmas.RsMasRequest;
import com.hd123.baas.sop.remote.rsmas.RsMasResponse;
import com.hd123.baas.sop.remote.rsmas.RsParameter;
import com.hd123.baas.sop.remote.rsmas.index.RsDocument;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSKUBatchCreate;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSku;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSkuCreate;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSkuFilter;
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
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.spms.commons.json.JsonUtil;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author seven
 */
@Slf4j
@Component
public class ShopSkuExecutor extends AbstractEvCallExecutor<ShopSkuMsg> {
  public static final String SHOP_SKU_EXECUTOR_ID = ShopSkuExecutor.class.getSimpleName();

  private final static String PARAM_REQUESTBODY = "requestBosy";
  private static final BigDecimal DEFAULT_QTY = new BigDecimal("9999");
  private static final Boolean DEFAULT_HAS_QTY = true;

  @Autowired
  private RsIasClient rsIasClient;
  @Autowired
  private BaasConfigClient configClient;

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
      BShopSkuCreateRequest shopSkuCreateRequest = null;
      for (RsParameter parameter : task.getParameters()) {
        if (PARAM_REQUESTBODY.equals(parameter.getName())) {
          shopSkuCreateRequest = JsonUtil.jsonToObject(parameter.getValue(),
              BShopSkuCreateRequest.class);
        }
      }
      if (shopSkuCreateRequest == null) {
        log.error("租户{}-任务{}参数{}不存在", msg.getTenant(), msg.getTaskId(), PARAM_REQUESTBODY);
        throw new IllegalArgumentException(
            "租户" + msg.getTenant() + "-任务" + msg.getTaskId() + "参数" + PARAM_REQUESTBODY + "不存在");
      }
      doExecuteShopSku(msg, shopSkuCreateRequest);
      reportTaskProcessFinish(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId(), msg.getOperator());
    } catch (Exception e) {
      log.error("租户:{},消息处理异常：{}", msg.getTenant(), e.getMessage(), e);
      try {
        reportTaskException(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId(), e.getMessage(), msg.getOperator());
      } catch (Exception em) {
        log.error("租户:{},报告任务异常失败:{}", msg.getTenant(), msg.getTraceId(), em.getMessage(), em);
      }
    }
  }

  private void doExecuteShopSku(ShopSkuMsg msg, BShopSkuCreateRequest shopSkuCreateRequest)
      throws Exception {

    Map<String, String> shopIdCodeMap = new HashMap<>();
    List<String> shopIds = getShopIds(msg, msg.getOrgType(), msg.getOrgId(), shopSkuCreateRequest, shopIdCodeMap);
    if (CollectionUtils.isEmpty(shopIds)) {
      log.info("租户:{},shopIds为空", msg.getTenant());
      return;
    }

    Map<String, RsInvSync> shopSkuInvMap = new HashMap<>();
    List<String> skuIds = getSkuIds(msg, msg.getOrgType(), msg.getOrgId(), shopSkuCreateRequest, shopSkuInvMap);
    if (CollectionUtils.isEmpty(skuIds)) {
      log.info("租户:{},skuIds为空", msg.getTenant());
      return;
    }

    final int size = shopIds.size() * skuIds.size();
    int count = 0;
    int batchSize = 100;
    TaskExportData exportData = init(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), batchSize, msg.getTaskId(),
        new BigDecimal(size), msg.getOperator());
    List<RsShopSkuCreate> lines = new ArrayList<>();
    List<RsInvSync> invSyncs = new ArrayList<>();
    List<RsDocument> docs = new ArrayList<>();
    while (count < size) {

      for (String shopId : shopIds) {
        Map<String, RsShopSku> RsShopSkuMap = getRsShopSkuMap(msg, shopId, shopSkuCreateRequest,
            skuIds);
        String shopCode = shopIdCodeMap.get(shopId);
        for (String skuId : skuIds) {
          if (RsShopSkuMap.containsKey(skuId)) {
            count++;
            continue;
          }
          lines.add(buildRsShopSkuCreate(shopId, skuId));

          RsInvSync rsInvSync = shopSkuInvMap.get(skuId);
          if (rsInvSync != null) {
            RsInvSync invSync = new RsInvSync();
            BeanUtils.copyProperties(rsInvSync, invSync);
            invSync.setWrhId(shopId);
            invSync.setWrhCode(shopCode);
            invSyncs.add(invSync);
          }

          RsDocument doc = buildShopSkuInvDoc(msg.getTenant(), shopId, skuId, DEFAULT_QTY, DEFAULT_HAS_QTY);
          docs.add(doc);

          count++;
          if (lines.size() > 99) {
            doUpload(msg, exportData, lines, invSyncs, docs);
            lines = new ArrayList<>();
            invSyncs.clear();
            docs.clear();
          }
        }
      }
    }

    if (!lines.isEmpty()) {
      doUpload(msg, exportData, lines, invSyncs, docs);
    }
  }

  private Map<String, RsShopSku> getRsShopSkuMap(ShopSkuMsg msg, String shopId,
      BShopSkuCreateRequest shopSkuCreateRequest, List<String> skuIds) {
    RsShopSkuFilter shopSkuFilter = new RsShopSkuFilter();
    shopSkuFilter.setShopIdEq(shopId);
    shopSkuFilter.setOrgTypeEq(msg.getOrgType());
    shopSkuFilter.setOrgIdEq(msg.getOrgId());
    if (!shopSkuCreateRequest.getAllSku()) {
      shopSkuFilter.setSkuIdIn(skuIds);
    }
    BaasResponse<List<RsShopSku>> skuQueryResult = covertBaasResponse(
        getClient().shopSkuQuery(msg.getTenant(), shopSkuFilter));
    Map<String, RsShopSku> result = new HashMap<>();
    if (!CollectionUtils.isEmpty(skuQueryResult.getData())) {
      for (RsShopSku shopSku : skuQueryResult.getData()) {
        if (shopSku.getSku() != null) {
          result.put(shopSku.getSku().getId(), shopSku);
        }
      }
    }
    return result;
  }

  private List<String> getSkuIds(ShopSkuMsg msg, String orgType, String orgId, BShopSkuCreateRequest shopSkuCreateRequest,
      Map<String, RsInvSync> shopSkuInvMap) {
    if (!shopSkuCreateRequest.getAllSku()
        && CollectionUtils.isEmpty(shopSkuCreateRequest.getSkuIds())) {
      log.info("租户:{},skuIds为空", msg.getTenant());
      return new ArrayList<>();
    }
    RsSkuFilter skuFilter = new RsSkuFilter();
    if (!shopSkuCreateRequest.getAllSku()) {
      skuFilter.setIdIn(shopSkuCreateRequest.getSkuIds());
    }
    skuFilter.setOrgIdEq(orgId);
    skuFilter.setOrgTypeEq(orgType);
    BaasResponse<List<RsSku>> skuQueryResult = covertBaasResponse(
        getClient().skuQuery(msg.getTenant(), skuFilter));

    if (CollectionUtils.isEmpty(skuQueryResult.getData())) {
      return Collections.EMPTY_LIST;
    }

    Date now = new Date();
    for (RsSku sku : skuQueryResult.getData()) {
      RsInvSync rsInvSync = new RsInvSync();
      rsInvSync.setSkuId(sku.getId());
      rsInvSync.setSkuCode(sku.getCode());
      rsInvSync.setQpc(sku.getQpc());
      rsInvSync.setLastSynced(now);
      rsInvSync.setQty(DEFAULT_QTY);
      shopSkuInvMap.put(sku.getId(), rsInvSync);
    }

    return skuQueryResult.getData().stream().map(RsSku::getId).collect(Collectors.toList());
  }

  private List<String> getShopIds(ShopSkuMsg msg, String orgType, String orgId, BShopSkuCreateRequest shopSkuCreateRequest,
      Map<String, String> shopIdCodeMap) {
    if (!shopSkuCreateRequest.getAllShop()
        && CollectionUtils.isEmpty(shopSkuCreateRequest.getShopIds())) {
      log.info("租户:{},shopIds为空", msg.getTenant());
      return new ArrayList<>();
    }
    RsStoreFilter storeFilter = new RsStoreFilter();
    if (!shopSkuCreateRequest.getAllShop()) {
      storeFilter.setIdIn(shopSkuCreateRequest.getShopIds());
    }
    storeFilter.setOrgIdEq(orgId);
    storeFilter.setOrgTypeEq(orgType);
    BaasResponse<List<RsStore>> storeQueryResult = covertBaasResponse(
        getClient().storeQuery(msg.getTenant(), storeFilter));

    if (CollectionUtils.isEmpty(storeQueryResult.getData())) {
      return Collections.EMPTY_LIST;
    }

    for (RsStore store : storeQueryResult.getData()) {
      shopIdCodeMap.put(store.getId(), store.getCode());
    }
    return storeQueryResult.getData().stream().map(RsStore::getId).collect(Collectors.toList());
  }

  private void doUpload(ShopSkuMsg msg, TaskExportData exportData, List<RsShopSkuCreate> lines,
      List<RsInvSync> invSyncs, List<RsDocument> docs)
      throws Exception {
    RsShopSKUBatchCreate request = new RsShopSKUBatchCreate();
    request.setLines(lines);
    BaasResponse response = covertBaasResponse(
        getClient().batchCreateAsYc(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), request, msg.getOperator()));
    if (!response.isSuccess()) {
      log.error("租户:{},lines:{},批量新建门店商品失败：{}", msg.getTenant(), JsonUtil.objectToJson(lines),
          response.getMsg());
    } else {
      RsInvSyncReq invSyncReq = new RsInvSyncReq();
      invSyncReq.setRequestId(UUID.randomUUID().toString());
      invSyncReq.setLines(invSyncs);

      RsIasClientConfig iasClientConfig = configClient.getConfig(msg.getTenant(), RsIasClientConfig.class);
      log.info("invSync --- 获取库存中台组件配置: {}", JsonUtil.objectToJson(iasClientConfig));
      RsIasResponse<Void> rsIasResponse = RsIasClientVersion.V2.name().equals(iasClientConfig.getVersion()) ?
          rsIasClient.invSyncV2(msg.getTenant(), invSyncReq)
          : rsIasClient.invSync(msg.getTenant(), invSyncReq);
      if (!rsIasResponse.isSuccess()) {
        log.error("租户:{},invSyncs:{},库存中心，同步门店商品库存失败：{}", msg.getTenant(), JsonUtil.objectToJson(invSyncs),
            response.getMsg());
      } else {
        RsMasRequest masRequest = new RsMasRequest();
        masRequest.setData(docs);
        RsMasResponse rsMasResponse = getClient().updateDoc(msg.getTenant(), masRequest);
        if (!rsMasResponse.isSuccess()) {
          log.error("租户:{},docs:{},更新门店商品索引失败：{}", msg.getTenant(), JsonUtil.objectToJson(docs),
              response.getMsg());
        }
      }
    }

    nextStep(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), exportData, lines.size(), msg.getOperator());
    log.info("租户:{},导入批次进度：{},当前处理条数:{}", msg.getTenant(), exportData.getProcess(), lines.size());
  }

  private RsShopSkuCreate buildRsShopSkuCreate(String shopId, String skuId) {
    RsShopSkuCreate result = new RsShopSkuCreate();
    result.setShopId(shopId);
    result.setSkuId(skuId);
    return result;
  }

  public TaskExportData init(String tenantId, String orgType, String orgId, int batchSize, String taskId, BigDecimal total,
      String operator) {
    TaskExportData data = new TaskExportData();
    data.setTotal(total);
    data.setTaskId(taskId);
    if (total.compareTo(BigDecimal.ZERO) <= 0) {
      return data;
    }
    BigDecimal div = total.divide(new BigDecimal(batchSize), 2, RoundingMode.HALF_UP);
    if (div.doubleValue() % 1 > 0) {
      div = div.add(new BigDecimal(1));
    }
    BigDecimal batch = new BigDecimal(div.intValue());
    data.setStep(new BigDecimal(100).divide(batch, 6, RoundingMode.HALF_UP));
    try {
      RsTaskExecuteReport report = new RsTaskExecuteReport();
      report.setReportType(ReportType.start);
      BaasResponse response = covertBaasResponse(
          getClient().taskReport(tenantId, orgType, orgId, taskId, report, operator));
      if (response.isSuccess() == false) {
        log.error("init-报告任务出错{}", response.getMsg());
      }
    } catch (Exception e) {
      log.error("init-报告任务出错:{}", e.getMessage(), e);
    }
    return data;
  }

  public void nextStep(String tenantId, String orgType, String orgId, TaskExportData data, final int size, String operator) {
    data.setAddStep(BigDecimal.valueOf(size * 100).divide(data.getTotal(), 4, RoundingMode.HALF_UP)
        .setScale(4, RoundingMode.HALF_UP));
    if (BigDecimal.valueOf(100).compareTo(data.getAddStep()) >= 0) {
      data.setProcess(data.getProcess().add(data.getAddStep()).setScale(4, RoundingMode.HALF_UP));
      try {
        RsTaskExecuteReport report = new RsTaskExecuteReport();
        report.setProgress(
            data.getProcess().divide(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP));
        report.setReportType(ReportType.progress);
        BaasResponse response = covertBaasResponse(
            getClient().taskReport(tenantId, orgType, orgId, data.getTaskId(), report, operator));
        if (response.isSuccess() == false) {
          log.error("nextStep-报告任务出错,step:{},原因:{}", StringUtil.toString(data.getStep()),
              response.getMsg());
        }
      } catch (Exception e) {
        log.error("nextStep-报告任务出错：{}", e.getMessage(), e);
      }

      data.setAddStep(BigDecimal.ZERO);
    }
  }

  protected RsTask getTask(String tenant, String orgType, String orgId, String taskId) throws Exception {
    BaasResponse<RsTask> response = covertBaasResponse(getClient().taskGet(tenant, orgType, orgId, taskId));
    if (!response.isSuccess()) {
      log.error("获取任务详情报错:{}", response.getMsg());
      throw new IllegalArgumentException("获取任务详情报错" + response.getMsg());
    }
    return response.getData();
  }

  protected void reportTaskStart(String tenant, String orgType, String orgId, String taskId, String operator) throws Exception {
    RsTaskExecuteReport report = new RsTaskExecuteReport();
    report.setReportType(ReportType.start);
    BaasResponse response = covertBaasResponse(
        getClient().taskReport(tenant, orgType, orgId, taskId, report, operator));
    if (!response.isSuccess()) {
      log.error("报告任务开始出错:{}", response.getMsg());
    }
  }

  protected void reportTaskProcessFinish(String tenant, String orgType, String orgId, String taskId, String operator)
      throws Exception {
    RsTaskExecuteReport report = new RsTaskExecuteReport();
    report.setProgress(BigDecimal.ONE);
    report.setReportType(ReportType.result);
    report.setExecuteResult(RsExecuteResult.success);
    BaasResponse response = covertBaasResponse(
        getClient().taskReport(tenant, orgType, orgId, taskId, report, operator));
    if (!response.isSuccess()) {
      log.error("报告任务完成出错：{}", response.getMsg());
      throw new IllegalArgumentException("报告任务完成出错" + response.getMsg());
    }
  }

  protected void reportTaskException(String tenant, String orgType, String orgId, String taskId, String message, String operator)
      throws Exception {
    RsTaskExecuteReport report = new RsTaskExecuteReport();
    report.setProgress(BigDecimal.ONE);
    report.setReportType(ReportType.result);
    report.setExecuteResult(RsExecuteResult.fail);
    report.setFailReason(message);
    BaasResponse response = covertBaasResponse(
        getClient().taskReport(tenant, orgType, orgId, taskId, report, operator));
    if (response.isSuccess() == false) {
      log.error("报告任务出错:{}", response.getMsg());
    }
  }

  protected RsMasClient getClient() {
    return ApplicationContextUtils.getBean(RsMasClient.class);
  }

  protected BaasResponse covertBaasResponse(RsMasResponse rsMasResponse) {
    BaasResponse response = new BaasResponse();
    response.setMsg(rsMasResponse.getEchoMessage());
    response.setCode(rsMasResponse.getEchoCode() == 0 ? 2000 : rsMasResponse.getEchoCode());
    response.setSuccess(rsMasResponse.isSuccess());
    response.setData(rsMasResponse.getData());
    return response;
  }

  protected BaasResponse covertBaasResponse(RsMasPageResponse rsMasPageResponse) {
    BaasResponse response = new BaasResponse();
    response.setMsg(rsMasPageResponse.getEchoMessage());
    response.setCode(rsMasPageResponse.getEchoCode() == 0 ? 2000 : rsMasPageResponse.getEchoCode());
    response.setSuccess(rsMasPageResponse.isSuccess());
    response.setTotal(rsMasPageResponse.getTotal());
    response.setData(rsMasPageResponse.getData());
    return response;
  }

  private RsDocument buildShopSkuInvDoc(String tenant, String shopId, String skuId, BigDecimal qty, Boolean hasQty) {
    RsDocument doc = new RsDocument();
    doc.setIndex("mas");
    doc.setType("doc");
    doc.setId(new StringBuffer("shopsku")
        .append(tenant)
        .append("-")
        .append("-")
        .append(skuId)//
        .append(shopId)//
        .toString());
    doc.getFields().put("inv.qty", qty);
    doc.getFields().put("inv.hasQty", hasQty);
    return doc;
  }

}
