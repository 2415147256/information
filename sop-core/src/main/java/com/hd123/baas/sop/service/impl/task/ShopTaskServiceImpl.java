package com.hd123.baas.sop.service.impl.task;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.MessageConfig;
import com.hd123.baas.sop.config.ShopTaskTransferMessageConfig;
import com.hd123.baas.sop.service.impl.task.message.MessageTag;
import com.hd123.baas.sop.service.api.message.*;
import com.hd123.baas.sop.service.api.task.*;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupShop;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupType;
import com.hd123.baas.sop.service.api.taskplan.AssignTaskPlanType;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanLine;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanType;
import com.hd123.baas.sop.service.api.taskpoints.TaskPoints;
import com.hd123.baas.sop.service.api.taskpoints.TaskPointsOccurredType;
import com.hd123.baas.sop.service.api.taskpoints.TaskPointsService;
import com.hd123.baas.sop.service.dao.task.*;
import com.hd123.baas.sop.service.dao.taskgroup.TaskGroupShopDaoBof;
import com.hd123.baas.sop.service.dao.taskplan.TaskPlanLineDaoBof;
import com.hd123.baas.sop.service.impl.taskplan.TaskPlanNewServiceImpl;
import com.hd123.baas.sop.service.api.task.BShopTaskTransferDetailReqType;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.shoptask.ShopTaskSummaryEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.shoptask.ShopTaskSummaryMsg;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.AppMessageSaveNewReq;
import com.hd123.baas.sop.remote.fms.bean.MessageConvertUtil;
import com.hd123.baas.sop.utils.DateUtil;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.BaasStatus;
import com.qianfan123.baas.common.http2.BaasResponse;
import com.qianfan123.baas.common.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Service
@Slf4j
public class ShopTaskServiceImpl implements ShopTaskService {

  @Autowired
  private ShopTaskDaoBof dao;
  @Autowired
  private ShopTaskLogDaoBof shopTaskLogDao;

  @Autowired
  private TaskGroupShopDaoBof taskGroupShopDao;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private ShopTaskSummaryDaoBof shopTaskSummaryDao;
  @Autowired
  private ShopTaskTransferDaoBof shopTaskTransferDao;
  @Autowired
  private BaasConfigClient baasConfigClient;
  @Autowired
  private MessageService messageService;
  @Autowired
  private TaskPointsService taskPointsService;
  @Autowired
  private TaskPlanLineDaoBof taskPlanLineDao;
  @Autowired
  private TaskReadHistoryService taskReadHistoryService;
  @Autowired
  private ShopTaskWatcherService shopTaskWatcherService;
  @Autowired
  private FmsClient fmsClient;

  @Override
  public List<ShopTask> getByShopTaskGroupId(String tenant, String shopTaskGroupId, String... sort) {
    Assert.notNull(tenant, "??????");
    Assert.notNull(shopTaskGroupId, "shopTaskGroupId");
    List<ShopTask> byShopTaskGroupId = dao.getByShopTaskGroupId(tenant, shopTaskGroupId, sort);
    return byShopTaskGroupId;
  }

  @Override
  @Tx
  public void finish(String tenant, String uuid, String finishAppId, String feedback, OperateInfo finishInfo)
      throws BaasException {
    Assert.notNull(tenant, "??????");
    Assert.notNull(uuid, "uuid");
    ShopTask shopTask = this.get(tenant, uuid);
    if (shopTask == null) {
      throw new BaasException("?????????????????????");
    }
    if (ShopTaskState.EXPIRED.name().equals(shopTask.getState().name())) {
      throw new BaasException("??????????????????");
    }
    if (ShopTaskState.FINISHED.name().equals(shopTask.getState().name())) {
      log.info("?????????<{}>?????????", shopTask.getUuid());
      throw new BaasException("??????????????????");
    }
    dao.finish(tenant, uuid, finishAppId, feedback, finishInfo);
  }

  @Override
  @Tx
  public void batchInsert(String tenant, List<ShopTask> shopTasks) throws BaasException {
    Assert.notNull(tenant, "??????");
    Assert.notEmpty(shopTasks);
    dao.batchInsert(tenant, shopTasks);
  }

  @Override
  @Tx
  public void delete(String tenant, String uuid) {
    Assert.notNull(tenant, "??????");
    Assert.notNull(uuid, "uuid");
    dao.delete(tenant, uuid);
  }

  @Override
  public ShopTask getByShopTaskGroupIdAndTaskPlanId(String tenant, String shopTaskGroupId, String taskPlanId) {
    Assert.notNull(tenant, "??????");
    Assert.notNull(shopTaskGroupId, "shopTaskGroupId");
    Assert.notNull(taskPlanId, "taskPlanId");
    return dao.getByShopTaskGroupIdAndTaskPlanId(tenant, shopTaskGroupId, taskPlanId);
  }

  @Override
  @Tx
  public void batchSaveNew(String tenant, ShopTask shopTask) throws BaasException {
    Assert.notNull(tenant, "??????");
    Assert.notNull(shopTask, "shopTask");
    dao.insert(tenant, shopTask);
  }

  @Override
  @Tx
  public void batchCheckLastOne(String tenant, String shop, String groupId, String lastShopTaskGroupId) {
    Assert.notNull(tenant, "??????");
    Assert.notNull(groupId, "?????????");
    Assert.notEmpty(lastShopTaskGroupId, "???????????????");
    List<ShopTask> oldShopTasks = dao.getByShopTaskGroupId(tenant, lastShopTaskGroupId);
    if (CollectionUtils.isEmpty(oldShopTasks)) {
      return;
    }
    for (ShopTask shopTask : oldShopTasks) {
      if (shopTask.getState().equals(ShopTaskState.UNFINISHED)) {
        dao.setExpired(tenant, shopTask.getUuid());
      }
    }
  }

  @Override
  public ShopTask get(String tenant, String uuid) {
    Assert.notNull(tenant, "??????");
    Assert.notNull(uuid, "uuid");
    return dao.get(tenant, uuid);
  }

  @Override
  public ShopTask getWithLock(String tenant, String uuid) {
    Assert.notNull(tenant, "??????");
    Assert.notNull(uuid, "uuid");
    return dao.getWithLock(tenant, uuid);
  }

  @Override
  public QueryResult<ShopTask> query(String tenant, QueryDefinition qd, String... fetchParts) {
    Assert.notNull(tenant, "??????");
    Assert.notNull(qd, "qd");
    QueryResult<ShopTask> query = dao.query(tenant, qd);
    List<ShopTask> records = query.getRecords();
    if (CollectionUtils.isNotEmpty(records)) {
      for (ShopTask record : records) {
        fetchParts(tenant, record, fetchParts);
      }
    }
    return query;
  }

  @Override
  public QueryResult<ShopTask> querySummary(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "??????");
    Assert.notNull(qd, "qd");
    QueryResult<ShopTask> query = dao.querySummary(tenant, qd);
    return query;
  }

  @Override
  public List<ShopTask> list(String tenant, String owner, String... fetchParts) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owner, "shopPlanTaskId");
    List<ShopTask> list = dao.list(tenant, owner);
    if (CollectionUtils.isNotEmpty(list)) {
      for (ShopTask record : list) {
        fetchParts(tenant, record, fetchParts);
      }
    }
    return list;
  }

  @Override
  public List<ShopTask> listByLoginId(String tenant, String owner, String loginId, String... fetchParts) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owner, "shopPlanTaskId");
    List<ShopTask> list = dao.listByLoginId(tenant, owner, loginId);
    if (CollectionUtils.isNotEmpty(list)) {
      for (ShopTask record : list) {
        fetchPartsByLoginId(tenant, record, loginId, fetchParts);
      }
    }
    return list;
  }

  @Override
  @Tx
  public void saveTaskLog(String tenant, ShopTaskLog shopTaskLog, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(shopTaskLog);
    Assert.notNull(shopTaskLog.getFeedback(), "feedback");
    Assert.notNull(operateInfo, "operateInfo");
    checkTaskLog(tenant, shopTaskLog.getUuid());
    shopTaskLogDao.update(tenant, shopTaskLog, operateInfo);
  }

  @Override
  @Tx
  public void saveAssignableTaskLog(String tenant, ShopTaskLog shopTaskLog, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(shopTaskLog);
    Assert.notNull(shopTaskLog.getFeedback(), "feedback");
    Assert.notNull(operateInfo, "operateInfo");
    shopTaskLogDao.update(tenant, shopTaskLog, operateInfo);
  }

  @Override
  @Tx
  public void finishShopTaskLog(String tenant, ShopTaskLog shopTaskLog, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(shopTaskLog);
    Assert.notNull(shopTaskLog.getFeedback(), "feedback");
    Assert.notNull(shopTaskLog.getScore(), "score");
    Assert.notNull(operateInfo, "operateInfo");
    shopTaskLog.setState(ShopTaskState.FINISHED.name());
    checkTaskLog(tenant, shopTaskLog.getUuid());
    shopTaskLogDao.update(tenant, shopTaskLog, operateInfo);

    addScore(tenant, shopTaskLog.getOwner(), operateInfo);
  }

  @Override
  @Tx
  public void finishAssignableShopTaskLog(String tenant, ShopTaskLog shopTaskLog, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(shopTaskLog);
    Assert.notNull(shopTaskLog.getFeedback(), "feedback");
    Assert.notNull(operateInfo, "operateInfo");
    shopTaskLog.setState(ShopTaskState.FINISHED.name());
    shopTaskLogDao.update(tenant, shopTaskLog, operateInfo);
  }

  @Override
  @Tx
  public void batchSaveNew(String tenant, String plan, String shop, List<ShopTask> tasks, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(plan, "plan");
    Assert.notNull(shop, "shop");
    checkTask(plan, shop, tasks);
    ShopTask shopTask = tasks.get(0);
    ShopTaskSummary summary = new ShopTaskSummary();
    summary.setTenant(tenant);
    summary.setUuid(UUID.randomUUID().toString());
    summary.setState(ShopTaskState.UNFINISHED);

    summary.setPlan(shopTask.getPlan());
    summary.setPlanCode(shopTask.getPlanCode());
    summary.setPlanName(shopTask.getPlanName());
    summary.setPlanPeriod(shopTask.getPlanPeriod());
    // ???????????????
    summary.setPlanPeriodCode(shopTask.getPlanPeriod());
    summary.setPlanStartTime(shopTask.getPlanStartTime());
    summary.setPlanEndTime(shopTask.getPlanEndTime());

    summary.setShop(shopTask.getShop());
    summary.setShopName(shopTask.getShopName());
    summary.setShopCode(shopTask.getShopCode());

    summary.setPoint(tasks.stream().map(ShopTask::getPoint).reduce(BigDecimal.ZERO, BigDecimal::add));
    summary.setRank(BigDecimal.ZERO);

    List<ShopTaskLog> logs = new ArrayList<>();
    for (ShopTask task : tasks) {
      task.setLastModifyInfo(operateInfo);
      task.setPlanPeriodCode(summary.getPlanPeriodCode());
      task.setOwner(summary.getUuid());
      task.setCreateInfo(operateInfo);
      if (task.getUuid() == null) {
        task.setUuid(UUID.randomUUID().toString());
      }
      task.getLogs().stream().forEach(s -> s.setOwner(task.getUuid()));
      logs.addAll(task.getLogs());
    }
    shopTaskSummaryDao.insert(tenant, summary);
    dao.batchInsert(tenant, tasks);
    shopTaskLogDao.batchInsert(tenant, logs, operateInfo);
  }

  @Override
  public ShopTaskLog getLog(String tenant, String logId) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(logId, "logId");
    return shopTaskLogDao.get(tenant, logId);
  }

  @Override
  @Tx
  public String transfer(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getShopTaskLogId(), "shopTaskLogId");
    Assert.hasText(shopTaskTransfer.getShopTaskId(), "shopTaskId");
    Assert.hasText(shopTaskTransfer.getShop(), "shopId");
    Assert.hasText(shopTaskTransfer.getShopCode(), "shopCode");
    Assert.hasText(shopTaskTransfer.getTransferFrom(), "transferFrom");
    Assert.hasText(shopTaskTransfer.getTransferTo(), "transferTo");

    // ????????????????????????????????????
    List<ShopTaskTransfer> shopTaskTransferList = shopTaskTransferDao.listByShopTaskLogIdAndTransferFrom(tenant,
        shopTaskTransfer.getShopTaskLogId(), shopTaskTransfer.getTransferFrom());
    if (CollectionUtils.isNotEmpty(shopTaskTransferList)
        && shopTaskTransferList.stream().anyMatch(item -> ShopTaskTransferState.TRANSFER == item.getState())) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "?????????????????????????????????"));
    }
    // ???????????????????????????????????????
    checkShopTask(tenant, shopTaskTransfer.getShopTaskId());

    if (StringUtils.isEmpty(shopTaskTransfer.getUuid())) {
      String uuid = IdGenUtils.buildRdUuid();
      shopTaskTransfer.setUuid(uuid);
    }
    shopTaskTransfer.setTransferTime(new Date());
    shopTaskTransfer.setState(ShopTaskTransferState.TRANSFER);
    shopTaskTransfer.setTenant(tenant);
    shopTaskTransferDao.transfer(tenant, shopTaskTransfer);
    // ????????????????????????????????????????????????????????????
    sendTransferMessage(tenant, shopTaskTransfer, operateInfo);
    return shopTaskTransfer.getUuid();
  }

  @Override
  @Tx
  public String batchTransfer(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException {
    //????????????????????????shopTaskId
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getShopTaskId(), "shopTaskId");
    Assert.hasText(shopTaskTransfer.getShop(), "shopId");
    Assert.hasText(shopTaskTransfer.getShopCode(), "shopCode");
    Assert.hasText(shopTaskTransfer.getTransferFrom(), "transferFrom");
    Assert.hasText(shopTaskTransfer.getTransferTo(), "transferTo");
    //???????????????????????????log????????????????????????,?????????log????????????????????????????????????????????????????????????
    shopTaskTransferDao.cancelLogTransfer(tenant, shopTaskTransfer.getShopTaskId(), operateInfo);
    //???????????????????????????????????????????????????
    List<ShopTaskLog> shopTaskLogList = new ArrayList<>();
    List<ShopTaskLog> list = shopTaskLogDao.list(tenant, shopTaskTransfer.getShopTaskId(), operateInfo.getOperator().getId());
    if (CollectionUtils.isNotEmpty(list)) {
      shopTaskLogList = list.stream().filter(log -> ShopTaskLogState.UNFINISHED.name().equals(log.getState())).collect(Collectors.toList());
    }
    if (CollectionUtils.isEmpty(shopTaskLogList)) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????????????????"));
    }
    // ????????????????????????????????????????????????
    List<ShopTaskTransfer> shopTaskTransferList = shopTaskTransferDao.listBatchByShopTaskIdAndTransferFrom(tenant,
        shopTaskTransfer.getShopTaskId(), shopTaskTransfer.getTransferFrom());
    if (CollectionUtils.isNotEmpty(shopTaskTransferList)
        && shopTaskTransferList.stream().anyMatch(item -> ShopTaskTransferState.TRANSFER == item.getState())) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "???????????????????????????????????????"));
    }
    // ???????????????????????????????????????
    checkShopTask(tenant, shopTaskTransfer.getShopTaskId());

    if (StringUtils.isEmpty(shopTaskTransfer.getUuid())) {
      String uuid = IdGenUtils.buildRdUuid();
      shopTaskTransfer.setUuid(uuid);
    }
    shopTaskTransfer.setTransferTime(new Date());
    shopTaskTransfer.setState(ShopTaskTransferState.TRANSFER);
    shopTaskTransfer.setTenant(tenant);
    shopTaskTransfer.setType(ShopTaskTransferType.BATCH.name());
    shopTaskTransferDao.transfer(tenant, shopTaskTransfer);
    // ??????????????????????????????
    batchInsertShopTaskLogTransfer(tenant, shopTaskTransfer, shopTaskLogList);
    // ????????????????????????????????????????????????????????????
    sendBatchTransfer(tenant, shopTaskTransfer, operateInfo);
    return shopTaskTransfer.getUuid();
  }

  private void batchInsertShopTaskLogTransfer(String tenant, ShopTaskTransfer shopTaskTransfer, List<ShopTaskLog> shopTaskLogList) throws BaasException {
    List<ShopTaskTransfer> shopTaskTransferList = new ArrayList<>();
    for (ShopTaskLog shopTaskLog : shopTaskLogList) {
      ShopTaskTransfer shopTaskTransferLog = buildShopTaskTransfer(tenant, shopTaskTransfer);
      //??????ID
      shopTaskTransferLog.setUuid(IdGenUtils.buildRdUuid());
      shopTaskTransferLog.setBatchId(shopTaskTransfer.getUuid());
      shopTaskTransferLog.setShopTaskLogId(shopTaskLog.getUuid());
      shopTaskTransferList.add(shopTaskTransferLog);
    }
    log.info("????????????????????????????????????{}", JSONUtil.safeToJson(shopTaskTransferList));
    if (CollectionUtils.isNotEmpty(shopTaskTransferList)) {
      shopTaskTransferDao.batchSaveShopTaskLogTransfer(tenant, shopTaskTransferList);
    }
  }

  private ShopTaskTransfer buildShopTaskTransfer(String tenant, ShopTaskTransfer shopTaskTransfer) {
    ShopTaskTransfer shopTaskTransferLog = new ShopTaskTransfer();
    shopTaskTransferLog.setShop(shopTaskTransfer.getShop());
    shopTaskTransferLog.setShopTaskId(shopTaskTransfer.getShopTaskId());
    shopTaskTransferLog.setShopTaskLogId(shopTaskTransfer.getShopTaskLogId());
    shopTaskTransferLog.setTransferTo(shopTaskTransfer.getTransferTo());
    shopTaskTransferLog.setTransferToName(shopTaskTransfer.getTransferToName());
    shopTaskTransferLog.setTransferTime(shopTaskTransfer.getTransferTime());
    shopTaskTransferLog.setTransferToPositionName(shopTaskTransfer.getTransferToPositionName());
    shopTaskTransferLog.setTransferToPositionCode(shopTaskTransfer.getTransferToPositionCode());
    shopTaskTransferLog.setTransferFrom(shopTaskTransfer.getTransferFrom());
    shopTaskTransferLog.setTransferFromName(shopTaskTransfer.getTransferFromName());
    shopTaskTransferLog.setBatchId(shopTaskTransfer.getBatchId());
    shopTaskTransferLog.setType(shopTaskTransfer.getType());
    shopTaskTransferLog.setShopCode(shopTaskTransfer.getShopCode());
    shopTaskTransferLog.setShopName(shopTaskTransfer.getShopName());
    shopTaskTransferLog.setState(shopTaskTransfer.getState());
    shopTaskTransferLog.setTransferTime(shopTaskTransfer.getTransferTime());
    shopTaskTransferLog.setOperTime(shopTaskTransfer.getOperTime());
    shopTaskTransferLog.setReason(shopTaskTransfer.getReason());
    shopTaskTransferLog.setTenant(tenant);
    return shopTaskTransferLog;
  }

  private Message buildAcceptTransferFromMessage(String tenant, ShopTaskTransfer shopTaskTransfer, String path,
      String title, String contentText, String tag) {
    Message message = new Message();
    message.setTenant(tenant);
    message.setShop(shopTaskTransfer.getShop());
    message.setShopCode(shopTaskTransfer.getShopCode());
    message.setShopName(shopTaskTransfer.getShopName());
    message.setAction(MessageAction.PAGE);
    message.setActionInfo(path);
    message.setTitle(title);
    message.setType(MessageType.NOTICE);
    message.setTag(tag);
    message.setSource(shopTaskTransfer.getShopTaskId());
    Map<MessageContentKey, String> content = new LinkedHashMap<>();
    content.put(MessageContentKey.TEXT, "??????????????????????????????????????????" + contentText);
    message.setContent(content);
    message.setUserId(shopTaskTransfer.getTransferFrom());
    return message;
  }

  private Message buildRefuseTransferFromMessage(String tenant, ShopTaskTransfer shopTaskTransfer, String path,
      String title, String contentText, String tag) {
    Message message = new Message();
    message.setTenant(tenant);
    message.setShop(shopTaskTransfer.getShop());
    message.setShopCode(shopTaskTransfer.getShopCode());
    message.setShopName(shopTaskTransfer.getShopName());
    message.setAction(MessageAction.PAGE);
    message.setActionInfo(path);
    message.setTitle(title);
    message.setType(MessageType.NOTICE);
    message.setTag(tag);
    message.setSource(shopTaskTransfer.getShopTaskId());
    Map<MessageContentKey, String> content = new LinkedHashMap<>();
    content.put(MessageContentKey.TEXT, "??????????????????????????????????????????" + contentText);
    message.setContent(content);
    message.setUserId(shopTaskTransfer.getTransferFrom());
    return message;
  }

  @Override
  @Tx
  public void accept(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getUuid(), "uuid");
    Assert.hasText(shopTaskTransfer.getShopTaskId(), "shopTaskId");
    ShopTaskTransfer shopTaskTransferHistory = getByUuid(tenant, shopTaskTransfer.getUuid());
    if (shopTaskTransferHistory == null || ShopTaskTransferState.TRANSFER != shopTaskTransferHistory.getState()) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????"));
    }
    //?????????????????????
    checkShopTask(tenant, shopTaskTransfer.getShopTaskId());
    shopTaskTransfer.setOperTime(new Date());
    shopTaskTransfer.setState(ShopTaskTransferState.ACCEPTED);
    shopTaskTransfer.setTenant(tenant);
    shopTaskTransferDao.updateState(tenant, shopTaskTransfer);

    // ?????????log??????????????????
    changeShopTaskLogOperator(tenant, shopTaskTransfer, operateInfo);

    // ??????????????????????????????????????????????????????????????????
    sendAcceptMessage(tenant, shopTaskTransfer, operateInfo);

    // ????????????????????????????????????
    ShopTask shopTask = dao.get(tenant, shopTaskTransferHistory.getShopTaskId());
    taskReadHistoryService.deleteByUk(tenant, shopTask.getPlan(), shopTask.getPlanPeriod(), operateInfo.getOperator().getId(), TaskPlanType.INSPECTION.name());

    // ??????????????????????????????shop_task_log??????????????????????????????????????????????????????
    saveMyFollow(tenant, shopTaskTransfer);
  }

  private void saveMyFollow(String tenant, ShopTaskTransfer shopTaskTransfer) {
    ShopTaskWatcher shopTaskWatcher = buildShopTaskWatcher(tenant, shopTaskTransfer);
    // ??????????????????????????????????????????
    String watcher = shopTaskTransfer.getTransferFrom();
    String shopTaskLogId = shopTaskTransfer.getShopTaskLogId();
    ShopTaskWatcher shopTaskWatcherHistory = shopTaskWatcherService.getByWatcherAndShopTaskLogId(tenant, watcher, shopTaskLogId);
    if (null == shopTaskWatcherHistory) {
      shopTaskWatcherService.saveNew(tenant, shopTaskWatcher);
    } else {
      log.warn("???????????????????????????????????????????????????shopTaskId:{}", shopTaskLogId);
    }
  }

  private ShopTaskWatcher buildShopTaskWatcher(String tenant, ShopTaskTransfer shopTaskTransfer) {
    ShopTaskWatcher shopTaskWatcher = new ShopTaskWatcher();
    // shopTaskWatcher??????shopTaskId????????????shopTaskLogId
    shopTaskWatcher.setShopTaskId(shopTaskTransfer.getShopTaskLogId());
    shopTaskWatcher.setWatcher(shopTaskTransfer.getTransferFrom());
    shopTaskWatcher.setWatcherName(shopTaskTransfer.getTransferFromName());
    shopTaskWatcher.setCreated(new Date());
    shopTaskWatcher.setTenant(tenant);
    shopTaskWatcher.setUuid(IdGenUtils.buildRdUuid());
    return shopTaskWatcher;
  }

  private void sendAcceptMessage(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException {

    ShopTaskTransferMessageConfig config = baasConfigClient.getConfig(tenant, ShopTaskTransferMessageConfig.class);
    String path = config.getShopTaskTransferPath() + "?type=" + BShopTaskTransferDetailReqType.transferFrom.name()
        + "&shopTaskLogId=" + shopTaskTransfer.getShopTaskLogId() + "&shopTaskId=" + shopTaskTransfer.getShopTaskId();
    String title = "????????????";
    ShopTask shopTask = dao.get(tenant, shopTaskTransfer.getShopTaskId());
    String contentText = shopTask.getName();
    Message message = buildAcceptTransferFromMessage(tenant, shopTaskTransfer, path, title, contentText, MessageTag.????????????);
    this.sendMessage(tenant, operateInfo, message);
  }

  @Override
  @Tx
  public void batchAccept(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    ShopTaskTransfer shopTaskTransferHistory = getByUuid(tenant, uuid);
    if (shopTaskTransferHistory == null || ShopTaskTransferState.TRANSFER != shopTaskTransferHistory.getState()) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????"));
    }
    //??????????????????
    checkShopTask(tenant, shopTaskTransferHistory.getShopTaskId());
    //????????????????????????????????????????????????
    if (StringUtils.isNotEmpty(shopTaskTransferHistory.getTransferTo()) && !shopTaskTransferHistory.getTransferTo().equals(operateInfo.getOperator().getId())) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "????????????????????????????????????????????????????????????"));
    }
    shopTaskTransferHistory.setOperTime(new Date());
    shopTaskTransferHistory.setState(ShopTaskTransferState.ACCEPTED);
    shopTaskTransferHistory.setTenant(tenant);
    shopTaskTransferDao.updateState(tenant, shopTaskTransferHistory);

    // ????????????log??????????????????,???log?????????????????????batch_id????????????log????????????,??????????????????????????????
    // ?????????????????????????????????
    List<ShopTaskTransfer> logTransferList = shopTaskTransferDao.listByBatchId(tenant, shopTaskTransferHistory.getShopTaskId(), shopTaskTransferHistory.getUuid());
    if (CollectionUtils.isEmpty(logTransferList)) {
      log.error("????????????????????????????????????????????????????????????");
      throw new BaasException("????????????????????????????????????????????????????????????");
    }
    //???????????????????????????ID
    List<String> logIds = logTransferList.stream().filter(log -> log.getState() == ShopTaskTransferState.TRANSFER).map(ShopTaskTransfer::getShopTaskLogId).distinct().collect(Collectors.toList());
    if (CollectionUtils.isEmpty(logIds)) {
      log.error("????????????????????????????????????????????????????????????????????????");
      throw new BaasException("????????????????????????????????????????????????????????????????????????");
    }
    shopTaskLogDao.changeBatchShopTaskLogOperator(tenant, shopTaskTransferHistory, logIds, operateInfo);
    // ???????????????????????????????????????????????????????????????????????????????????????
    shopTaskTransferDao.changeBatchTransferState(tenant, uuid, ShopTaskTransferState.ACCEPTED);
    // ??????????????????????????????????????????????????????????????????
    sendBatchAcceptMessage(tenant, shopTaskTransferHistory, operateInfo);
    // ????????????????????????????????????
    ShopTask shopTask = dao.get(tenant, shopTaskTransferHistory.getShopTaskId());
    taskReadHistoryService.deleteByUk(tenant, shopTask.getPlan(), shopTask.getPlanPeriod(), operateInfo.getOperator().getId(), TaskPlanType.INSPECTION.name());

    // ??????????????????????????????shop_task_log??????????????????????????????????????????????????????
    saveMyFollows(tenant, shopTaskTransferHistory, logIds);
  }

  private void saveMyFollows(String tenant, ShopTaskTransfer shopTaskTransferHistory, List<String> logIds) {
    List<ShopTaskWatcher> shopTaskWatcherList = buildShopTaskWatcherList(tenant, shopTaskTransferHistory, logIds);
    // ???????????????
    String watcher = shopTaskTransferHistory.getTransferFrom();
    List<ShopTaskWatcher> shopTaskWatcherListHistory = shopTaskWatcherService.listByWatcherAndShopTaskLogIdList(tenant, watcher, logIds);
    if (CollectionUtils.isEmpty(shopTaskWatcherListHistory)) {
      shopTaskWatcherService.batchSave(tenant, shopTaskWatcherList);
    } else {
      // ????????????????????????????????????????????????????????????????????????????????????
      List<ShopTaskWatcher> needSaveResult = shopTaskWatcherList.stream().filter(shopTaskWatcher ->
              shopTaskWatcherListHistory.stream().noneMatch(shopTaskWatcherHistory ->
                  StringUtils.isNotEmpty(shopTaskWatcher.getTenant()) && shopTaskWatcher.getTenant().equals(shopTaskWatcherHistory.getTenant()) &&
                      StringUtils.isNotEmpty(shopTaskWatcher.getWatcher()) && shopTaskWatcher.getWatcher().equals(shopTaskWatcherHistory.getWatcher()) &&
                      StringUtils.isNotEmpty(shopTaskWatcher.getShopTaskId()) && shopTaskWatcher.getShopTaskId().equals(shopTaskWatcherHistory.getShopTaskId())))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(needSaveResult)) {
        shopTaskWatcherService.batchSave(tenant, needSaveResult);
      } else {
        log.warn("shopTaskLogIds:{},??????????????????????????????", logIds);
      }
    }
  }

  private List<ShopTaskWatcher> buildShopTaskWatcherList(String tenant, ShopTaskTransfer shopTaskTransferHistory, List<String> logIds) {
    List<ShopTaskWatcher> shopTaskWatcherList = new ArrayList<>();
    for (String logId : logIds) {
      ShopTaskWatcher shopTaskWatcher = new ShopTaskWatcher();
      // shopTaskWatcher??????shopTaskId????????????shopTaskLogId
      shopTaskWatcher.setShopTaskId(logId);
      shopTaskWatcher.setWatcher(shopTaskTransferHistory.getTransferFrom());
      shopTaskWatcher.setWatcherName(shopTaskTransferHistory.getTransferFromName());
      shopTaskWatcher.setCreated(new Date());
      shopTaskWatcher.setTenant(tenant);
      shopTaskWatcher.setUuid(IdGenUtils.buildRdUuid());
      shopTaskWatcherList.add(shopTaskWatcher);
    }
    return shopTaskWatcherList;
  }

  @Override
  @Tx
  public void refuse(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getUuid(), "uuid");

    ShopTaskTransfer shopTaskTransferHistory = getByUuid(tenant, shopTaskTransfer.getUuid());
    if (shopTaskTransferHistory == null || ShopTaskTransferState.TRANSFER != shopTaskTransferHistory.getState()) {
      throw new BaasException(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????");
    }

    shopTaskTransfer.setOperTime(new Date());
    shopTaskTransfer.setState(ShopTaskTransferState.REFUSED);
    shopTaskTransfer.setTenant(tenant);

    shopTaskTransferDao.updateState(tenant, shopTaskTransfer);
    // ??????????????????????????????????????????????????????????????????
    sendRefuseMessage(tenant, shopTaskTransfer, operateInfo);
  }

  private void sendRefuseMessage(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException {
    ShopTaskTransferMessageConfig config = baasConfigClient.getConfig(tenant, ShopTaskTransferMessageConfig.class);
    String path = config.getShopTaskTransferPath() + "?type=" + BShopTaskTransferDetailReqType.transferFrom.name()
        + "&shopTaskLogId=" + shopTaskTransfer.getShopTaskLogId() + "&shopTaskId=" + shopTaskTransfer.getShopTaskId();
    String title = "????????????";
    ShopTask shopTask = dao.get(tenant, shopTaskTransfer.getShopTaskId());
    String contentText = shopTask.getName();
    Message message = buildRefuseTransferFromMessage(tenant, shopTaskTransfer, path, title, contentText, MessageTag.????????????);
    this.sendMessage(tenant, operateInfo, message);
  }

  @Override
  @Tx
  public void batchRefuse(String tenant, String uuid, String reason, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    ShopTaskTransfer shopTaskTransferHistory = getByUuid(tenant, uuid);
    if (shopTaskTransferHistory == null || ShopTaskTransferState.TRANSFER != shopTaskTransferHistory.getState()) {
      throw new BaasException(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????");
    }

    shopTaskTransferHistory.setOperTime(new Date());
    shopTaskTransferHistory.setState(ShopTaskTransferState.REFUSED);
    shopTaskTransferHistory.setTenant(tenant);
    shopTaskTransferHistory.setReason(reason);

    shopTaskTransferDao.updateState(tenant, shopTaskTransferHistory);

    // ???????????????????????????????????????????????????????????????????????????????????????
    shopTaskTransferDao.refuseLogBatchTransfer(tenant, shopTaskTransferHistory);
    // ??????????????????????????????????????????????????????????????????
    sendBatchRefuseMessage(tenant, shopTaskTransferHistory, operateInfo);
  }

  private void sendBatchRefuseMessage(String tenant, ShopTaskTransfer shopTaskTransferHistory, OperateInfo operateInfo) throws BaasException {
    ShopTaskTransferMessageConfig config = baasConfigClient.getConfig(tenant, ShopTaskTransferMessageConfig.class);
    String path = config.getShopTaskTransferPath() + "?type=" + BShopTaskTransferDetailReqType.transferFrom.name()
        + "&shopTaskId=" + shopTaskTransferHistory.getShopTaskId() + "&transferType=" + shopTaskTransferHistory.getType();
    String title = "????????????";
    ShopTask shopTask = dao.get(tenant, shopTaskTransferHistory.getShopTaskId());
    String contentText = shopTask.getGroupName();
    Message message = buildRefuseTransferFromMessage(tenant, shopTaskTransferHistory, path, title, contentText, MessageTag.????????????);
    this.sendMessage(tenant, operateInfo, message);
  }

  @Override
  public ShopTaskTransferDetail getShopTaskTransferDetail(String tenant,
                                                           ShopTaskTransferDetailReq shopTaskTransferDetailReq) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransferDetailReq.getShopTaskLogId(), "shopTaskId");
    Assert.hasText(shopTaskTransferDetailReq.getType(), "type");

    ShopTaskLog shopTaskLog = shopTaskLogDao.get(tenant, shopTaskTransferDetailReq.getShopTaskLogId());
    ShopTaskTransferDetail shopTaskTransferDetail = new ShopTaskTransferDetail();
    ShopTask shopTask = dao.get(tenant, shopTaskLog.getOwner());
    if (shopTask == null) {
      throw new BaasException("??????ID???{}???????????????????????????", shopTaskLog.getOwner());
    }
    List<ShopTaskTransfer> shopTaskTransferList = new ArrayList<>();
    // ????????????????????????????????????,???????????????????????????
    if (BShopTaskTransferDetailReqType.transferFrom.name().equals(shopTaskTransferDetailReq.getType())) {
      shopTaskTransferList = shopTaskTransferDao.listByShopTaskLogIdAndTransferFrom(tenant,
          shopTaskTransferDetailReq.getShopTaskLogId(), shopTaskTransferDetailReq.getTransferFrom());
    } else if (BShopTaskTransferDetailReqType.transferTo.name().equals(shopTaskTransferDetailReq.getType())) {
      ShopTaskTransfer shopTaskTransfer = shopTaskTransferDao.listByShopTaskLogIdAndTransferTo(tenant,
          shopTaskTransferDetailReq.getShopTaskLogId(), shopTaskTransferDetailReq.getTransferTo());
      shopTaskTransferList = Collections.singletonList(shopTaskTransfer);
    } else {
      throw new BaasException("????????????????????????");
    }
    List<ShopTaskLog> shopTaskLogs = new ArrayList<>();
    shopTaskLogs.add(shopTaskLog);
    shopTask.setLogs(shopTaskLogs);
    shopTaskTransferDetail.setShopTask(shopTask);
    shopTaskTransferDetail.setShopTaskTransferList(shopTaskTransferList);
    return shopTaskTransferDetail;
  }

  @Override
  public ShopTaskTransferDetail getShopTaskBatchTransferDetail(String tenant, String shopTaskId, String transferFrom, String transferTo, String type) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");
    Assert.hasText(type, "type");

    ShopTaskTransferDetail shopTaskTransferDetail = new ShopTaskTransferDetail();
    ShopTask shopTask = dao.get(tenant, shopTaskId);
    if (shopTask == null) {
      throw new BaasException("??????ID???{}???????????????????????????", shopTaskId);
    }
    List<ShopTaskTransfer> shopTaskTransferList = new ArrayList<>();
    // ????????????????????????????????????,???????????????????????????
    if (BShopTaskTransferDetailReqType.transferFrom.name().equals(type)) {
      shopTaskTransferList = shopTaskTransferDao.listBatchByShopTaskIdAndTransferFrom(tenant,
          shopTaskId, transferFrom);
    } else if (BShopTaskTransferDetailReqType.transferTo.name().equals(type)) {
      ShopTaskTransfer shopTaskTransfer = shopTaskTransferDao.listBatchByShopTaskIdAndTransferTo(tenant,
          shopTaskId, transferTo);
      shopTaskTransferList = Collections.singletonList(shopTaskTransfer);
    } else {
      throw new BaasException("????????????????????????");
    }
    String transferFromHistory = "";
    if (CollectionUtils.isNotEmpty(shopTaskTransferList)) {
      transferFromHistory = shopTaskTransferList.get(0).getTransferFrom();
    }
    //???????????????????????????????????????????????????
    List<ShopTaskTransfer> shopTaskTransferListResult = shopTaskTransferDao.listLogBatchTransferByShopTaskIdList(tenant, Collections.singletonList(shopTask.getUuid()));
    if (CollectionUtils.isNotEmpty(shopTaskTransferListResult)) {
      String finalTransferFromHistory = transferFromHistory;
      shopTaskTransferListResult = shopTaskTransferListResult.stream()
          .filter(transfer -> finalTransferFromHistory.equals(transfer.getTransferFrom())
              && StringUtils.isNotEmpty(transfer.getType())
              && StringUtils.isNotEmpty(transfer.getBatchId())
              && ShopTaskTransferState.CANCELED != transfer.getState())
          .collect(Collectors.toList());
    }
    if (CollectionUtils.isNotEmpty(shopTaskTransferListResult)) {
      List<String> logUuidList = shopTaskTransferListResult.stream().map(ShopTaskTransfer::getShopTaskLogId).distinct().collect(Collectors.toList());
      List<ShopTaskLog> shopTaskLogs = shopTaskLogDao.listByUuidList(tenant, logUuidList);
      shopTask.setLogs(shopTaskLogs);
      shopTask.setPoint(CollectionUtils.isEmpty(shopTaskLogs) ? BigDecimal.ZERO : shopTaskLogs.stream()
          .map(ShopTaskLog::getPoint)
          .filter(Objects::nonNull)
          .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
    shopTaskTransferDetail.setShopTask(shopTask);
    shopTaskTransferDetail.setShopTaskTransferList(shopTaskTransferList);
    return shopTaskTransferDetail;
  }

  @Override
  public List<ShopTaskTransfer> listByShopTaskLogsIdList(String tenant, List<String> shopTaskLogIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(shopTaskLogIds, "shopTaskLogIds");

    return shopTaskTransferDao.listByShopTaskLogIdsList(tenant, shopTaskLogIds);
  }

  @Override
  @Tx
  public void cancel(String tenant, String uuid) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    ShopTaskTransfer shopTaskTransferHistory = getByUuid(tenant, uuid);
    if (shopTaskTransferHistory == null || ShopTaskTransferState.TRANSFER != shopTaskTransferHistory.getState()) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????"));
    }

    if (ShopTaskTransferType.BATCH.name().equals(shopTaskTransferHistory.getType())) {
      // ??????????????????????????????????????????????????????????????????
      shopTaskTransferDao.changeBatchTransferState(tenant, uuid, ShopTaskTransferState.CANCELED);
    }
    shopTaskTransferDao.cancel(tenant, uuid);
  }

  @Override
  @Tx
  public void cancelByShopTaskId(String tenant, List<String> shopTaskIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTaskIds, "shopTaskIds");

    shopTaskTransferDao.cancelByShopTaskId(tenant, shopTaskIds);
  }

  @Override
  public List<ShopTask> listByUK(String tenant, String plan, String planPeriod, String shop, String operatorId) {
    return dao.listByUK(tenant, plan, planPeriod, shop, operatorId);
  }

  @Override
  @Tx
  public void cancelByLogId(String tenant, String logId) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(logId, "logId");

    shopTaskTransferDao.cancelByLogId(tenant, logId);
  }

  @Override
  @Tx
  public void batchSaveNewAssign(String tenant, String plan, List<ShopTask> tasks, OperateInfo sysOperateInfo) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(plan, "plan");
    Assert.notEmpty(tasks, "taskShop");

    for (ShopTask task : tasks) {
      task.setLastModifyInfo(sysOperateInfo);
      task.setPlanPeriodCode(task.getPlanCode());
      if (StringUtils.isEmpty(task.getUuid())) {
        task.setUuid(UUID.randomUUID().toString());
      }
    }
    dao.batchInsert(tenant, tasks);
  }

  @Override
  public ShopTaskTransfer getByUuid(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    return shopTaskTransferDao.get(tenant, uuid);
  }

  @Override
  public List<ShopTaskLog> listByOwners(String tenant, List<String> owners) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(owners, "owners");

    return shopTaskLogDao.listByOwners(tenant, owners);
  }

  @Override
  @Tx
  public String transferAssignableShopTask(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getShopTaskId(), "shopTaskId");
    Assert.hasText(shopTaskTransfer.getShop(), "shopId");
    Assert.hasText(shopTaskTransfer.getShopCode(), "shopCode");
    Assert.hasText(shopTaskTransfer.getTransferFrom(), "transferFrom");
    Assert.hasText(shopTaskTransfer.getTransferTo(), "transferTo");
    // ????????????????????????????????????
    List<ShopTaskTransfer> shopTaskTransferList = shopTaskTransferDao.listByShopTaskIdAndTransferFrom(tenant,
        shopTaskTransfer.getShopTaskId(), shopTaskTransfer.getTransferFrom());
    if (CollectionUtils.isNotEmpty(shopTaskTransferList)
        && shopTaskTransferList.stream().anyMatch(item -> ShopTaskTransferState.TRANSFER == item.getState())) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "?????????????????????????????????"));
    }
    ShopTask shopTask = dao.get(tenant, shopTaskTransfer.getShopTaskId());
    if (shopTask == null || (StringUtils.isNotEmpty(operateInfo.getOperator().getId()))
        && !operateInfo.getOperator().getId().equals(shopTask.getOperatorId())) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????????????????????????????"));
    }
    // ???????????????????????????????????????
    checkShopTask(tenant, shopTaskTransfer.getShopTaskId());

    if (StringUtils.isEmpty(shopTaskTransfer.getUuid())) {
      String uuid = IdGenUtils.buildRdUuid();
      shopTaskTransfer.setUuid(uuid);
    }
    shopTaskTransfer.setTransferTime(new Date());
    shopTaskTransfer.setState(ShopTaskTransferState.TRANSFER);
    shopTaskTransfer.setTenant(tenant);
    shopTaskTransferDao.transfer(tenant, shopTaskTransfer);

    // ????????????????????????????????????????????????????????????
    sendTransferAssignableShopTaskMessage(tenant, shopTask.getName(), shopTaskTransfer, operateInfo);

    return shopTaskTransfer.getUuid();
  }

  @Override
  @Tx
  public void refuseAssignableShopTask(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getUuid(), "uuid");
    ShopTaskTransfer shopTaskTransferHistory = getByUuid(tenant, shopTaskTransfer.getUuid());
    if (shopTaskTransferHistory == null || ShopTaskTransferState.TRANSFER != shopTaskTransferHistory.getState()) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????"));
    }

    shopTaskTransfer.setOperTime(new Date());
    shopTaskTransfer.setState(ShopTaskTransferState.REFUSED);
    shopTaskTransfer.setTenant(tenant);

    shopTaskTransferDao.updateState(tenant, shopTaskTransfer);
    // ??????????????????????????????????????????????????????????????????
    sendRefuseAssignableShopTask(tenant, shopTaskTransfer, operateInfo);
  }

  @Override
  @Tx
  public void cancelAssignableShopTask(String tenant, String uuid) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    ShopTaskTransfer shopTaskTransferHistory = getByUuid(tenant, uuid);
    if (shopTaskTransferHistory == null || ShopTaskTransferState.TRANSFER != shopTaskTransferHistory.getState()) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????"));
    }
    shopTaskTransferDao.cancel(tenant, uuid);
  }

  @Override
  @Tx
  public void acceptAssignableShopTask(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getUuid(), "uuid");
    Assert.hasText(shopTaskTransfer.getShopTaskId(), "shopTaskId");
    ShopTask shopTask = get(tenant, shopTaskTransfer.getShopTaskId());
    ShopTaskTransfer shopTaskTransferHistory = getByUuid(tenant, shopTaskTransfer.getUuid());
    if (shopTaskTransferHistory == null || ShopTaskTransferState.TRANSFER != shopTaskTransferHistory.getState()) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????"));
    }
    //??????????????????
    checkShopTask(tenant, shopTaskTransfer.getShopTaskId());

    shopTaskTransfer.setOperTime(new Date());
    shopTaskTransfer.setState(ShopTaskTransferState.ACCEPTED);
    shopTaskTransfer.setTenant(tenant);
    shopTaskTransferDao.updateState(tenant, shopTaskTransfer);

    // ??????????????????
    shopTask.setOperatorPositionCode(shopTaskTransfer.getTransferToPositionCode());
    shopTask.setOperatorPositionName(shopTaskTransfer.getTransferToPositionName());
    // ??????shopTask???????????????????????????
    changeShopTaskOperator(tenant, shopTask, operateInfo);
    // ???????????????transferFrom???????????????
    shopTaskLogDao.deleteByOperateIdAndReply(tenant, shopTaskTransfer.getShopTaskId(),
        shopTaskTransferHistory.getTransferFrom());

    // ??????????????????????????????????????????????????????????????????
    sendAcceptAssignableShopTaskMessage(tenant, shopTask.getName(), shopTaskTransfer, operateInfo);
  }

  @Override
  @Tx
  public void terminate(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    ShopTask shopTask = dao.get(tenant, uuid);
    if (shopTask == null || shopTask.getState() != ShopTaskState.UNFINISHED) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????????????????"));
    }
    dao.terminate(tenant, uuid, operateInfo);
  }

  @Override
  public List<ShopTask> listShop(String tenant, String loginId) {
    Assert.hasText(tenant, "tenant");

    return dao.listShop(tenant, loginId);
  }

  @Override
  @Tx
  public void changeShopTaskState(String tenant, String uuid, BigDecimal score, ShopTaskState state,
      OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(score);
    Assert.notNull(operateInfo);
    Assert.notNull(state);

    dao.changeShopTaskState(tenant, uuid, score, state, operateInfo);
  }

  @Override
  @Tx
  public void changeShopTaskState(String tenant, String uuid, ShopTaskState state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(operateInfo);
    Assert.notNull(state);

    dao.changeShopTaskState(tenant, uuid, state, operateInfo);
    shopTaskLogDao.changeShopTaskLogStateByOwner(tenant, uuid, state, operateInfo);
  }

  // TODO ?????????
  @Override
  @Tx
  public void reply(String tenant, AssignableShopTaskLog log, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(log.getOwner(), "??????ID");

    ShopTask shopTask = get(tenant, log.getOwner());
    if (shopTask == null) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "?????????????????????"));
    }
    if (!shopTask.getState().name().equals(ShopTaskState.UNFINISHED.name())) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "?????????????????????????????????:" + shopTask.getState()));
    }
    if (!operateInfo.getOperator().getId().equals(shopTask.getOperatorId())) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "?????????????????????????????????????????????????????????"));
    }
    // ????????????????????????????????????????????????????????????????????????shopTask??????????????????log?????????????????????????????????
    if (StringUtils.isNotEmpty(log.getUuid())) {
      ShopTaskLog shopTaskLog = getLog(tenant, log.getUuid());
      shopTaskLog.setFeedback(JsonUtil.objectToJson(log.getFeedbacks()));
      shopTaskLog.setState(ShopTaskState.UNFINISHED.name());
      shopTaskLog.setFinishAppId(log.getAppId());
      shopTaskLog.setOwner(log.getOwner());
      shopTaskLog.setOperatorId(operateInfo.getOperator().getId());
      shopTaskLog.setOperatorName(operateInfo.getOperator().getFullName());
      shopTaskLog.setType(ShopTaskLogType.REPLY.name());
      if ("save".equals(log.getOperateType())) {
        saveAssignableTaskLog(tenant, shopTaskLog, operateInfo);
      } else if ("submit".equals(log.getOperateType())) {
        finishAssignableShopTaskLog(tenant, shopTaskLog, operateInfo);
        // ?????????????????????????????????
        shopTaskTransferDao.cancelByShopTaskId(tenant, Collections.singletonList(log.getOwner()));
        // ???????????????????????????????????????????????????????????????
        if (!shopTask.getAudit()) {
          changeShopTaskState(tenant, shopTask.getUuid(), shopTask.getPoint(), ShopTaskState.FINISHED, operateInfo);
          shopTask.setScore(shopTask.getPoint());
          saveTaskPoints(tenant, shopTask, operateInfo);
        } else {
          changeShopTaskState(tenant, shopTask.getUuid(), BigDecimal.ZERO, ShopTaskState.SUBMITTED, operateInfo);
        }
      } else {
        ShopTaskServiceImpl.log.error("????????????????????????");
      }
    } else {
      ShopTaskLog shopTaskLog = new ShopTaskLog();
      shopTaskLog.setFeedback(JsonUtil.objectToJson(log.getFeedbacks()));
      shopTaskLog.setState(ShopTaskState.UNFINISHED.name());
      shopTaskLog.setFinishAppId(log.getAppId());
      shopTaskLog.setOperatorId(operateInfo.getOperator().getId());
      shopTaskLog.setOperatorName(operateInfo.getOperator().getFullName());
      shopTaskLog.setTenant(tenant);
      shopTaskLog.setOwner(log.getOwner());
      shopTaskLog.setUuid(IdGenUtils.buildRdUuid());
      shopTaskLog.setType(ShopTaskLogType.REPLY.name());
      if ("save".equals(log.getOperateType())) {
        shopTaskLogDao.insert(tenant, shopTaskLog, operateInfo);
      } else if ("submit".equals(log.getOperateType())) {
        shopTaskLog.setState(ShopTaskState.FINISHED.name());
        shopTaskLogDao.insert(tenant, shopTaskLog, operateInfo);
        // ?????????????????????????????????
        shopTaskTransferDao.cancelByShopTaskId(tenant, Collections.singletonList(log.getOwner()));
        // ???????????????????????????????????????????????????????????????
        if (!shopTask.getAudit()) {
          changeShopTaskState(tenant, shopTask.getUuid(), shopTask.getPoint(), ShopTaskState.FINISHED, operateInfo);
          shopTask.setScore(shopTask.getPoint());
          saveTaskPoints(tenant, shopTask, operateInfo);
        } else {
          changeShopTaskState(tenant, shopTask.getUuid(), shopTask.getPoint(), ShopTaskState.SUBMITTED, operateInfo);
        }
      } else {
        ShopTaskServiceImpl.log.error("????????????????????????");
      }
    }
  }

  @Override
  public List<ShopTask> listByShopIds(String tenant, ShopTaskState state, List<String> shopIdLists) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopIdLists);

    return dao.listByShopIds(tenant, state, shopIdLists);
  }

  @Override
  public List<ShopTaskTransfer> listTransferByShopTaskIdList(String tenant, List<String> shopTaskIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTaskIds);

    return shopTaskTransferDao.listTransferByShopTaskIdList(tenant, shopTaskIds);
  }

  @Override
  public List<ShopTaskTransfer> listBatchTransferByShopTaskIdList(String tenant, List<String> shopTaskIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTaskIds);

    return shopTaskTransferDao.listBatchTransferByShopTaskIdList(tenant, shopTaskIds);
  }

  @Override
  public List<AssignableShopTaskCount> getCountByState(String tenant, List<String> operators, ShopTaskState state) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(operators);
    Assert.notNull(state);
    return dao.getCountByState(tenant, operators, state);
  }

  @Override
  @Tx
  public void audit(String tenant, AssignableShopTaskLog log, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(log.getOwner(), "??????ID");
    // ?????????????????????
    ShopTask shopTask = get(tenant, log.getOwner());
    if (shopTask == null) {
      throw new BaasException("?????????????????????????????????");
    }
    if (!shopTask.getAudit()) {
      throw new BaasException("????????????????????????????????????");
    }
    if (shopTask.getState() != ShopTaskState.SUBMITTED) {
      throw new BaasException("??????????????????????????????????????????");
    }
    if (StringUtils.isNotEmpty(shopTask.getCreateInfo().getOperator().getId())
        && !shopTask.getCreateInfo().getOperator().getId().equals(operateInfo.getOperator().getId())) {
      throw new BaasException("??????????????????????????????????????????????????????");
    }
    ShopTaskLog shopTaskLog = new ShopTaskLog();
    shopTaskLog.setUuid(IdGenUtils.buildRdUuid());
    shopTaskLog.setTenant(tenant);
    shopTaskLog.setScore(log.getScore());
    shopTaskLog.setOwner(log.getOwner());
    shopTaskLog.setOperatorId(operateInfo.getOperator().getId());
    shopTaskLog.setOperatorName(operateInfo.getOperator().getFullName());
    shopTaskLog.setFeedback(JsonUtil.objectToJson(log.getFeedbacks()));
    shopTaskLog.setState(ShopTaskState.FINISHED.name());
    shopTaskLog.setType(ShopTaskLogType.AUDIT.name());
    shopTaskLog.setFinishAppId(log.getAppId());
    shopTaskLogDao.insert(tenant, shopTaskLog, operateInfo);
    // ?????????????????????????????????
    changeShopTaskState(tenant, shopTask.getUuid(), shopTaskLog.getScore(), ShopTaskState.FINISHED, operateInfo);
    // ????????????
    shopTask.setScore(shopTaskLog.getScore());
    saveTaskPoints(tenant, shopTask, operateInfo);
  }

  @Override
  @Tx
  public void grabOrder(String tenant, ShopTask shopTaskReq, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskReq.getUuid(), "uuid");
    Assert.hasText(shopTaskReq.getShop(), "shop");
    Assert.hasText(shopTaskReq.getShopCode(), "shopCode");
    Assert.hasText(shopTaskReq.getShopName(), "shopName");

    ShopTask shopTask = getWithLock(tenant, shopTaskReq.getUuid());
    if (shopTask == null) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "?????????????????????????????????"));
    }
    if (StringUtils.isNotEmpty(shopTask.getAssignType())
        && !shopTask.getAssignType().equals(AssignTaskPlanType.GRABBING_ORDERS.name())) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "?????????????????????????????????????????????"));
    }
    if (shopTask.getState() == null || shopTask.getState() != ShopTaskState.UNFINISHED) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????????????????????????????"));
    }
    if (StringUtils.isNotEmpty(shopTask.getOperatorId())) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????"));
    }
    // ????????????????????????????????????????????????
    List<TaskPlanLine> taskPlanLines = taskPlanLineDao.list(tenant, shopTask.getPlan());
    if (CollectionUtils.isEmpty(taskPlanLines) || taskPlanLines.stream()
        .noneMatch(item -> StringUtils.isNotEmpty(operateInfo.getOperator().getId())
            && operateInfo.getOperator().getId().equals(item.getAssigneeId()))) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????????????????"));
    }
    // ?????????????????????????????????????????????????????????????????????
    dao.grabOrder(tenant, shopTaskReq, operateInfo);
  }

  @Override
  @Tx
  public void expireShopTask(String tenant) {
    Assert.hasText(tenant, "tenant");
    dao.expireShopTask(tenant);
  }

  @Override
  public List<AssignableShopTaskSummary> querySummary(String tenant, Date startDate, Date endDate, String shopKeyWord,
      Integer page, Integer pageSize) {
    Assert.hasText(tenant, "tenant");
    return dao.querySummary(tenant, startDate, endDate, shopKeyWord, page, pageSize);
  }

  @Override
  public long querySummaryCount(String tenant, Date startDate, Date endDate, String shopKeyWord) {
    Assert.hasText(tenant, "tenant");
    return dao.querySummaryCount(tenant, startDate, endDate, shopKeyWord);
  }

  @Override
  public long queryCountGreaterRate(String tenant, Date startDate, Date endDate, BigDecimal rate) {
    Assert.hasText(tenant, "tenant");

    return dao.queryCountGreaterRate(tenant, startDate, endDate, rate);
  }

  @Override
  public List<ShopTaskLine> query(String tenant, String shopKeyword, Date startDate, Date endDate, String planKeyword) {
    if (StringUtils.isEmpty(tenant)) {
      return null;
    }
    return dao.query(tenant, shopKeyword, startDate, endDate, planKeyword);
  }

  @Override
  @Tx
  public void checkBatchTransfer(String tenant, String shopTaskId, String logId, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");

    //?????????????????????????????????????????????[?????????]????????????????????????????????????????????????
    List<ShopTaskLog> shopTaskLogs = shopTaskLogDao.listUnfinishedByOperateIdAndShopTaskId(tenant, shopTaskId, operateInfo);
    if (CollectionUtils.isEmpty(shopTaskLogs)) {
      // ?????????????????????????????????????????????????????????
      shopTaskTransferDao.cancelBatchTransfer(tenant, shopTaskId, operateInfo);
    } else {
      log.info("????????????????????????????????????????????????????????????????????????logIds:{}", shopTaskLogs.stream().map(ShopTaskLog::getUuid).collect(Collectors.toList()));
      // ?????????????????????????????????????????????
      shopTaskTransferDao.cancelBatchTransferLog(tenant, shopTaskId, logId, operateInfo);
    }
  }

  @Override
  public List<ShopTaskLog> listByShopTaskIdsAndOperatorId(String tenant, List<String> shopTaskIds, String operatorId) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(shopTaskIds, "shopTaskIds");
    Assert.hasText(operatorId, "operatorId");

    return shopTaskLogDao.listByShopTaskIdsAndOperatorId(tenant, shopTaskIds, operatorId);
  }

  @Override
  public List<ShopTask> listShopTaskTransferByUK(String tenant, String plan, String planPeriod, String shop, String operatorId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(plan, "plan");
    Assert.hasText(planPeriod, "planPeriod");
    Assert.hasText(shop, "shop");

    List<ShopTask> shopTaskList = new ArrayList<>();
    List<ShopTaskLog> shopTaskLogList = this.listShopTaskLogTransferByUK(tenant, plan, planPeriod, shop, operatorId);
    if (CollectionUtils.isEmpty(shopTaskLogList)) {
      return shopTaskList;
    }
    List<String> shopTaskIds = shopTaskLogList.stream().map(ShopTaskLog::getOwner).distinct().collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(shopTaskIds)) {
      shopTaskList = dao.listByUUIDs(tenant, shopTaskIds);
      if (CollectionUtils.isNotEmpty(shopTaskList)) {
        shopTaskList.forEach(shopTask -> shopTask.setLogs(shopTaskLogList.stream().filter(log -> StringUtils.isNotEmpty(log.getOwner()) && log.getOwner().equals(shopTask.getUuid())).collect(Collectors.toList())));
      }
    }
    return shopTaskList;
  }

  @Override
  public List<ShopTaskLog> listShopTaskLogTransferByUK(String tenant, String plan, String planPeriod, String shop, String operatorId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(plan, "plan");
    Assert.hasText(planPeriod, "planPeriod");
    Assert.hasText(shop, "shop");

    return shopTaskLogDao.listTransferredByUK(tenant, plan, planPeriod, shop, operatorId);
  }

  @Override
  public List<ShopTaskTransfer> listByShopTaskLogIdOrShopTaskId(String tenant, String id, String operatorId, String transferType) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(id, "shopTaskId???shopTaskLogId????????????");
    Assert.isTrue("shopTask".equals(transferType) || "shopTaskLog".equals(transferType), "transferType????????????shopTask???shopTaskLog");

    List<ShopTaskTransfer> shopTaskTransferList = new ArrayList<>();
    if ("shopTaskLog".equals(transferType)) {
      shopTaskTransferList = shopTaskTransferDao.listByShopTaskLogId(tenant, id, operatorId);
    } else {
      shopTaskTransferList = shopTaskTransferDao.listByShopTaskId(tenant, id, operatorId);
    }
    return shopTaskTransferList;
  }

  @Override
  public List<ShopTask> listByRemindTime(String tenant, Date remindTime) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(remindTime, "remindTime");
    remindTime = DateUtil.truncate(remindTime, Calendar.MINUTE);
    return dao.list(tenant, remindTime);
  }

  @Override
  public List<ShopTask> listByStartTime(String tenant, Date remindTime) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(remindTime, "remindTime");
    remindTime = DateUtil.truncate(remindTime, Calendar.MINUTE);
    return dao.listByStartTime(tenant, remindTime);
  }

  @Override
  public List<ShopTask> listAssignableByRemindTime(String tenant, Date remindTime) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(remindTime, "remindTime");
    remindTime = DateUtil.truncate(remindTime, Calendar.MINUTE);
    return dao.listAssignable(tenant, remindTime);
  }

  private void checkTaskLog(String tenant, String logId) throws BaasException {
    ShopTaskLog log = shopTaskLogDao.get(tenant, logId);
    if (log == null) {
      throw new BaasException("???????????????");
    }
    if (!log.getState().equals(ShopTaskState.UNFINISHED.name())) {
      throw new BaasException("????????????????????????????????????{0}", log.getState());
    }
  }

  private void checkTask(String plan, String shop, List<ShopTask> tasks) throws BaasException {
    if (CollectionUtils.isEmpty(tasks)) {
      throw new BaasException("????????????????????????");
    }
    boolean b = tasks.stream().allMatch(s -> s.getPlan().equals(plan) && s.getShop().equals(shop));
    if (!b) {
      throw new BaasException("??????????????????????????????");
    }
    boolean logNull = tasks.stream().anyMatch(s -> CollectionUtils.isEmpty(s.getLogs()));
    if (logNull) {
      throw new BaasException("????????????????????????");
    }
  }

  private void addScore(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    List<ShopTaskLog> logs = shopTaskLogDao.list(tenant, uuid);
    if (CollectionUtils.isEmpty(logs)) {
      return;
    }
    boolean allFinished = logs.stream().allMatch(s -> ShopTaskState.FINISHED.name().equals(s.getState()));
    BigDecimal score = logs.stream()
        .map(s -> null != s.getScore() ? s.getScore() : BigDecimal.ZERO)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    ShopTask shopTask = dao.get(tenant, uuid);
    shopTask.setScore(score);
    dao.addScore(tenant, uuid, score);
    if (allFinished) {
      dao.finish(tenant, uuid, operateInfo);
    }
    ShopTaskSummaryMsg msg = new ShopTaskSummaryMsg();
    msg.setTenant(tenant);
    msg.setUuid(shopTask.getOwner());
    publisher.publishForNormal(ShopTaskSummaryEvCallExecutor.SHOP_TASK_SUMMARY_EXECUTOR_ID, msg);
  }

  private void fetchParts(String tenant, ShopTask shopTask, String[] fetchParts) {
    if (null == shopTask || null == fetchParts) {
      return;
    }
    for (String fetchPart : fetchParts) {
      if (fetchPart.equals(SHOP_TASK_LOG)) {
        List<ShopTaskLog> logs = shopTaskLogDao.list(tenant, shopTask.getUuid());
        if (CollectionUtils.isNotEmpty(logs)) {
          BigDecimal point = BigDecimal.ZERO;
          BigDecimal score = BigDecimal.ZERO;
          for (ShopTaskLog log : logs) {
            point = point.add(log.getPoint());
            BigDecimal taskLogScore = null != log.getScore() ? log.getScore() : BigDecimal.ZERO;
            score = score.add(taskLogScore);
          }
          shopTask.setPoint(point);
          shopTask.setScore(score);
        }
        shopTask.setLogs(logs);
      }
    }
  }

  private void fetchPartsByLoginId(String tenant, ShopTask shopTask, String loginId, String[] fetchParts) {
    if (null == shopTask || null == fetchParts) {
      return;
    }
    for (String fetchPart : fetchParts) {
      if (fetchPart.equals(SHOP_TASK_LOG)) {
        List<ShopTaskLog> logs = shopTaskLogDao.list(tenant, shopTask.getUuid(), loginId);
        if (CollectionUtils.isNotEmpty(logs)) {
          BigDecimal point = BigDecimal.ZERO;
          BigDecimal score = BigDecimal.ZERO;
          for (ShopTaskLog log : logs) {
            point = point.add(log.getPoint());
            BigDecimal taskLogScore = null != log.getScore() ? log.getScore() : BigDecimal.ZERO;
            score = score.add(taskLogScore);
          }
          shopTask.setPoint(point);
          shopTask.setScore(score);
        }
        shopTask.setLogs(logs);
      }
    }
  }

  @Override
  public List<ShopTask> list(String tenant, String shop, TaskGroupType type, String planTime) throws ParseException {
    Assert.notNull(tenant, "??????");
    Assert.notNull(shop, "??????");
    Assert.notNull(planTime, "??????");
    List<TaskGroupShop> list = taskGroupShopDao.getByShop(tenant, shop, type);
    if (CollectionUtils.isEmpty(list)) {
      return null;
    }
    List<String> taskGroupId = list.stream().map(TaskGroupShop::getTaskGroup).collect(Collectors.toList());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = sdf.parse(planTime);
    return dao.list(tenant, shop, taskGroupId, getStartOfDay(date), getEndOfDay(date));
  }

  @Override
  public List<ShopTaskLog> logList(String tenant, String uuid) {
    return shopTaskLogDao.list(tenant, uuid);
  }

  @Override
  public List<ShopTaskLog> logListByLoginId(String tenant, String uuid, String loginId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.hasText(loginId, "loginId");

    return shopTaskLogDao.list(tenant, uuid, loginId);
  }

  public Date getEndOfDay(Date date) {
    LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
    return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
  }

  public Date getStartOfDay(Date date) {
    LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
    return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
  }

  private void checkShopTask(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    ShopTask shopTask = get(tenant, uuid);
    if (shopTask == null) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "???????????????"));
    }
    if (!shopTask.getState().name().equals(ShopTaskState.UNFINISHED.name())
        && !shopTask.getState().name().equals(ShopTaskState.SUBMITTED.name())) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "????????????????????????????????????" + shopTask.getState()));
    }
    if (StringUtils.isNotEmpty(shopTask.getOperatorId())
        && !shopTask.getOperatorId().equals(operateInfo.getOperator().getId())) {
      throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "??????????????????????????????"));
    }
  }

  private void saveTaskPoints(String tenant, ShopTask shopTask, OperateInfo operateInfo) throws BaasException {
    TaskPoints taskPoints = new TaskPoints();
    taskPoints.setPoints(shopTask.getScore());
    taskPoints.setUserId(shopTask.getOperatorId());
    taskPoints.setOccurredType(TaskPointsOccurredType.ASSIGNABLE_TASK);
    taskPoints.setOccurredUuid(shopTask.getUuid());
    taskPoints.setOccurredDesc(shopTask.getName());
    taskPoints.setUserName(shopTask.getOperatorName());
    taskPoints.setCreateInfo(operateInfo);
    taskPointsService.saveNew(tenant, taskPoints);
  }

  private void sendTransferMessage(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException {
    ShopTaskTransferMessageConfig config = baasConfigClient.getConfig(tenant, ShopTaskTransferMessageConfig.class);
    String path = config.getShopTaskTransferPath() + "?type=" + BShopTaskTransferDetailReqType.transferTo.name()
        + "&shopTaskLogId=" + shopTaskTransfer.getShopTaskLogId() + "&shopTaskId=" + shopTaskTransfer.getShopTaskId();
    String title = "????????????";
    ShopTask shopTask = dao.get(tenant, shopTaskTransfer.getShopTaskId());
    String contentText = shopTask.getName();
    Message message = buildTransferToMessage(tenant, shopTaskTransfer, path, title, contentText, MessageTag.????????????);
    this.sendMessage(tenant, operateInfo, message);
  }

  private void sendTransferAssignableShopTaskMessage(String tenant, String contentText, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException {
    ShopTaskTransferMessageConfig config = baasConfigClient.getConfig(tenant, ShopTaskTransferMessageConfig.class);
    String path = config.getAssignableShopTaskTransferPath() + "?uuid=" + shopTaskTransfer.getShopTaskId();
    String title = "????????????";
    Message message = buildTransferToMessage(tenant, shopTaskTransfer, path, title, contentText, MessageTag.????????????);
    this.sendMessage(tenant, operateInfo, message);
  }

  private void sendBatchTransfer(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException {
    ShopTaskTransferMessageConfig config = baasConfigClient.getConfig(tenant, ShopTaskTransferMessageConfig.class);
    String path = config.getShopTaskTransferPath() + "?type=" + BShopTaskTransferDetailReqType.transferTo.name()
        + "&shopTaskId=" + shopTaskTransfer.getShopTaskId() + "&transferType=" + shopTaskTransfer.getType();
    String title = "????????????";
    ShopTask shopTask = dao.get(tenant, shopTaskTransfer.getShopTaskId());
    String contentText = shopTask.getGroupName();
    Message message = buildTransferToMessage(tenant, shopTaskTransfer, path, title, contentText, MessageTag.????????????);
    this.sendMessage(tenant, operateInfo, message);
  }

  private void checkShopTask(String tenant, String shopTaskId) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");
    ShopTask shopTask = dao.get(tenant, shopTaskId);
    if (shopTask == null) {
      throw new BaasException("??????ID???{}?????????", shopTaskId);
    }
    ShopTaskState state = shopTask.getState();
    switch (state) {
      case EXPIRED:
        throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "???????????????????????????????????????"));
      case FINISHED:
        throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "???????????????????????????????????????"));
      case TERMINATE:
        throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "???????????????????????????????????????"));
      case SUBMITTED:
        throw new BaasException(new BaasStatus(TaskPlanNewServiceImpl.ERROR_CODE, "???????????????????????????????????????"));
      default:
        break;
    }
  }

  private Message buildTransferToMessage(String tenant, ShopTaskTransfer shopTaskTransfer, String path, String title, String contentText, String tag) {
    Message message = new Message();
    message.setTenant(tenant);
    message.setShop(shopTaskTransfer.getShop());
    message.setShopCode(shopTaskTransfer.getShopCode());
    message.setShopName(shopTaskTransfer.getShopName());
    message.setAction(MessageAction.PAGE);
    message.setActionInfo(path);
    message.setTitle(title);
    message.setType(MessageType.NOTICE);
    message.setTag(tag);
    message.setSource(shopTaskTransfer.getShopTaskId());
    Map<MessageContentKey, String> content = new LinkedHashMap<>();
    content.put(MessageContentKey.TEXT, "?????????????????????????????????" + contentText);
    message.setContent(content);
    message.setUserId(shopTaskTransfer.getTransferTo());
    return message;
  }

  private void sendRefuseAssignableShopTask(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException {
    ShopTaskTransferMessageConfig config = baasConfigClient.getConfig(tenant, ShopTaskTransferMessageConfig.class);
    String path = config.getAssignableAcceptOrRefusePath() + "?uuid=" + shopTaskTransfer.getShopTaskId();
    String title = "????????????";
    ShopTask shopTask = dao.get(tenant, shopTaskTransfer.getShopTaskId());
    String contentText = shopTask.getName();
    Message message = buildRefuseTransferFromMessage(tenant, shopTaskTransfer, path, title, contentText, MessageTag.????????????);
    this.sendMessage(tenant, operateInfo, message);
  }

  private void sendAcceptAssignableShopTaskMessage(String tenant, String contentText, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) throws BaasException {
    ShopTaskTransferMessageConfig config = baasConfigClient.getConfig(tenant, ShopTaskTransferMessageConfig.class);
    String path = config.getAssignableAcceptOrRefusePath() + "?uuid=" + shopTaskTransfer.getShopTaskId();
    String title = "????????????";
    Message message = buildAcceptTransferFromMessage(tenant, shopTaskTransfer, path, title, contentText, MessageTag.????????????);
    this.sendMessage(tenant, operateInfo, message);
  }

  private void changeShopTaskOperator(String tenant, ShopTask shopTask, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTask);

    dao.changeShopTaskOperator(tenant, shopTask, operateInfo);
  }

  private void sendBatchAcceptMessage(String tenant, ShopTaskTransfer shopTaskTransferHistory, OperateInfo operateInfo) throws BaasException {
    ShopTaskTransferMessageConfig config = baasConfigClient.getConfig(tenant, ShopTaskTransferMessageConfig.class);
    String path = config.getShopTaskTransferPath() + "?type=" + BShopTaskTransferDetailReqType.transferFrom.name()
        + "&shopTaskId=" + shopTaskTransferHistory.getShopTaskId() + "&transferType=" + shopTaskTransferHistory.getType();
    String title = "????????????";
    ShopTask shopTask = dao.get(tenant, shopTaskTransferHistory.getShopTaskId());
    String contentText = shopTask.getGroupName();
    Message message = buildAcceptTransferFromMessage(tenant, shopTaskTransferHistory, path, title, contentText, MessageTag.????????????);
    this.sendMessage(tenant, operateInfo, message);
  }

  private void sendMessage(String tenant, OperateInfo operateInfo, Message message) throws BaasException {
    MessageConfig messageConfig = baasConfigClient.getConfig(tenant, MessageConfig.class);
    if (MessageConfig.FMS.equals(messageConfig.getAppMessageVendor())) {
      MessageConfig config = baasConfigClient.getConfig(tenant, MessageConfig.class);
      if (MessageConfig.FMS.equals(config.getAppMessageVendor())) {
        AppMessageSaveNewReq convert = MessageConvertUtil.MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ.convert(message);
        convert.setOperateInfo(operateInfo);
        BaasResponse<Void> response = fmsClient.batchSave(tenant, message.getOrgId(), Collections.singletonList(convert));
        if (!response.isSuccess()) {
          throw new BaasException("??????fms?????????code???{}???msg???{}", response.getCode(), response.getMsg());
        }
      }
    } else {
      messageService.create(tenant, message, operateInfo);
    }
  }

  private void changeShopTaskLogOperator(String tenant, ShopTaskTransfer shopTaskTransfer, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskTransfer.getTransferTo(), "transferTo");
    Assert.hasText(shopTaskTransfer.getShopTaskId(), "shopTaskId");

    shopTaskLogDao.changeShopTaskLogOperator(tenant, shopTaskTransfer, operateInfo);
  }

}
