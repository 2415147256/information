package com.hd123.baas.sop.service.impl.feedback;

import com.alibaba.fastjson.JSON;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.FeedbackConfig;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackSubmittedEvCalExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackSubmittedMsg;
import com.hd123.baas.sop.service.api.feedback.FeedbackExt;
import com.hd123.baas.sop.service.dao.feedback.PFeedback;
import com.hd123.baas.sop.utils.ApplicationContextUtils;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.service.api.sysconfig.FeedbackAutoAuditConfig;
import com.hd123.baas.sop.service.api.sysconfig.SysConfig;
import com.hd123.baas.sop.service.api.sysconfig.SysConfigService;
import com.hd123.baas.sop.service.api.feedback.Feedback;
import com.hd123.baas.sop.service.api.feedback.FeedbackApproval;
import com.hd123.baas.sop.service.api.feedback.FeedbackCreation;
import com.hd123.baas.sop.service.api.feedback.FeedbackDepLine;
import com.hd123.baas.sop.service.api.feedback.FeedbackFilter;
import com.hd123.baas.sop.service.api.feedback.FeedbackGdSearchResult;
import com.hd123.baas.sop.service.api.feedback.FeedbackReasonSaver;
import com.hd123.baas.sop.service.api.feedback.FeedbackReasonType;
import com.hd123.baas.sop.service.api.feedback.FeedbackRejection;
import com.hd123.baas.sop.service.api.feedback.FeedbackService;
import com.hd123.baas.sop.service.api.feedback.FeedbackState;
import com.hd123.baas.sop.service.dao.feedback.FeedbackDao;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.common.OrgConstants;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackApprovalEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackApprovalMsg;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackCreateAndSubmitEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackCreateAndSubmitMsg;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackPushEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackPushMsg;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackRejectEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackRejectMsg;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackSaveApplyReasonEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackSaveApplyReasonMsg;
import com.hd123.baas.sop.excel.job.feedback.FeedbackImplJob;
import com.hd123.baas.sop.job.JobHandlerService;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.feedback.FeedbackToRsH6Converter;
import com.hd123.baas.sop.remote.rssos.RsSOSClient;
import com.hd123.baas.sop.remote.rssos.feedback.BSOPFeedbackReasonSaver;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.FilterParam;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ???????????????????????????
 *
 * @author yu lilin on 2020/11/10
 */
@Slf4j
@Service
public class FeedbackServiceImpl implements FeedbackService {

  @Autowired
  private FeedbackDao feedbackDao;
  @Autowired
  private JobHandlerService jobHandlerService;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private SysConfigService configService;
  @Autowired
  private StoreService storeService;
  @Autowired
  private BaasConfigClient configClient;

  @Override
  public QueryResult<Feedback> query(String tenant, FeedbackFilter filter) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(filter, "filter");
    return feedbackDao.query(tenant, filter);
  }

  @Override
  public QueryResult<FeedbackGdSearchResult> search(String shop, String tenant, FeedbackFilter filter)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(filter, "filter");
    List<FilterParam> filterParams = buildFilterParams(filter);

    QueryRequest request = new QueryRequest();
    request.setFilters(filterParams);
    BaasResponse<List<FeedbackGdSearchResult>> response = getRsSOSClient(tenant).feedbackSearch(tenant, shop, request);
    if (!response.isSuccess()) {
      log.error("??????RsSOSClient??????,response={}", JsonUtil.objectToJson(response));
      throw new BaasException(response.getMsg());
    }
    QueryResult<FeedbackGdSearchResult> result = new QueryResult<>();
    result.setRecords(response.getData());
    result.setMore(response.getMore());
    result.setRecordCount(response.getTotal());
    return result;
  }

  /**
   * ?????????????????????
   *
   * @param uuid
   *     ?????????uuid
   * @return ?????????
   * @throws BaasException
   *     ?????????
   */
  @Override
  public Feedback get(String uuid, String... fetchParts) throws BaasException {
    Assert.notNull(uuid);
    return feedbackDao.get(uuid, fetchParts);
  }

  @Override
  @Tx
  public String save(Feedback feedback) throws BaasException {
    Assert.notNull(feedback, "feedback");
    if (feedbackDao.isExists(feedback.getBillId()) != null) {
      return feedback.getBillId();
    }
    String tenant = feedback.getTenant();
    // ????????????
    setCheckPolicy(tenant, feedback);
    // ??????
    String billId = feedbackDao.save(feedback);
    // ????????????
    if (FeedbackConfig.WORK_WX_POLICY.equals(feedback.getCheckPolicy())
        && FeedbackState.submitted.equals(feedback.getState())) {
      // ????????????
      FeedbackSubmittedMsg msg = new FeedbackSubmittedMsg();
      msg.setTenant(feedback.getTenant());
      msg.setBillId(billId);
      publisher.publishForNormal(FeedbackSubmittedEvCalExecutor.FEEDBACK_SUBMITTED_EXECUTOR_ID, msg);
    } else {
      autoAudit(tenant, billId);
    }
    return billId;
  }

  @Override
  @Tx
  public String createAndSubmit(String tenant, FeedbackCreation creation, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(creation, "creation");
    Assert.notNull(operateInfo, "operateInfo");
    if (feedbackDao.isExists(creation.getBillId()) != null) {
      return creation.getBillId();
    }
    Feedback feedback = convert(tenant, creation, operateInfo);
    // ????????????
    setCheckPolicy(tenant, feedback);
    if (StringUtils.isEmpty(feedback.getUuid())) {
      feedback.setUuid(IdGenUtils.buildRdUuid());
    }
    // ??????
    feedbackDao.insert(tenant, feedback);

    creation.setCreated(operateInfo.getTime());
    creation.setCreatorId(operateInfo.getOperator().getId());
    creation.setCreatorName(operateInfo.getOperator().getFullName());

    // ???????????????sos
    FeedbackCreateAndSubmitMsg msg = new FeedbackCreateAndSubmitMsg();
    msg.setAppId(FeedbackService.IMPORT_APPID);
    msg.setTenant(tenant);
    msg.setCreation(creation);
    publisher.publishForNormal(FeedbackCreateAndSubmitEvCallExecutor.FEEDBACK_CREATE_AND_SUBMIT_EXECUTOR_ID, msg);
    // ????????????
    if (FeedbackState.submitted.equals(feedback.getState())) {
      FeedbackSubmittedMsg submittedMsg = new FeedbackSubmittedMsg();
      submittedMsg.setTenant(feedback.getTenant());
      submittedMsg.setBillId(feedback.getBillId());
      publisher.publishForNormal(FeedbackSubmittedEvCalExecutor.FEEDBACK_SUBMITTED_EXECUTOR_ID, submittedMsg);
    }
    return feedback.getBillId();
  }

  @Override
  @Tx
  public void reject(String tenant, String appId, FeedbackRejection rejection, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(appId, "appId");
    Assert.notNull(rejection, "rejection");
    Assert.notNull(operateInfo, "operateInfo");
    Feedback item = feedbackDao.get(rejection.getBillId());
    // ????????????
    checkPre(item);
    feedbackDao.reject(rejection, operateInfo);

    rejection.setAuditTime(operateInfo.getTime());
    rejection.setAuditorId(operateInfo.getOperator().getId());
    rejection.setAuditorName(operateInfo.getOperator().getFullName());

    // ??????sos??????????????????
    FeedbackRejectMsg msg = new FeedbackRejectMsg();
    msg.setAppId(appId);
    msg.setTenant(tenant);
    msg.setRejection(rejection);

    // ????????????????????????????????????????????????
    try {
      BaasResponse<Void> response = getRsSOSClient(tenant).feedbackReject(tenant, rejection.getShop(), appId, rejection);
      if (!response.isSuccess()) {
        publisher.publishForNormal(FeedbackRejectEvCallExecutor.FEEDBACK_REJECT_EXECUTOR_ID, msg);
      }
    } catch (Exception e) {
      publisher.publishForNormal(FeedbackRejectEvCallExecutor.FEEDBACK_REJECT_EXECUTOR_ID, msg);
    }


    Feedback rejectedFeedback = feedbackDao.get(rejection.getBillId(), Feedback.FETCH_DEP_LINES, Feedback.FETCH_IMAGES);
    if (rejectedFeedback == null) {
      throw new BaasException("??????????????????????????????????????????");
    }

    // ????????????????????????
    FeedbackPushMsg pushMsg = new FeedbackPushMsg();
    pushMsg.setTenant(tenant);
    pushMsg.setFeedback(rejectedFeedback);
    try {
      BaasResponse<Void> response = getH6SOPClient(tenant).feedbackAccept(tenant, new FeedbackToRsH6Converter().convert(rejectedFeedback));
      if (!response.isSuccess()) {
        publisher.publishForNormal(FeedbackPushEvCallExecutor.FEEDBACK_PUSH_EXECUTOR_ID, pushMsg);
      }
    } catch (Exception e) {
      publisher.publishForNormal(FeedbackPushEvCallExecutor.FEEDBACK_PUSH_EXECUTOR_ID, pushMsg);
    }
  }

  @Override
  @Tx
  public void audit(String tenant, String appId, FeedbackApproval approval, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(appId, "appId");
    Assert.notNull(approval, "rejection");
    Assert.notNull(operateInfo, "operateInfo");
    Feedback item = feedbackDao.get(approval.getBillId());
    // ????????????
    checkPre(item);
    feedbackDao.audit(approval, operateInfo, item.getTenant(), item.getShop());

    approval.setAuditTime(operateInfo.getTime());
    approval.setAuditorId(operateInfo.getOperator().getId());
    approval.setAuditorName(operateInfo.getOperator().getFullName());

    // ????????????????????????????????????????????????
    Feedback auditedFeedback = feedbackDao.get(approval.getBillId(), Feedback.FETCH_DEP_LINES, Feedback.FETCH_IMAGES);
    if (auditedFeedback == null) {
      throw new BaasException("??????????????????????????????????????????");
    }

    // ??????sos??????????????????
    FeedbackApprovalMsg msg = new FeedbackApprovalMsg();
    msg.setAppId(appId);
    msg.setTenant(tenant);
    msg.setApproval(approval);

    // ????????????????????????????????????????????????
    try {
      BaasResponse<Void> response = getRsSOSClient(tenant).feedbackAudit(tenant, approval.getShop(), appId, approval);
      if (!response.isSuccess()) {
        publisher.publishForNormal(FeedbackApprovalEvCallExecutor.FEEDBACK_APPROVAL_EXECUTOR_ID, msg);
      }
    } catch (Exception e) {
      publisher.publishForNormal(FeedbackApprovalEvCallExecutor.FEEDBACK_APPROVAL_EXECUTOR_ID, msg);
    }

    // ????????????????????????
    FeedbackPushMsg pushMsg = new FeedbackPushMsg();
    pushMsg.setTenant(tenant);
    pushMsg.setFeedback(auditedFeedback);
    try {
      BaasResponse<Void> response = getH6SOPClient(tenant).feedbackAccept(tenant, new FeedbackToRsH6Converter().convert(auditedFeedback));
      if (!response.isSuccess()) {
        publisher.publishForNormal(FeedbackPushEvCallExecutor.FEEDBACK_PUSH_EXECUTOR_ID, pushMsg);
      }
    } catch (Exception e) {
      publisher.publishForNormal(FeedbackPushEvCallExecutor.FEEDBACK_PUSH_EXECUTOR_ID, pushMsg);
    }
  }

  private void checkPre(Feedback item) throws BaasException {
    if (item == null) {
      throw new BaasException("???????????????????????????");
    }
    // ??????????????????
    if (FeedbackConfig.WORK_WX_POLICY.equals(item.getCheckPolicy())) {
      if (!FeedbackState.checked.equals(item.getState())) {
        throw new BaasException("??????????????????????????????????????????????????????");
      }
    } else {
      if (!FeedbackState.submitted.equals(item.getState())) {
        throw new BaasException("????????????????????????????????????????????????");
      }
    }
  }

  @Override
  public String importFeedbacks(String tenant, String orgId, String appId, String path, OperateInfo operateInfo) throws Exception {

    String name = "IMPORT_FEEDBACK" + UUID.randomUUID().toString();

    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put(FeedbackImplJob.TENANT, tenant);
    dataMap.put(FeedbackImplJob.OSS_PATH, path);
    dataMap.put(FeedbackImplJob.ORG_ID, orgId);

    dataMap.put(FeedbackService.OPERATE_TIME,
        StringUtil.dateToString(operateInfo.getTime(), FeedbackService.DATETIME_FORMAT));
    dataMap.put(FeedbackService.OPERATOR_ID, operateInfo.getOperator().getId());
    dataMap.put(FeedbackService.OPERATOR_NAME, operateInfo.getOperator().getNamespace());

    jobHandlerService.getScheduler().getContext().putAll(new HashMap<String, Object>() {
      {
        put(FeedbackImplJob.TENANT, tenant);
      }
    });
    jobHandlerService.startJob(name, FeedbackImplJob.class, dataMap);

    return name;
  }

  @Override
  public List<String> listReasons(String tenant, String orgId, FeedbackReasonType type) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notNull(type, "type");
    return feedbackDao.listReasons(tenant, orgId, type);
  }

  @Override
  public void saveAuditReason(String tenant, String orgId, List<String> reasons, FeedbackReasonType type, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notEmpty(reasons, "reasons");
    Assert.notNull(type, "type");
    Assert.notNull(operateInfo, "operateInfo");
    if (!(FeedbackReasonType.approve.equals(type) || FeedbackReasonType.reject.equals(type))) {
      throw new BaasException("???????????????");
    }
    feedbackDao.deleteReasons(tenant, orgId, type);
    feedbackDao.saveReasons(tenant, orgId, reasons, type, operateInfo);
  }

  @Override
  public void saveApplyReason(String tenant, String orgId, List<String> reasons, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notEmpty(reasons, "reasons");
    Assert.notNull(operateInfo, "operateInfo");
    feedbackDao.deleteReasons(tenant, orgId, FeedbackReasonType.apply);
    feedbackDao.saveReasons(tenant, orgId, reasons, FeedbackReasonType.apply, operateInfo);

    // ??????????????????????????????
    FeedbackSaveApplyReasonMsg msg = new FeedbackSaveApplyReasonMsg();
    msg.setTenant(tenant);
    msg.setReasons(reasons);
    publisher.publishForNormal(FeedbackSaveApplyReasonEvCallExecutor.FEEDBACK_SAVE_APPLY_REASON_EXECUTOR_ID, msg);
  }

  @Override
  public void batchUpdateState(String tenant, List<String> billIds, FeedbackState state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(billIds, "billIds");
    Assert.notNull(state, "state");
    Assert.notNull(operateInfo, "operateInfo");

    feedbackDao.batchUpdateState(billIds, state, operateInfo);
  }

  @Override
  public String isExists(String id) {
    Assert.hasText(id, "id");
    return feedbackDao.isExists(id);
  }

  @Override
  public void saveReasons(String tenant, FeedbackReasonSaver feedReasonV2, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(feedReasonV2, "feedbackReasonBySortCode");
    Assert.notNull(feedReasonV2.getReasons(), "reasons");
    if (feedReasonV2.getType() == null) {
      throw new BaasException("????????????<{0}>??????", feedReasonV2.getType());
    }
    BSOPFeedbackReasonSaver feedbackReasonSaver = new BSOPFeedbackReasonSaver();
    feedbackReasonSaver.setType(sopTypeToSos(feedReasonV2.getType()));
    feedbackReasonSaver.setSortCode(feedReasonV2.getSortCode());
    feedbackReasonSaver.setSortName(feedReasonV2.getSortName());
    feedbackReasonSaver.setReasons(feedReasonV2.getReasons());
    BaasResponse<Void> response = getRsSOSClient(tenant).feedbackSaveApplyReasonWithSort(tenant, feedReasonV2.getOrgId(), feedbackReasonSaver);
    if (!response.isSuccess()) {
      throw new BaasException("??????????????????????????????:{0}", response.getMsg());
    }
  }

  @Override
  public List<FeedbackReasonSaver> queryReasons(String tenant, String orgId, String type, String sortCode) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    Assert.notNull(type, "type");
    FeedbackReasonType feedbackReasonType = StringUtil.toEnum(type, FeedbackReasonType.class);
    if (feedbackReasonType == null) {
      throw new BaasException("??????????????????");
    }
    BaasResponse<List<String>> response = getRsSOSClient(tenant)
        .feedbackApplyReasonList(tenant, DefaultOrgIdConvert.toH6DefOrgId(orgId), sopTypeToSos(feedbackReasonType), sortCode);
    if (!response.isSuccess()) {
      throw new BaasException("????????????:{0}", response.getMsg());
    }
    List<String> reasonSavers = response.getData();
    List<FeedbackReasonSaver> savers = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(reasonSavers)) {
      FeedbackReasonSaver saver = new FeedbackReasonSaver();
      saver.setType(feedbackReasonType);
      saver.setOrgId(orgId);
      saver.setSortCode(sortCode);
      saver.setSortName("");
      saver.setReasons(reasonSavers);
      savers.add(saver);
    }
    return savers;
  }

  @Override
  @Tx
  public void check(String tenant, String billId, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(billId, "billId");
    Feedback feedback = get(billId);
    if (!FeedbackState.submitted.equals(feedback.getState())) {
      log.info("???????????????????????????,????????????,tenant={},feedback={}", tenant, JsonUtil.objectToJson(feedback));
    }
    feedbackDao.updateState(tenant, billId, FeedbackState.checked, operateInfo);
  }

  @Override
  public void addSpNo(String tenant, String billId, String spNo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(billId, "billId");
    Assert.hasText(spNo, "spNo");
    feedbackDao.addSpNo(tenant, billId, spNo);
  }

  @Override
  public void saveExt(String tenant, String billId, FeedbackExt ext) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(billId, "billId");
    Assert.notNull(ext, "ext");
    feedbackDao.addExt(tenant, billId, ext);
  }

  private void autoAudit(String tenant, String billId) throws BaasException {
    Feedback feedback = get(billId, Feedback.FETCH_DEP_LINES, Feedback.FETCH_IMAGES);
    String shop = feedback.getShop();
    StoreFilter filter = new StoreFilter();
    filter.setIdIn(Collections.singletonList(shop));
    QueryResult<Store> storeResult = storeService.query(tenant, filter);
    if (CollectionUtils.isEmpty(storeResult.getRecords())) {
      throw new BaasException("??????<{}>?????????", shop);
    }
    Store store = storeResult.getRecords().get(0);
    String orgId = store.getOrgId() == null ? OrgConstants.DEFAULT_MAS_ORG_ID : store.getOrgId();
    SysConfig sysConfig = configService.get(feedback.getTenant(), orgId, SysConfig.FEEDBACK_AUTO_AUDIT);
    log.info("??????<{}>??????<{}>??????????????????:<{}>", tenant, orgId, JSON.toJSONString(sysConfig));
    if (sysConfig == null) {
      log.info("???????????????????????????????????????");
      return;
    }
    log.info("?????????????????????{}", JsonUtil.objectToJson(feedback));
    FeedbackAutoAuditConfig autoAuditConfig = JSON.parseObject(sysConfig.getCfValue(), FeedbackAutoAuditConfig.class);
    if (!autoAuditConfig.getEnable()) {
      log.info("?????????????????????????????????");
      return;
    }
    String gradeName = feedback.getGradeName();
    String channel = feedback.getChannel();
    String categoryCode = feedback.getGdTypeCode();
    BigDecimal payTotal = feedback.getTotal();
    if (StringUtils.isBlank(gradeName)) {
      log.info("???????????????<{}>????????????", feedback.getBillId());
      return;
    }

    FeedbackAutoAuditConfig.SourceRule sourceRule = autoAuditConfig.getSourceRules()
        .stream()
        .filter(s -> s.getSource().equals(channel))
        .findFirst()
        .orElse(null);
    if (sourceRule != null) {
      FeedbackAutoAuditConfig.Rule gradeRule = sourceRule.getRules()
          .stream()
          .filter(s -> s.getGradeName().equals(gradeName))
          .findFirst()
          .orElse(null);
      if (gradeRule != null && gradeRule.getApplyAmount().compareTo(payTotal) >= 0) {
        FeedbackAutoAuditConfig.RuleDepartmentLine ruleDepartmentLine = gradeRule.getLines()
            .stream()
            .filter(s -> isParentCategory(s.getCategoryCode(), categoryCode))
            .findFirst()
            .orElse(null);
        if (ruleDepartmentLine != null) {
          OperateInfo sysOperateInfo = feedback.getLastModifyInfo();
          FeedbackApproval feedbackApproval = new FeedbackApproval();
          feedbackApproval.setShop(shop);
          feedbackApproval.setAuditorId(sysOperateInfo.getOperator().getId());
          feedbackApproval.setAuditorName(sysOperateInfo.getOperator().getFullName());
          feedbackApproval.setAuditTime(sysOperateInfo.getTime());
          feedbackApproval.setBillId(feedback.getBillId());

          BigDecimal realPayTotal = gradeRule.getRate().multiply(payTotal).multiply(new BigDecimal("0.01"));
          realPayTotal = toHalfUp(realPayTotal);
          FeedbackDepLine feedbackDepLine = new FeedbackDepLine();
          feedbackDepLine.setUuid(UUID.randomUUID().toString());
          feedbackDepLine.setDepCode(ruleDepartmentLine.getDepCode());
          feedbackDepLine.setDepName(ruleDepartmentLine.getDepName());
          feedbackDepLine.setRate(new BigDecimal(100));
          feedbackDepLine.setTotal(realPayTotal);
          feedbackApproval.setFeedbackDepLines(Collections.singletonList(feedbackDepLine));

          feedbackApproval.setPayRate(gradeRule.getRate());
          feedbackApproval.setPayTotal(realPayTotal);
          feedbackApproval.setNote("??????????????????");
          feedbackApproval.setReason(gradeRule.getReason());
          log.info("????????????????????????????????????{}", JSON.toJSONString(feedbackApproval));
          this.audit(tenant, "sop-service", feedbackApproval, sysOperateInfo);
        }
      }
    }
  }

  private BigDecimal toHalfUp(BigDecimal decimal) {
    return decimal.setScale(2, BigDecimal.ROUND_HALF_UP);
  }

  /**
   * ????????????c1?????????c2???????????? ????????????c1???c2??????0????????????????????????
   */
  private boolean isParentCategory(String c1, String c2) {
    if (c1 == null || c2 == null) {
      return false;
    }
    if (c1.length() > c2.length()) {
      return false;
    }
    String sub = c2.substring(0, c1.length());
    return c1.equals(sub);
  }

  protected OperateInfo getSysOperateInfo() {
    OperateInfo operateInfo = new OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("????????????");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }

  private void setCheckPolicy(String tenant, Feedback feedback) {
    FeedbackConfig config = configClient.getConfig(tenant, FeedbackConfig.class);
    if (config != null && config.getCheckPolicy().equals(FeedbackConfig.WORK_WX_POLICY)) {
      feedback.setCheckPolicy(config.getCheckPolicy());
    } else {
      feedback.setCheckPolicy(FeedbackConfig.DEFAULT_POLICY);
    }
  }

  private Feedback convert(String tenant, FeedbackCreation creation, OperateInfo operateInfo) {
    Feedback feedback = new Feedback();
    feedback.setBillId(creation.getBillId());
    feedback.setOrgId(creation.getOrgId());
    feedback.setAppId(FeedbackService.IMPORT_APPID);
    feedback.setTenant(tenant);
    feedback.setShop(creation.getShop());
    feedback.setShopNo(creation.getShopNo());
    feedback.setShopName(creation.getShopName());
    feedback.setReceiptNum(creation.getReceiptNum());
    feedback.setReceiptLineId(creation.getReceiptLineId());
    feedback.setGdUuid(creation.getGdUuid());
    feedback.setGdCode(creation.getGdCode());
    feedback.setGdInputCode(creation.getGdInputCode());
    feedback.setGdName(creation.getGdName());
    feedback.setMunit(creation.getMunit());
    feedback.setMinMunit(creation.getMinMunit());
    feedback.setQpc(creation.getQpc());
    feedback.setGdTypeCode(creation.getGdTypeCode());
    feedback.setGdTypeName(creation.getGdTypeName());
    feedback.setType(creation.getType());
    feedback.setDeliveryTime(creation.getDeliveryTime());
    feedback.setSinglePrice(creation.getSinglePrice());
    feedback.setReceiptQty(creation.getReceiptQty());
    feedback.setQty(creation.getQty());
    feedback.setTotal(creation.getTotal());
    feedback.setApplyReason(creation.getApplyReason());
    feedback.setApplyNote(creation.getApplyNote());
    feedback.setCreateInfo(operateInfo);
    feedback.setLastModifyInfo(operateInfo);
    feedback.setSubmitterId(operateInfo.getOperator().getId());
    feedback.setSubmitterName(operateInfo.getOperator().getFullName());
    feedback.setSubmitTime(operateInfo.getTime());
    feedback.setState(FeedbackState.submitted);
    if (StringUtil.isNullOrBlank(creation.getChannel())) {
      feedback.setChannel(PFeedback.DEFAULT_CHANNEL);
    } else {
      feedback.setChannel(creation.getChannel());
    }
    return feedback;
  }

  private List<FilterParam> buildFilterParams(FeedbackFilter filter) {
    List<FilterParam> filterParams = new ArrayList<>();
    if (!StringUtil.isNullOrBlank(filter.getKeywordLike())) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_KEYWORD_LIKE);
      filterParam.setValue(filter.getKeywordLike());
      filterParams.add(filterParam);
    }
    if (!StringUtil.isNullOrBlank(filter.getTypeNameLike())) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_TYPENAME_LIKE);
      filterParam.setValue(filter.getTypeNameLike());
      filterParams.add(filterParam);
    }
    if (!StringUtil.isNullOrBlank(filter.getShopNameLike())) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_SHOPNAME_LIKE);
      filterParam.setValue(filter.getShopNameLike());
      filterParams.add(filterParam);
    }
    if (filter.getTypeEq() != null) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_TYPE_EQ);
      filterParam.setValue(filter.getTypeEq());
      filterParams.add(filterParam);
    }
    if (!StringUtil.isNullOrBlank(filter.getApplyReasonEq())) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_APPLYREASON_EQ);
      filterParam.setValue(filter.getApplyReasonEq());
      filterParams.add(filterParam);
    }
    if (!StringUtil.isNullOrBlank(filter.getApplyReasonLike())) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_APPLYREASON_LIKE);
      filterParam.setValue(filter.getApplyReasonLike());
      filterParams.add(filterParam);
    }
    if (filter.getResultEq() != null) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_RESULT_EQ);
      filterParam.setValue(filter.getResultEq());
      filterParams.add(filterParam);
    }
    if (filter.getStateEq() != null) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_STATE_EQ);
      filterParam.setValue(filter.getStateEq());
      filterParams.add(filterParam);
    }
    if (filter.getDeliveryTimeIn() != null && filter.getDeliveryTimeIn().size() == 2) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_DELIVERYTIME_BETWEEN);
      filterParam.setValue(filter.getDeliveryTimeIn());
      filterParams.add(filterParam);
    }
    if (filter.getAuditTimeIn() != null && filter.getAuditTimeIn().size() == 2) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_AUDITTIME_BETWEEN);
      filterParam.setValue(filter.getAuditTimeIn());
      filterParams.add(filterParam);
    }
    if (!StringUtil.isNullOrBlank(filter.getGoodsCodeEq())) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_GOODSCODE_EQ);
      filterParam.setValue(filter.getGoodsCodeEq());
      filterParams.add(filterParam);
    }

    if (CollectionUtils.isNotEmpty(filter.getGoodsCodeIn())) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_GOODSCODE_IN);
      filterParam.setValue(filter.getGoodsCodeIn());
      filterParams.add(filterParam);
    }
    if (CollectionUtils.isNotEmpty(filter.getDeliveryTimeRealIn())) {
      FilterParam filterParam = new FilterParam();
      filterParam.setProperty(FeedbackFilter.CONDITION_DELIVERYTIME_IN);
      filterParam.setValue(filter.getDeliveryTimeRealIn());
      filterParams.add(filterParam);
    }
    return filterParams;
  }


  private Integer sopTypeToSos(FeedbackReasonType feedbackReasonType) {
    if (feedbackReasonType == null) {
      return null;
    }
    Integer type = null;
    switch (feedbackReasonType) {
      case apply:
        type = 0;
        break;
      case approve:
        type = 1;
        break;
      case reject:
        type = 2;
        break;
      default:
        break;
    }
    return type;
  }

  private RsH6SOPClient getH6SOPClient(String tenant) throws BaasException {
    return feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
  }

  private RsSOSClient getRsSOSClient(String tenant) throws BaasException {
    return feignClientMgr.getClient(tenant, null, RsSOSClient.class);
  }

//  public void checkResponse(BaasResponse response) throws IllegalArgumentException {
//    if (response.isSuccess()) {
//      return;
//    }
//    throw new IllegalArgumentException(response.getMsg());
//  }

//  private FeedbackReasonType sosTypeToSop(Integer type) {
//    if (type == null) {
//      return null;
//    }
//    FeedbackReasonType feedbackReasonType = null;
//    if (type == 0) {
//      feedbackReasonType = FeedbackReasonType.apply;
//    } else if (type == 1) {
//      feedbackReasonType = FeedbackReasonType.approve;
//    } else if (type == 3) {
//      feedbackReasonType = FeedbackReasonType.reject;
//    }
//    return feedbackReasonType;
//  }
}
