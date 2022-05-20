/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop 文件名：	PlatshopcategoryExecutor.java 模块说明： 修改历史： 2021/9/2 - XLT - 创建。
 */
package com.hd123.baas.sop.evcall.exector.platshopcategory;

import com.hd123.baas.sop.utils.ApplicationContextUtils;
import com.hd123.baas.sop.service.api.basedata.platshopcategory.PlatShopCategory;
import com.hd123.baas.sop.service.api.basedata.platshopcategory.PlatShopCategoryCopyRequest;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsmas.RsMasClient;
import com.hd123.baas.sop.remote.rsmas.RsMasResponse;
import com.hd123.baas.sop.remote.rsmas.RsParameter;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategoryCopyRequest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author XLT
 */
@Slf4j
@Component
public class PlatShopCategoryExecutor extends AbstractEvCallExecutor<PlatShopCategoryMsg> {
  public static final String PLAT_SHOP_CATEGORY_EXECUTOR_ID = PlatShopCategoryExecutor.class.getSimpleName();

  private final static String PARAM_REQUESTBODY = "requestBosy";

  public static final String DEFAULT_PLATFORM = "-";

  @Autowired
  private RsMasClient rsMasClient;

  @Override
  protected PlatShopCategoryMsg decodeMessage(String msg) throws BaasException {
    log.info("收到PlatShopCategoryMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, PlatShopCategoryMsg.class);
  }

  @Override
  public void doExecute(PlatShopCategoryMsg msg, @NotNull EvCallExecutionContext context) throws Exception {
    try {
      RsTask task = getTask(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId());
      if (task == null) {
        log.error("租户:{},组织Id{},获取任务{}详情不存在", msg.getTenant(), msg.getOrgId(), msg.getTaskId());
        throw new IllegalArgumentException(
            "租户" + msg.getTenant() + "组织id" + msg.getOrgId() + "获取任务" + msg.getTaskId() + "详情不存在");
      }
      if (task.getStatus().equals(RsTaskStatus.canceled)
          || task.getStatus().equals(RsTaskStatus.ended)) {
        log.error("租户:{},组织id:{},获取任务{}已结束或者已取消{}", msg.getTenant(), msg.getOrgId(), msg.getTaskId(),
            task.getStatus().name());
        return;
      }
      reportTaskStart(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId(), msg.getOperator());
      PlatShopCategoryCopyRequest platShopCategoryCopyRequest = null;
      for (RsParameter parameter : task.getParameters()) {
        if (PARAM_REQUESTBODY.equals(parameter.getName())) {
          platShopCategoryCopyRequest = JsonUtil.jsonToObject(parameter.getValue(),
              PlatShopCategoryCopyRequest.class);
        }
      }
      if (platShopCategoryCopyRequest == null) {
        log.error("租户{}-组织id{}-任务{}参数{}不存在", msg.getTenant(), msg.getOrgId(), msg.getTaskId(), PARAM_REQUESTBODY);
        throw new IllegalArgumentException(
            "租户" + msg.getTenant() + "组织Id" + msg.getOrgId() + "-任务" + msg.getTaskId() + "参数" + PARAM_REQUESTBODY + "不存在");
      }
      doCopyPlatShopCategory(msg, platShopCategoryCopyRequest);
      reportTaskProcessFinish(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId(), msg.getOperator());
    } catch (Exception e) {
      log.error("租户:{},组织Id:{},消息处理异常：{}", msg.getTenant(), msg.getOrgId(), e.getMessage(), e);
      try {
        reportTaskException(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId(), e.getMessage(), msg.getOperator());
      } catch (Exception em) {
        log.error("租户:{},组织Id:{},报告任务异常失败:{}", msg.getTenant(), msg.getOrgId(), em.getMessage(), em);
      }
    }
  }

  private void doCopyPlatShopCategory(PlatShopCategoryMsg msg, PlatShopCategoryCopyRequest platShopCategoryCopyRequest)
      throws Exception {
    if (!platShopCategoryCopyRequest.isAllUnit() && CollectionUtils.isEmpty(platShopCategoryCopyRequest.getShopIds())) {
      log.info("租户:{},shopIds为空", msg.getTenant());
      return;
    }
    Set<String> storeIds = new HashSet<>(platShopCategoryCopyRequest.getShopIds());
    if (platShopCategoryCopyRequest.isAllUnit()) {
      RsStoreFilter rsStoreFilter = new RsStoreFilter();
      rsStoreFilter.setOrgIdEq(msg.getOrgId());
      rsStoreFilter.setOrgTypeEq(msg.getOrgType());
      List<RsStore> stores = getClient().storeQuery(msg.getTenant(), rsStoreFilter).getData();
      if (CollectionUtils.isEmpty(stores)) {
        log.info("租户:{},组织Id{},需要初始化的门店不存在", msg.getTenant(),msg.getOrgId());
        return;
      }
      storeIds = stores.stream().map(RsStore::getId).collect(Collectors.toSet());
      storeIds.remove(platShopCategoryCopyRequest.getSourceShopId());
    }
    List<String> copyShopIds = new ArrayList<>(storeIds);
    startReport(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), msg.getTaskId(), msg.getOperator());
    doUpload(msg, msg.getTaskId(), copyShopIds, platShopCategoryCopyRequest);
  }

  private void doUpload(PlatShopCategoryMsg msg, String taskId,
      List<String> storeIds, PlatShopCategoryCopyRequest platShopCategoryCopyRequest)
      throws Exception {
    RsPlatShopCategoryCopyRequest target = new RsPlatShopCategoryCopyRequest();
    target.setShopIds(storeIds);
    target.setPlatformId(DEFAULT_PLATFORM);
    target.setType(PlatShopCategory.TYPE_FRONT);
    target.setSourceShopId(platShopCategoryCopyRequest.getSourceShopId());
    BaasResponse response = covertBaasResponse(
        rsMasClient.platShopCategoryCopyInit(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), target, msg.getOperator()));
    if (!response.isSuccess()) {
      log.error("租户:{},组织Id{} ,storeIds:{},门店类目复制：{}", msg.getTenant(), msg.getOrgId(), JsonUtil.objectToJson(storeIds),
          response.getMsg());
    }

    nextStep(msg.getTenant(), msg.getOrgType(), msg.getOrgId(), taskId, msg.getOperator());
    log.info("租户:{},导入批次进度：{},当前处理条数:{}", msg.getTenant(), taskId, storeIds.size());
  }


  public void startReport(String tenantId, String orgType, String orgId, String taskId, String operator) {
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
  }

  public void nextStep(String tenantId, String orgType, String orgId, String taskId, String operator) {
    try {
      RsTaskExecuteReport report = new RsTaskExecuteReport();
      report.setReportType(ReportType.progress);
      BaasResponse response = covertBaasResponse(
          getClient().taskReport(tenantId, orgType, orgId, taskId, report, operator));
      if (response.isSuccess() == false) {
        log.error("nextStep-报告任务出错,step:{},原因:{}", StringUtil.toString(taskId),
            response.getMsg());
      }
    } catch (Exception e) {
      log.error("nextStep-报告任务出错：{}", e.getMessage(), e);
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
}