package com.hd123.baas.sop.job.bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.MessageConfig;
import com.hd123.baas.sop.config.TaskMessageConfig;
import com.hd123.baas.sop.config.TaskPlanJobConfig;
import com.hd123.baas.sop.service.impl.task.message.MessageTag;
import com.hd123.baas.sop.service.api.message.Message;
import com.hd123.baas.sop.service.api.message.MessageAction;
import com.hd123.baas.sop.service.api.message.MessageContentKey;
import com.hd123.baas.sop.service.api.message.MessageService;
import com.hd123.baas.sop.service.api.message.MessageType;
import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.baas.sop.service.api.task.ShopTaskService;
import com.hd123.baas.sop.service.api.taskplan.AssignTaskPlanType;
import com.hd123.baas.sop.service.api.taskplan.TaskPlan;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanItem;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanLine;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanNewService;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanState;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanType;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.AppMessageSaveNewReq;
import com.hd123.baas.sop.remote.fms.bean.MessageConvertUtil;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.baas.sop.utils.entity.CronType;
import com.hd123.mpa.api.common.JSONUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryOrderDirection;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.util.converter.ConverterUtil;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author guyahui
 */
@Slf4j
@DisallowConcurrentExecution
@Component
public class TaskPlanAssignableJob implements Job {

  @Autowired
  private TaskPlanNewService taskPlanNewService;
  @Autowired
  private ShopTaskService shopTaskService;
  @Autowired
  private MessageService messageService;
  @Autowired
  private BaasConfigClient baasConfigClient;
  @Autowired
  private FmsClient fmsClient;

  public static final int PAGE_SIZE = 100;

  /**
   * 标记手动触发任务还是job触发任务，以便于精确知道当前修改人的身份
   */
  public static final String SOURCE_JOB = "JOB";
  public static final String SOURCE_HAND = "hand";

  private static final SimpleDateFormat WEEK = new SimpleDateFormat("yyyy/MM/dd");
  private static final SimpleDateFormat MONTH = new SimpleDateFormat("yyyy/MM");
  private static final SimpleDateFormat YYMMDD_SPACE = new SimpleDateFormat("yyyy-MM-dd ");
  private static final SimpleDateFormat YYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {

    MDC.put("trace_id", UUID.randomUUID().toString());
    log.info("准备执行普通任务计划发布");
    List<String> tenants = getTenants();
    if (CollectionUtils.isEmpty(tenants)) {
      log.info("当前无租户，忽略");
      return;
    }
    for (String tenant : tenants) {
      finishTaskPlan(tenant);
      publishSingle(tenant);
      publishWeekly(tenant);
      publishMonth(tenant);
    }
  }

  private void finishTaskPlan(String tenant) {
    // 分页遍历所有的任务计划，将处于已结束的任务状态更改为已结束
    QueryDefinition queryDefinition = new QueryDefinition();
    queryDefinition.setPageSize(PAGE_SIZE);
    queryDefinition.addByField(TaskPlan.Queries.END_DATE, Cop.LESS, new Date());
    queryDefinition.addByField(TaskPlan.Queries.STATE, Cop.EQUALS, TaskPlanState.EFFECTIVE.name());
    queryDefinition.addByField(TaskPlan.Queries.TYPE, Cop.EQUALS, TaskPlanType.ASSIGNABLE.name());
    QueryResult<TaskPlan> query = taskPlanNewService.query(tenant, queryDefinition);
    if (CollectionUtils.isEmpty(query.getRecords())) {
      return;
    }
    for (int page = 0; page < query.getPageCount(); page++) {
      QueryDefinition qd = new QueryDefinition();
      qd.setPageSize(PAGE_SIZE);
      qd.setPage(page);
      qd.addByField(TaskPlan.Queries.END_DATE, Cop.LESS, new Date());
      qd.addByField(TaskPlan.Queries.STATE, Cop.EQUALS, TaskPlanState.EFFECTIVE.name());
      qd.addByField(TaskPlan.Queries.TYPE, Cop.EQUALS, TaskPlanType.ASSIGNABLE.name());
      qd.addOrder(TaskPlan.Queries.CREATED, QueryOrderDirection.desc);
      QueryResult<TaskPlan> queryResult = taskPlanNewService.query(tenant, qd);
      if (CollectionUtils.isNotEmpty(queryResult.getRecords())) {
        for (TaskPlan taskPlan : queryResult.getRecords()) {
          taskPlanNewService.updateState(tenant, taskPlan.getUuid(), TaskPlanState.FINISHED.name(),
              getSysOperateInfo());
        }
      }
    }
  }

  private void publishSingle(String tenant) {
    // 获取总页数
    int total = getTaskPlanPageTotal(tenant, CronType.single);
    // 分页处理结果
    for (int page = 0; page < total; page++) {
      List<TaskPlan> taskPlans;
      taskPlans = getResultByPage(tenant, page, CronType.single);
      if (CollectionUtils.isEmpty(taskPlans)) {
        continue;
      }
      for (TaskPlan taskPlan : taskPlans) {
        publishTaskPlan(tenant, taskPlan, SOURCE_JOB);
      }
    }
  }

  private void publishWeekly(String tenant) {
    log.info("按周发布普通任务计划开始");
    // 获取总页数
    int total = getTaskPlanPageTotal(tenant, CronType.weekly);
    // 分页处理结果
    for (int page = 0; page < total; page++) {
      List<TaskPlan> taskPlans;
      taskPlans = getResultByPage(tenant, page, CronType.weekly);
      if (CollectionUtils.isEmpty(taskPlans)) {
        continue;
      }
      for (TaskPlan taskPlan : taskPlans) {
        publishTaskPlan(tenant, taskPlan, SOURCE_JOB);
      }
    }
  }

  private void publishMonth(String tenant) {
    log.info("按月发布普通任务计划开始");
    // 获取总页数
    int total = getTaskPlanPageTotal(tenant, CronType.monthly);
    // 分页处理结果
    for (int page = 0; page < total; page++) {
      List<TaskPlan> taskPlans;
      taskPlans = getResultByPage(tenant, page, CronType.monthly);
      if (CollectionUtils.isEmpty(taskPlans)) {
        continue;
      }
      for (TaskPlan taskPlan : taskPlans) {
        publishTaskPlan(tenant, taskPlan, SOURCE_JOB);
      }
    }
  }

  /**
   * 获取总页数
   *
   * @param tenant
   *     租户
   * @param cronType
   *     计划周期
   * @return 任务计划总页码
   */
  private int getTaskPlanPageTotal(String tenant, CronType cronType) {
    QueryDefinition qd = new QueryDefinition();
    qd.setPageSize(PAGE_SIZE);
    qd.addByField(TaskPlan.Queries.START_DATE, Cop.LESS_OR_EQUALS, getPublishEffectiveEndDate(tenant));
    qd.addByField(TaskPlan.Queries.END_DATE, Cop.GREATER_OR_EQUALS, new Date());
    qd.addByField(TaskPlan.Queries.TYPE, Cop.EQUALS, TaskPlanType.ASSIGNABLE.name());
    if (cronType.equals(CronType.single)) {
      qd.addByField(TaskPlan.Queries.CYCLE, Cop.EQUALS, CronType.single.name());
      qd.addByField(TaskPlan.Queries.STATE, Cop.EQUALS, TaskPlanState.UN_EFFECTIVE.name());
    } else if (cronType.equals(CronType.weekly)) {
      qd.addByField(TaskPlan.Queries.CYCLE, Cop.EQUALS, CronType.weekly.name());
      qd.addByField(TaskPlan.Queries.STATE, Cop.IN,
          Arrays.asList(TaskPlanState.UN_EFFECTIVE.name(), TaskPlanState.EFFECTIVE.name()).toArray());
    } else if (cronType.equals(CronType.monthly)) {
      qd.addByField(TaskPlan.Queries.CYCLE, Cop.EQUALS, CronType.monthly.name());
      qd.addByField(TaskPlan.Queries.STATE, Cop.IN,
          Arrays.asList(TaskPlanState.UN_EFFECTIVE.name(), TaskPlanState.EFFECTIVE.name()).toArray());
    }
    QueryResult<TaskPlan> query = taskPlanNewService.query(tenant, qd);
    return query.getPageCount();
  }

  private List<TaskPlanLine> getTaskPlanLine(String tenant, String uuid) {
    if (StringUtils.isEmpty(uuid)) {
      return new ArrayList<>();
    }
    return taskPlanNewService.listTaskPlanLineByOwner(tenant, uuid);
  }

  private List<TaskPlan> getResultByPage(String tenant, int page, CronType cronType) {
    QueryDefinition queryDefinition = new QueryDefinition();
    queryDefinition.setPageSize(PAGE_SIZE);
    queryDefinition.setPage(page);
    queryDefinition.addOrder(TaskPlan.Queries.CREATED, QueryOrderDirection.desc);
    queryDefinition.addByField(TaskPlan.Queries.START_DATE, Cop.LESS_OR_EQUALS, getPublishEffectiveEndDate(tenant));
    queryDefinition.addByField(TaskPlan.Queries.END_DATE, Cop.GREATER_OR_EQUALS, new Date());
    queryDefinition.addByField(TaskPlan.Queries.TYPE, Cop.EQUALS, TaskPlanType.ASSIGNABLE.name());
    if (CronType.single.equals(cronType)) {
      queryDefinition.addByField(TaskPlan.Queries.STATE, Cop.EQUALS, TaskPlanState.UN_EFFECTIVE.name());
      queryDefinition.addByField(TaskPlan.Queries.CYCLE, Cop.EQUALS, CronType.single.name());
    } else if (CronType.weekly.equals(cronType)) {
      queryDefinition.addByField(TaskPlan.Queries.STATE, Cop.IN,
          Arrays.asList(TaskPlanState.UN_EFFECTIVE.name(), TaskPlanState.EFFECTIVE.name()).toArray());
      queryDefinition.addByField(TaskPlan.Queries.CYCLE, Cop.EQUALS, CronType.weekly.name());
    } else if (CronType.monthly.equals(cronType)) {
      queryDefinition.addByField(TaskPlan.Queries.STATE, Cop.IN,
          Arrays.asList(TaskPlanState.UN_EFFECTIVE.name(), TaskPlanState.EFFECTIVE.name()).toArray());
      queryDefinition.addByField(TaskPlan.Queries.CYCLE, Cop.EQUALS, CronType.monthly.name());
    }
    QueryResult<TaskPlan> pageQuery = taskPlanNewService.query(tenant, queryDefinition);
    List<TaskPlan> taskPlans;
    taskPlans = pageQuery.getRecords();
    return taskPlans;
  }

  /**
   * 发布任务
   */
  @Tx
  public void publishTaskPlan(String tenant, TaskPlan taskPlan, String source) {
    if (taskPlan == null || StringUtils.isEmpty(taskPlan.getCycle())) {
      return;
    }
    List<TaskPlanLine> taskPlanLines = getTaskPlanLine(tenant, taskPlan.getUuid());
    List<TaskPlanItem> taskPlanItems = getTaskPlanItem(tenant, taskPlan.getUuid());
    if (CollectionUtils.isEmpty(taskPlanItems)) {
      log.info("巡检计划描述为空，不能发布该任务，任务id为：{}", taskPlan.getUuid());
      return;
    }
    // 获取所有的发布日期
    List<Date> allPublishDate = getAllPublishDate(tenant, taskPlan);
    // 发布
    for (Date publishDate : allPublishDate) {
      if (CronType.weekly.name().equals(taskPlan.getCycle()) || CronType.monthly.name().equals(taskPlan.getCycle())) {
        if (validTaskPublishDate(tenant, publishDate, taskPlan)) {
          publishShopTask(tenant, taskPlanLines, taskPlanItems, taskPlan, publishDate, source);
        }
      } else if (CronType.single.name().equals(taskPlan.getCycle())) {
        if (validTaskPublishDate(tenant, publishDate, taskPlan)) {
          publishShopTask(tenant, taskPlanLines, taskPlanItems, taskPlan, taskPlan.getStartDate(), source);
        }
      }
    }
  }

  private List<TaskPlanItem> getTaskPlanItem(String tenant, String uuid) {
    if (StringUtils.isEmpty(uuid)) {
      return new ArrayList<>();
    }
    return taskPlanNewService.listTaskPlanItemByTaskPlanIds(tenant, Collections.singletonList(uuid));
  }

  @SneakyThrows
  public List<Date> getAllPublishDate(String tenant, TaskPlan taskPlan) {
    List<Date> dateList = new ArrayList<>();
    if (CronType.weekly.name().equals(taskPlan.getCycle())) {
      Date weekStartDate = getWeekStartDate(taskPlan);
      Date weekEndDate = getEndDate(weekStartDate, taskPlan);
      Date publishEffectiveEndDate = getPublishEffectiveEndDate(tenant);
      do {
        if (weekStartDate.compareTo(publishEffectiveEndDate) <= 0 && weekEndDate.compareTo(taskPlan.getEndDate()) <= 0
            && weekStartDate.compareTo(new Date()) >= 0 && weekStartDate.compareTo(taskPlan.getStartDate()) >= 0) {
          dateList.add(weekStartDate);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(weekStartDate);
        calendar.add(Calendar.DATE, 7);
        weekStartDate = calendar.getTime();
        weekEndDate = getEndDate(weekStartDate, taskPlan);
      } while (weekStartDate.compareTo(publishEffectiveEndDate) <= 0
          && weekEndDate.compareTo(taskPlan.getEndDate()) <= 0);
    } else if (CronType.monthly.name().equals(taskPlan.getCycle())) {
      Date monthStartDate = getMonthStartDate(taskPlan);
      Date monthEndDate = getEndDate(monthStartDate, taskPlan);
      Date publishEffectiveEndDate = getPublishEffectiveEndDate(tenant);
      do {
        if (monthStartDate.compareTo(publishEffectiveEndDate) <= 0 && monthEndDate.compareTo(taskPlan.getEndDate()) <= 0
            && monthStartDate.compareTo(new Date()) >= 0 && monthStartDate.compareTo(taskPlan.getStartDate()) >= 0) {
          dateList.add(monthStartDate);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(monthStartDate);
        calendar.add(Calendar.MONTH, 1);
        monthStartDate = calendar.getTime();
        monthEndDate = getEndDate(monthStartDate, taskPlan);
      } while (monthStartDate.compareTo(publishEffectiveEndDate) <= 0
          && monthEndDate.compareTo(taskPlan.getEndDate()) <= 0);
    } else if (CronType.single.name().equals(taskPlan.getCycle())) {
      if (taskPlan.getStartDate() != null && new Date().compareTo(taskPlan.getEndDate()) <= 0) {
        dateList.add(taskPlan.getStartDate());
      }
    }
    return dateList;
  }

  @Tx
  public void publishShopTask(String tenant, List<TaskPlanLine> taskPlanLine, List<TaskPlanItem> taskPlanItems,
      TaskPlan taskPlan, Date publishDate, String source) {
    if (taskPlan == null || StringUtils.isEmpty(taskPlan.getAssignType())) {
      return;
    }
    // 指派和抢单的普通任务执行不同的shopTask构造逻辑，指派直接根据line中的条数进行发布，抢单任务则每个plan只需要生成一个shop_task记录
    List<ShopTask> shopTaskList = new ArrayList<>();
    if (AssignTaskPlanType.GRABBING_ORDERS.name().equals(taskPlan.getAssignType())) {
      try {
        buildGrabbingOrders(tenant, shopTaskList, taskPlanItems, taskPlan, CronType.valueOf(taskPlan.getCycle()),
            publishDate);
        // 发布任务
        publishTask(tenant, taskPlan.getUuid(), shopTaskList, taskPlan, source);
      } catch (Exception e) {
        log.error("任务发布失败，任务ID为：{}", taskPlan.getUuid(), e);
      }
      changeTaskPlanStateAndPublishDateCollect(tenant, taskPlan.getUuid(), publishDate, source, taskPlan);
    } else if (AssignTaskPlanType.ASSIGN.name().equals(taskPlan.getAssignType())) {
      try {
        buildAssign(tenant, shopTaskList, taskPlanLine, taskPlanItems, taskPlan, CronType.valueOf(taskPlan.getCycle()),
            publishDate);
        // 发布任务
        publishTask(tenant, taskPlan.getUuid(), shopTaskList, taskPlan, source);
      } catch (Exception e) {
        log.error("任务发布失败，任务ID为：{}", taskPlan.getUuid(), e);
      }
      changeTaskPlanStateAndPublishDateCollect(tenant, taskPlan.getUuid(), publishDate, source, taskPlan);
    } else {
      log.error("当前普通任务没有指定任务类型，任务ID为：{}", taskPlan.getUuid());
    }
    // 发送消息通知
    sendMessage(tenant, taskPlan, taskPlanLine, shopTaskList);
  }

  private void buildAssign(String tenant, List<ShopTask> shopTaskList, List<TaskPlanLine> taskPlanLine,
      List<TaskPlanItem> taskPlanItems, TaskPlan taskPlan, CronType type, Date publishDate) {
    for (int i = 0; i < taskPlanItems.size(); i++) {
      TaskPlanItem taskPlanItem = taskPlanItems.get(i);
      taskPlanLine.forEach(line -> {
        ShopTask shopTask = new ShopTask();
        // ------- 构造反馈信息开始 -----------
        shopTask.setDescription(taskPlan.getDescription());
        shopTask.setWordNeeded(taskPlan.isWordNeeded());
        shopTask.setImageNeeded(taskPlan.isImageNeeded());
        // shopTask.setVideoNeeded();
        shopTask.setTemplateCls(taskPlan.getTemplateCls());
        // --------- 反馈信息构造完毕 ---------

        // ---------构造店铺信息开始 ----------
        shopTask.setShop(line.getShop());
        shopTask.setTenant(tenant);
        shopTask.setShopCode(line.getShopCode());
        shopTask.setShopName(line.getShopName());
        // shopTask.setShopTaskGroup(); // 该项没有值
        // ---------构造店铺信息完毕 ----------

        // ------- 构造计划信息开始 -----------
        shopTask.setPlan(taskPlan.getUuid());
        shopTask.setPlanCode(taskPlan.getCode());
        shopTask.setPlanName(taskPlan.getName());
        shopTask.setPlanType(taskPlan.getType());
        shopTask.setAssignType(taskPlan.getAssignType());
        shopTask.setPlanPeriod(getPlanPeriod(publishDate, type));
        shopTask.setName(taskPlan.getName());
        // shopTask.setPlanTime();
        try {
          shopTask.setRemindTime(getRemindDate(type, taskPlan, publishDate));
        } catch (ParseException e) {
          log.error("任务计划发布失败，提醒详细时间类型转换失败，计划id为：{}", taskPlan.getUuid(), e);
          e.printStackTrace();
        }
        // shopTask.setPlanPeriodCode();
        shopTask.setPlanStartTime(publishDate);
        shopTask.setPlanEndTime(getEndDate(publishDate, taskPlan));
        // ------- 构造计划信息完毕 -----------

        // ------- 构造巡检人信息-----------
        shopTask.setOperatorId(line.getAssigneeId());
        shopTask.setOperatorName(line.getAssignee());
        shopTask.setOperatorPositionCode(line.getPositionCode());
        shopTask.setOperatorPositionName(line.getPositionName());
        shopTask.setCreatorShop(taskPlan.getCreatorShop());
        shopTask.setCreatorShopCode(taskPlan.getCreatorShopCode());
        shopTask.setCreatorShopName(taskPlan.getCreatorShopName());
        // ------- 构造巡检人信息结束-----------

        // -------- 构造创建人信息 ------------
        shopTask.setCreateInfo(taskPlan.getCreateInfo());
        shopTask.setLastModifyInfo(taskPlan.getLastModifyInfo());
        // -------- 构造创建人信息结束 ---------

        // 普通任务描述信息构造
        shopTask.setPoint(taskPlanItem.getPoint());
        shopTask.setAudit(taskPlanItem.getAudit());
        shopTask.setComment(taskPlanItem.getComment());
        shopTask.setAttachFiles(taskPlanItem.getAttachFiles());
        shopTask.setStarted(new Date());

        shopTaskList.add(shopTask);
      });
    }
  }

  private void buildGrabbingOrders(String tenant, List<ShopTask> shopTaskList, List<TaskPlanItem> taskPlanItems,
      TaskPlan taskPlan, CronType type, Date publishDate) {
    for (TaskPlanItem taskPlanItem : taskPlanItems) {
      ShopTask shopTask = new ShopTask();
      shopTask.setUuid(IdGenUtils.buildRdUuid());
      // ------- 构造反馈信息开始 -----------
      shopTask.setDescription(taskPlan.getDescription());
      shopTask.setWordNeeded(taskPlan.isWordNeeded());
      shopTask.setImageNeeded(taskPlan.isImageNeeded());
      // shopTask.setVideoNeeded();
      shopTask.setTemplateCls(taskPlan.getTemplateCls());
      // --------- 反馈信息构造完毕 ---------

      // ------- 构造计划信息开始 -----------
      shopTask.setPlan(taskPlan.getUuid());
      shopTask.setTenant(tenant);
      shopTask.setPlanCode(taskPlan.getCode());
      shopTask.setPlanName(taskPlan.getName());
      shopTask.setPlanType(taskPlan.getType());
      shopTask.setAssignType(taskPlan.getAssignType());
      shopTask.setPlanPeriod(getPlanPeriod(publishDate, type));
      shopTask.setName(taskPlan.getName());
      // shopTask.setPlanTime();
      try {
        shopTask.setRemindTime(getRemindDate(type, taskPlan, publishDate));
      } catch (ParseException e) {
        log.error("任务计划发布失败，提醒详细时间类型转换失败，计划id为：{}", taskPlan.getUuid(), e);
        e.printStackTrace();
      }
      // shopTask.setPlanPeriodCode();
      shopTask.setPlanStartTime(publishDate);
      shopTask.setPlanEndTime(getEndDate(publishDate, taskPlan));
      // ------- 构造计划信息完毕 -----------

      // -------- 构造创建人信息 ------------
      shopTask.setCreateInfo(taskPlan.getCreateInfo());
      shopTask.setLastModifyInfo(taskPlan.getLastModifyInfo());
      shopTask.setCreatorShop(taskPlan.getCreatorShop());
      shopTask.setCreatorShopCode(taskPlan.getCreatorShopCode());
      shopTask.setCreatorShopName(taskPlan.getCreatorShopName());
      // -------- 构造创建人信息结束 ---------

      // 普通任务描述信息构造
      shopTask.setPoint(taskPlanItem.getPoint());
      shopTask.setAudit(taskPlanItem.getAudit());
      shopTask.setComment(taskPlanItem.getComment());
      shopTask.setAttachFiles(taskPlanItem.getAttachFiles());
      shopTask.setStarted(new Date());

      shopTaskList.add(shopTask);
    }
  }

  private void changeTaskPlanStateAndPublishDateCollect(String tenant, String planId, Date publishDate, String source,
      TaskPlan taskPlan) {
    // 更改任务状态
    taskPlanNewService.updateState(tenant, planId, TaskPlanState.EFFECTIVE.name(), getSysOperateInfo());
    // 更改任务发布时间
    TaskPlan taskPlan1 = taskPlanNewService.get(tenant, planId);
    List<Date> convert = null;
    try {
      convert = JSONUtil.safeToObject(taskPlan1.getPublishTaskDateCollect(), new TypeReference<ArrayList<Date>>() {
      });
      if (CollectionUtils.isEmpty(convert)) {
        convert = new ArrayList<>();
      }
      if (!convert.contains(publishDate)) {
        convert.add(publishDate);
      }
      taskPlanNewService.updatePublishTaskDateCollect(tenant, planId,
          com.qianfan123.baas.common.util.JSONUtil.safeToJson(convert));
    } catch (BaasException e) {
      log.error("任务发布状态修改失败，计划ID为：{}", planId, e);
      e.printStackTrace();
    }
  }

  /**
   * 获取所有租户
   *
   * @return 租户列表
   */
  @SneakyThrows
  private List<String> getTenants() {
    return taskPlanNewService.listTenant().stream().map(TaskPlan::getTenant).collect(Collectors.toList());
  }

  protected OperateInfo getSysOperateInfo() {
    OperateInfo operateInfo = new OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("系统用户");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }

  private String getPlanPeriod(Date publishDate, CronType type) {
    String planPeriod = "单次";
    if (CronType.single.equals(type)) {
      return planPeriod;
    } else if (CronType.weekly.equals(type)) {
      return WEEK.format(publishDate);
    } else if (CronType.monthly.equals(type)) {
      return MONTH.format(publishDate);
    }
    return planPeriod;
  }

  /**
   * 获取当前日期是星期几<br>
   *
   * @return 当前日期是星期几
   */
  private static int getWeekOfDate() {
    int[] weekDays = {
        7, 1, 2, 3, 4, 5, 6 };
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
    if (w < 0)
      w = 0;
    return weekDays[w];
  }

  private static int getMonthOfDay() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
    String format = simpleDateFormat.format(new Date());
    return Integer.parseInt(format);
  }

  @SneakyThrows
  private boolean validTaskPublishDate(String tenant, Date publishDate, TaskPlan taskPlan) {
    // 判断开始日期在指定的发布时间内
    if (getPublishEffectiveEndDate(tenant).compareTo(publishDate) < 0) {
      return false;
    }
    // 判断发布日期是否发布过
    TaskPlan taskPlan1 = taskPlanNewService.get(tenant, taskPlan.getUuid());
    List<Date> convert = JSONUtil.safeToObject(taskPlan1.getPublishTaskDateCollect(),
        new TypeReference<ArrayList<Date>>() {
        });
    // 周期性的任务发布需要发布开始时间大于等于计划的开始时间，终止日期需要小于等于计划的终止时间，一次发布的截至时间也应大于当前时间
    Date endDate = getEndDate(publishDate, taskPlan);
    return (taskPlan.getPublishTaskDate() == null || taskPlan.getPublishTaskDate().compareTo(publishDate) != 0)
        && (!CollectionUtils.isNotEmpty(convert) || !convert.contains(publishDate))
        && (endDate.compareTo(new Date()) > 0) && (publishDate.compareTo(taskPlan.getStartDate()) >= 0);
  }

  /**
   * 获取提醒时间，此处默认计划任务已经有了开始有效期时间
   *
   * @param cronType
   *     周期类型
   * @param taskPlan
   *     计划
   * @return 提醒时间
   */
  private Date getRemindDate(CronType cronType, TaskPlan taskPlan, Date publishDate) throws ParseException {
    if (CronType.single.equals(cronType)) {
      return taskPlan.getRemindDate();
    } else if (CronType.weekly.equals(cronType) || CronType.monthly.equals(cronType)) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(publishDate);
      calendar.add(Calendar.DATE, taskPlan.getDelayDay());
      String startDateTmp = YYMMDD_SPACE.format(calendar.getTime()) + taskPlan.getRemindDetailTime() + ":00";
      return YYMMDD_HHMMSS.parse(startDateTmp);
    }
    return null;
  }

  private Date getWeekStartDate(TaskPlan taskPlan) throws Exception {
    int weekOfDate = getWeekOfDate();
    String startDateTmp = YYMMDD_SPACE.format(new Date()) + taskPlan.getPublishDate() + ":00";
    Date startDate = YYMMDD_HHMMSS.parse(startDateTmp);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    calendar.add(Calendar.DATE, taskPlan.getDayOfWeek() - weekOfDate);
    return calendar.getTime();
  }

  private Date getMonthStartDate(TaskPlan taskPlan) throws Exception {
    int monthOfDay = getMonthOfDay();
    String startDateTmp = YYMMDD_SPACE.format(new Date()) + taskPlan.getPublishDate() + ":00";
    Date startDate = YYMMDD_HHMMSS.parse(startDateTmp);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    calendar.add(Calendar.DATE, getEffectiveDayOfMonth(taskPlan.getDayOfMonth()) - monthOfDay);
    return calendar.getTime();
  }

  @SneakyThrows
  private Date getEndDate(Date publishDate, TaskPlan taskPlan) {
    if (CronType.single.name().equals(taskPlan.getCycle())) {
      return taskPlan.getEndDate();
    } else if (CronType.monthly.name().equals(taskPlan.getCycle())
        || CronType.weekly.name().equals(taskPlan.getCycle())) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(publishDate);
      calendar.add(Calendar.DATE, taskPlan.getValidityDays());
      return calendar.getTime();
    } else {
      log.error("taskPlan对应的周期不合法，taskPlan Id为：{}", taskPlan.getUuid());
      throw new BaasException("taskPlan对应的周期不合法, taskPlan Id为:" + taskPlan.getUuid());
    }
  }

  private void publishTask(String tenant, String plan, List<ShopTask> tasks, TaskPlan taskPlan, String source)
      throws Exception {
    if (SOURCE_JOB.equals(source)) {
      shopTaskService.batchSaveNewAssign(tenant, plan, tasks, getSysOperateInfo());
    } else if (SOURCE_HAND.equals(source)) {
      shopTaskService.batchSaveNewAssign(tenant, plan, tasks, taskPlan.getLastModifyInfo());
    } else {
      log.error("无效的来源，计划ID为：{}", plan);
    }
  }

  public void sendMessage(String tenant, TaskPlan taskPlan, List<TaskPlanLine> lines, List<ShopTask> shopTasks) {
    if (CollectionUtils.isEmpty(lines)) {
      return;
    }
    try {
      TaskMessageConfig config = baasConfigClient.getConfig(tenant, TaskMessageConfig.class);
      List<Message> messageList = new ArrayList<>();
      Message message = new Message();
      if (AssignTaskPlanType.GRABBING_ORDERS.name().equals(taskPlan.getAssignType())) {
        for (int i = 0; i < lines.size(); i++) {
          TaskPlanLine taskPlanLine = lines.get(i);
          message = buildGrabOrderMessage(tenant, taskPlan, config.getGrabOrderPage(), shopTasks, taskPlanLine);
          if (message != null) {
            messageList.add(message);
          }
        }
      } else if (AssignTaskPlanType.ASSIGN.name().equals(taskPlan.getAssignType())) {
        for (ShopTask shopTask : shopTasks) {
          message = buildAssignMessage(tenant, taskPlan, shopTask, config.getAssignableShopTaskPage());
          if (message != null) {
            messageList.add(message);
          }
        }
      } else {
        log.error("该普通任务未指定指派类型");
      }
      MessageConfig messageConfig = baasConfigClient.getConfig(tenant, MessageConfig.class);
      if (MessageConfig.FMS.equals(messageConfig.getAppMessageVendor())) {
        List<AppMessageSaveNewReq> reqs = ConverterUtil.convert(messageList, MessageConvertUtil.MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ);
        reqs.forEach(req->{req.setOperateInfo(this.getSysOperateInfo());});
        BaasResponse<Void> response = fmsClient.batchSave(tenant, shopTasks.get(0).getOrgId(), reqs);
        if (!response.isSuccess()) {
          throw new BaasException("发送失败，code：{}，msg：{}", response.getCode(), response.getMsg());
        }
      } else {
        messageService.batchCreate(tenant, messageList, getSysOperateInfo());
      }
    } catch (BaasException e) {
      log.error("发送消息提醒失败：{}", JsonUtil.objectToJson(e));
    }
  }

  private Message buildGrabOrderMessage(String tenant, TaskPlan taskPlan, String checkTaskPage,
      List<ShopTask> shopTasks, TaskPlanLine taskPlanLine) {
    if (CollectionUtils.isEmpty(shopTasks) || StringUtils.isEmpty(shopTasks.get(0).getUuid())) {
      return null;
    }
    if (StringUtils.isEmpty(taskPlanLine.getAssigneeId())) {
      return null;
    }
    Message message = new Message();
    message.setTenant(tenant);
    message.setShop(taskPlanLine.getShop());
    message.setShopCode(taskPlanLine.getShopCode());
    message.setShopName(taskPlanLine.getShopName());
    message.setAction(MessageAction.PAGE);
    message.setActionInfo(checkTaskPage + "?uuid=" + shopTasks.get(0).getUuid());
    message.setTitle("抢单任务");
    message.setType(MessageType.NOTICE);
    message.setTag(MessageTag.抢单任务);
    message.setSource(taskPlan.getSource());
    Map<MessageContentKey, String> content = new LinkedHashMap<>();
    content.put(MessageContentKey.TEXT, "您有一个新任务：" + shopTasks.get(0).getName() + "请尽快领取");
    message.setContent(content);
    message.setUserId(taskPlanLine.getAssigneeId());
    return message;
  }

  private Message buildAssignMessage(String tenant, TaskPlan taskPlan, ShopTask shopTask, String checkTaskPage) {
    if (shopTask == null || StringUtils.isEmpty(shopTask.getOperatorId())) {
      return null;
    }
    Message message = new Message();
    message.setTenant(tenant);
    message.setShop(shopTask.getShop());
    message.setShopCode(shopTask.getShopCode());
    message.setShopName(shopTask.getShopName());
    message.setAction(MessageAction.PAGE);
    message.setActionInfo(checkTaskPage + "?uuid=" + shopTask.getUuid());
    message.setTitle("指派任务");
    message.setType(MessageType.NOTICE);
    message.setTag(MessageTag.指派任务);
    message.setSource(taskPlan.getSource());
    Map<MessageContentKey, String> content = new LinkedHashMap<>();
    content.put(MessageContentKey.TEXT, "您有一个新任务：" + shopTask.getName() + "待完成");
    message.setContent(content);
    message.setUserId(shopTask.getOperatorId());
    return message;
  }

  public Date getPublishEffectiveEndDate(String tenant) {
    TaskPlanJobConfig config = baasConfigClient.getConfig(tenant, TaskPlanJobConfig.class);
    int publishEffectiveDay = config.getDays();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, publishEffectiveDay);
    return calendar.getTime();
  }

  private int getEffectiveDayOfMonth(int monthOfDay) {
    return Math.min(monthOfDay, getCurrentMonthDay());
  }

  /**
   * 获取当月的 天数
   */
  public static int getCurrentMonthDay() {
    Calendar a = Calendar.getInstance();
    a.set(Calendar.DATE, 1);
    a.roll(Calendar.DATE, -1);
    return a.get(Calendar.DATE);
  }

}
