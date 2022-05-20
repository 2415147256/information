/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	transfer 文件名：	GoodsExecutor.java 模块说明： 修改历史： 2020/7/17 - seven - 创建。
 */
package com.hd123.baas.sop.evcall.exector.shopsku;

import com.hd123.baas.sop.utils.ApplicationContextUtils;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.shopsku.batchrequest.ShopSkuCollocationGroupBatchUpdateRequest;
import com.hd123.baas.sop.remote.rsmas.RsMasClient;
import com.hd123.baas.sop.remote.rsmas.RsMasPageResponse;
import com.hd123.baas.sop.remote.rsmas.RsMasResponse;
import com.hd123.baas.sop.remote.rsmas.RsParameter;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsCollocationGroupShopSkuBind;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsCollocationGroupShopSkuBindLine;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author seven
 */
@Slf4j
@Component
public class BathBindCollocationGroupExecutor extends AbstractEvCallExecutor<ShopSkuMsg> {
  public static final String SHOP_SKU_EXECUTOR_ID = BathBindCollocationGroupExecutor.class.getSimpleName();

  private final static String PARAM_REQUESTBODY = "requestBosy";

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
      ShopSkuCollocationGroupBatchUpdateRequest shopSkuCollocationGroupBatchUpdateRequest = null;
      for (RsParameter parameter : task.getParameters()) {
        if (PARAM_REQUESTBODY.equals(parameter.getName())) {
          shopSkuCollocationGroupBatchUpdateRequest = JsonUtil.jsonToObject(parameter.getValue(),
              ShopSkuCollocationGroupBatchUpdateRequest.class);
        }
      }
      if (shopSkuCollocationGroupBatchUpdateRequest == null) {
        log.error("租户{}-任务{}参数{}不存在", msg.getTenant(), msg.getTaskId(), PARAM_REQUESTBODY);
        throw new IllegalArgumentException(
            "租户" + msg.getTenant() + "-任务" + msg.getTaskId() + "参数" + PARAM_REQUESTBODY + "不存在");
      }
      doExecuteShopSku(msg, shopSkuCollocationGroupBatchUpdateRequest);
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

  private void doExecuteShopSku(ShopSkuMsg msg, ShopSkuCollocationGroupBatchUpdateRequest collocationGroupBatchUpdateRequest)
      throws Exception {
    Set<String> shopIds = getShopIds(msg, collocationGroupBatchUpdateRequest);
    if (CollectionUtils.isEmpty(shopIds)) {
      log.info("租户:{},shopIds为空", msg.getTenant());
      return;
    }

    Set<String> skuIds = getSkuIds(msg, collocationGroupBatchUpdateRequest);
    if (CollectionUtils.isEmpty(skuIds)) {
      log.info("租户:{},skuIds为空", msg.getTenant());
      return;
    }

    final int size = shopIds.size() * skuIds.size();
    int count = 0;
    int batchSize = 100;
    TaskExportData exportData = init(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), batchSize, msg.getTaskId(),
        new BigDecimal(size), msg.getOperator());

    List<RsCollocationGroupShopSkuBindLine> lines = new ArrayList<>();
    while (count < size) {
      for (String shopId : shopIds) {
        for (String skuId : skuIds) {
          RsCollocationGroupShopSkuBindLine rsCollocationGroupShopSkuBindLine = new RsCollocationGroupShopSkuBindLine();
          rsCollocationGroupShopSkuBindLine.setShopId(shopId);
          rsCollocationGroupShopSkuBindLine.setSkuId(skuId);
          rsCollocationGroupShopSkuBindLine.setShowIndex(1);
          rsCollocationGroupShopSkuBindLine.setCollocationGroupId(collocationGroupBatchUpdateRequest.getCollocationGroupId());
          lines.add(rsCollocationGroupShopSkuBindLine);
          count++;
          if (lines.size() > 99) {
            doBindCollocationGroup(msg, exportData, lines);
            lines = new ArrayList<>();
          }
        }
      }
    }

    if (!lines.isEmpty()) {
      doBindCollocationGroup(msg, exportData, lines);
    }
  }

  private void doBindCollocationGroup(ShopSkuMsg msg, TaskExportData exportData, List<RsCollocationGroupShopSkuBindLine> lines) {
    RsCollocationGroupShopSkuBind rsCollocationGroupShopSkuUnbind = new RsCollocationGroupShopSkuBind();
    rsCollocationGroupShopSkuUnbind.setLines(lines);
    RsMasResponse response = getClient().shopSkuBindCollocationGroup(
        msg.getTenant(), msg.getOrgType(), msg.getOrgId(), rsCollocationGroupShopSkuUnbind, msg.getOperator());
    if (!response.isSuccess()) {
      log.error("租户:{},lines:{},批量设置加料组失败：{}", msg.getTenant(), JsonUtil.objectToJson(lines),
          response.getEchoMessage());
    }
    if (response.isSuccess() && response.getData() != null) {
      log.warn("租户:{},批量设置加料组警告：{}", msg.getTenant(), JsonUtil.objectToJson(response.getData()));
    }
    nextStep(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), exportData, lines.size(), msg.getOperator());
    log.info("租户:{},导入批次进度：{},当前处理条数:{}", msg.getTenant(), exportData.getProcess(), lines.size());
  }

  private Set<String> getSkuIds(ShopSkuMsg msg, ShopSkuCollocationGroupBatchUpdateRequest collocationGroupBatchUpdateRequest) {
    if (!collocationGroupBatchUpdateRequest.getAllSku()
        && CollectionUtils.isEmpty(collocationGroupBatchUpdateRequest.getSkuIds())) {
      log.info("租户:{},skuIds为空", msg.getTenant());
      return null;
    }
    RsSkuFilter skuFilter = new RsSkuFilter();
    if (!collocationGroupBatchUpdateRequest.getAllSku()) {
      skuFilter.setIdIn(collocationGroupBatchUpdateRequest.getSkuIds());
    }
    RsMasPageResponse<List<RsSku>> skuQueryResult = getClient().skuQuery(msg.getTenant(), skuFilter);
    if (CollectionUtils.isEmpty(skuQueryResult.getData())) {
      return null;
    }

    return skuQueryResult.getData().stream().map(RsSku::getId).collect(Collectors.toSet());
  }

  private Set<String> getShopIds(ShopSkuMsg msg, ShopSkuCollocationGroupBatchUpdateRequest collocationGroupBatchUpdateRequest) {
    if (!collocationGroupBatchUpdateRequest.getAllShop()
        && CollectionUtils.isEmpty(collocationGroupBatchUpdateRequest.getShopIds())) {
      log.info("租户:{},shopIds为空", msg.getTenant());
      return null;
    }
    RsStoreFilter storeFilter = new RsStoreFilter();
    if (!collocationGroupBatchUpdateRequest.getAllShop()) {
      storeFilter.setIdIn(collocationGroupBatchUpdateRequest.getShopIds());
    }

    RsMasPageResponse<List<RsStore>> storeQueryResult = getClient().storeQuery(msg.getTenant(), storeFilter);
    if (CollectionUtils.isEmpty(storeQueryResult.getData())) {
      return null;
    }

    return storeQueryResult.getData().stream().map(RsStore::getId).collect(Collectors.toSet());
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
      RsMasResponse response = getClient().taskReport(tenantId, orgType, orgId, taskId, report, operator);
      if (response.isSuccess() == false) {
        log.error("init-报告任务出错{}", response.getEchoMessage());
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
        RsMasResponse response = getClient().taskReport(tenantId, orgType, orgId, data.getTaskId(), report, operator);
        if (response.isSuccess() == false) {
          log.error("nextStep-报告任务出错,step:{},原因:{}", StringUtil.toString(data.getStep()),
              response.getEchoMessage());
        }
      } catch (Exception e) {
        log.error("nextStep-报告任务出错：{}", e.getMessage(), e);
      }
      data.setAddStep(BigDecimal.ZERO);
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
