package com.hd123.baas.sop.job.bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.MessageConfig;
import com.hd123.baas.sop.config.TaskMessageConfig;
import com.hd123.baas.sop.service.impl.task.message.MessageTag;
import com.hd123.baas.sop.service.api.message.Message;
import com.hd123.baas.sop.service.api.message.MessageAction;
import com.hd123.baas.sop.service.api.message.MessageContentKey;
import com.hd123.baas.sop.service.api.message.MessageService;
import com.hd123.baas.sop.service.api.message.MessageType;
import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.baas.sop.service.api.task.ShopTaskLog;
import com.hd123.baas.sop.service.api.task.ShopTaskService;
import com.hd123.baas.sop.service.api.task.ShopTaskState;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupService;
import com.hd123.baas.sop.service.api.taskgroup.TaskTemplate;
import com.hd123.baas.sop.service.api.taskplan.TaskPlan;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanLine;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanNewService;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanState;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanType;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.AppMessageSaveNewReq;
import com.hd123.baas.sop.remote.fms.bean.MessageConvertUtil;
import com.hd123.baas.sop.utils.DateUtil;
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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author guyahui
 */
@Slf4j
@DisallowConcurrentExecution
@Component
public class TaskPlanNewJob implements Job {

  @Autowired
  private TaskPlanNewService taskPlanNewService;
  @Autowired
  private ShopTaskService shopTaskService;
  @Autowired
  private TaskGroupService taskGroupService;
  @Autowired
  private MessageService messageService;
  @Autowired
  private BaasConfigClient baasConfigClient;
  @Autowired
  private FmsClient fmsClient;

  public static final int PAGE_SIZE = 100;

  private static final SimpleDateFormat WEEK = new SimpleDateFormat("yyyy/MM/dd");
  private static final SimpleDateFormat MONTH = new SimpleDateFormat("yyyy/MM");
  private static final SimpleDateFormat YYMMDD_SPACE = new SimpleDateFormat("yyyy-MM-dd ");
  private static final SimpleDateFormat YYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {

    MDC.put("trace_id", UUID.randomUUID().toString());
    String tranceId = IdGenUtils.buildRdUuid();
    log.info("????????????????????????,tranceId:{}", tranceId);
    List<String> tenants = getTenants();
    if (CollectionUtils.isEmpty(tenants)) {
      log.info("????????????????????????");
      return;
    }
    for (String tenant : tenants) {
      publishSingle(tenant);
      publishWeekly(tenant);
      publishMonth(tenant);
    }
    log.info("????????????????????????,tranceId:{}", tranceId);
  }

  private void publishSingle(String tenant) {
    // ???????????????
    int total = getTaskPlanPageTotal(tenant, CronType.single);
    // ??????????????????
    for (int page = 0; page < total; page++) {
      List<TaskPlan> taskPlans;
      taskPlans = getResultByPage(tenant, page, CronType.single);
      if (CollectionUtils.isEmpty(taskPlans)) {
        continue;
      }
      for (TaskPlan taskPlan : taskPlans) {
        try {
          publishTaskPlan(tenant, taskPlan);
        } catch (Exception e) {
          log.error("?????????????????????????????????ID??????{}", taskPlan.getUuid(), e);
        }
      }
    }
  }

  private void publishWeekly(String tenant) {
    log.info("??????????????????????????????");
    // ???????????????
    int total = getTaskPlanPageTotal(tenant, CronType.weekly);
    // ??????????????????
    for (int page = 0; page < total; page++) {
      List<TaskPlan> taskPlans;
      taskPlans = getResultByPage(tenant, page, CronType.weekly);
      if (CollectionUtils.isEmpty(taskPlans)) {
        continue;
      }
      for (TaskPlan taskPlan : taskPlans) {
        try {
          publishTaskPlan(tenant, taskPlan);
        } catch (Exception e) {
          log.error("?????????????????????????????????ID??????{}", taskPlan.getUuid(), e);
        }
      }
    }
  }

  private void publishMonth(String tenant) {
    log.info("??????????????????????????????");
    // ???????????????
    int total = getTaskPlanPageTotal(tenant, CronType.monthly);
    // ??????????????????
    for (int page = 0; page < total; page++) {
      List<TaskPlan> taskPlans;
      taskPlans = getResultByPage(tenant, page, CronType.monthly);
      if (CollectionUtils.isEmpty(taskPlans)) {
        continue;
      }
      for (TaskPlan taskPlan : taskPlans) {
        try {
          publishTaskPlan(tenant, taskPlan);
        } catch (Exception e) {
          log.error("?????????????????????????????????ID??????{}", taskPlan.getUuid(), e);
        }
      }
    }
  }

  /**
   * ???????????????
   *
   * @param tenant   ??????
   * @param cronType ????????????
   * @return ?????????????????????
   */
  private int getTaskPlanPageTotal(String tenant, CronType cronType) {
    Date now = new Date();
    now = DateUtil.truncate(now, Calendar.MINUTE);
    QueryDefinition qd = new QueryDefinition();
    qd.setPageSize(PAGE_SIZE);
    qd.addByField(TaskPlan.Queries.TYPE, Cop.EQUALS, TaskPlanType.INSPECTION.name());
    qd.addByField(TaskPlan.Queries.END_DATE, Cop.GREATER_OR_EQUALS, now);
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
    Date now = new Date();
    now = DateUtil.truncate(now, Calendar.MINUTE);
    QueryDefinition queryDefinition = new QueryDefinition();
    queryDefinition.setPageSize(PAGE_SIZE);
    queryDefinition.setPage(page);
    queryDefinition.addOrder(TaskPlan.Queries.CREATED, QueryOrderDirection.desc);
    queryDefinition.addByField(TaskPlan.Queries.TYPE, Cop.EQUALS, TaskPlanType.INSPECTION.name());
    queryDefinition.addByField(TaskPlan.Queries.END_DATE, Cop.GREATER_OR_EQUALS, now);
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
   * ????????????
   */
  @Tx
  public void publishTaskPlan(String tenant, TaskPlan taskPlan) throws Exception {
    if (taskPlan == null || StringUtils.isEmpty(taskPlan.getCycle())) {
      return;
    }
    List<TaskPlanLine> taskPlanLine = getTaskPlanLine(tenant, taskPlan.getUuid());
    if (CollectionUtils.isEmpty(taskPlanLine)) {
      log.info("?????????????????????????????????????????????????????????id??????{}", taskPlan.getUuid());
      return;
    }
    // ???????????????????????????
    List<Date> allPublishDate = getAllPublishDate(taskPlan);
    if (CollectionUtils.isEmpty(allPublishDate)) {
      log.info("?????????????????????????????????????????????id??????{}", taskPlan.getUuid());
      return;
    }
    //??????????????????????????????????????????
    adjustPublishDate(tenant, taskPlan, allPublishDate);
    //??????????????????
    beforePublish(tenant, allPublishDate, taskPlan);
    // ??????,??????????????????????????????????????????????????????????????????????????????????????????????????????
    if (CollectionUtils.isNotEmpty(allPublishDate)) {
      Date publishDate = allPublishDate.get(0);
      publishShopTask(tenant, taskPlanLine, taskPlan, publishDate);
      syncTaskPlan(tenant, publishDate, taskPlan);
    } else {
      log.warn("????????????????????????????????????????????????");
      return;
    }
  }

  private void syncTaskPlan(String tenant, Date publishDate, TaskPlan taskPlan) throws BaasException {
    //???????????????????????????
    TaskPlan taskPlanHistory = taskPlanNewService.get(tenant, taskPlan.getUuid());
    if (null == taskPlanHistory) {
      throw new BaasException("?????????????????????,??????ID??????{}", taskPlan.getUuid());
    }
    List<Date> publishedDates = JSONUtil.safeToObject(taskPlanHistory.getPublishTaskDateCollect(),
        new TypeReference<ArrayList<Date>>() {
        });
    if (CollectionUtils.isEmpty(publishedDates)) {
      publishedDates = new ArrayList<>();
    }
    publishedDates.add(publishDate);
    //??????????????????
    taskPlanNewService.updatePublishTaskDateCollect(tenant, taskPlan.getUuid(), JSONUtil.safeToJson(publishedDates));
    //??????????????????
    taskPlanNewService.updateState(tenant, taskPlan.getUuid(), TaskPlanState.EFFECTIVE.name(), getSysOperateInfo());
  }

  private void adjustPublishDate(String tenant, TaskPlan taskPlan, List<Date> allPublishDate) throws BaasException {
    Date now = new Date();
    now = DateUtil.truncate(now, Calendar.MINUTE);
    //???????????????????????????
    TaskPlan taskPlanHistory = taskPlanNewService.get(tenant, taskPlan.getUuid());
    if (null == taskPlanHistory) {
      throw new BaasException("????????????????????????????????????");
    }
    List<Date> publishedDates = JSONUtil.safeToObject(taskPlanHistory.getPublishTaskDateCollect(),
        new TypeReference<ArrayList<Date>>() {
        });
    if (CollectionUtils.isNotEmpty(publishedDates)) {
      allPublishDate.removeAll(publishedDates);
      if (CollectionUtils.isNotEmpty(allPublishDate)) {
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????,?????????????????????
        Date maxPublishDate = publishedDates.stream().max(Date::compareTo).orElse(now);
        if (CronType.weekly.name().equals(taskPlan.getCycle()) || CronType.monthly.name().equals(taskPlan.getCycle())) {
          Date endDate = getEndDate(maxPublishDate, taskPlan);
          if (now.compareTo(endDate) < 0) {
            allPublishDate.clear();
          }
        }
      }
    }
  }

  private void beforePublish(String tenant, List<Date> allPublishDate, TaskPlan taskPlan) throws BaasException {
    if (CollectionUtils.isNotEmpty(allPublishDate)) {
      Date publishDate = allPublishDate.get(0);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(publishDate);
      calendar.add(Calendar.DATE, taskPlan.getAdvancePubDay() * (-1));
      calendar.add(Calendar.HOUR, taskPlan.getAdvancePubHour() * (-1));
      Date currentDate = new Date();
      currentDate = DateUtil.truncate(currentDate, Calendar.MINUTE);
      if (currentDate.compareTo(calendar.getTime()) < 0) {
        log.error("?????????????????????????????????????????????????????????id??????{}???????????????????????????{}", taskPlan.getUuid(), calendar.getTime());
        throw new BaasException("?????????????????????????????????????????????????????????id??????" + taskPlan.getUuid() + "???????????????????????????" + calendar.getTime());
      }
      if (currentDate.compareTo(taskPlan.getEndDate()) >= 0) {
        log.info("????????????????????????????????????????????????id??????{}", taskPlan.getUuid());
        throw new BaasException("?????????????????????????????????????????????????????????id??????" + taskPlan.getUuid() + "???????????????????????????" + calendar.getTime());
      }
      //???????????????????????????
      TaskPlan taskPlanHistory = taskPlanNewService.get(tenant, taskPlan.getUuid());
      if (null == taskPlanHistory) {
        throw new BaasException("????????????????????????????????????");
      }
      List<Date> publishedDates = JSONUtil.safeToObject(taskPlanHistory.getPublishTaskDateCollect(),
          new TypeReference<ArrayList<Date>>() {
          });
      if (CollectionUtils.isNotEmpty(publishedDates) && allPublishDate.stream().anyMatch(publishedDates::contains)) {
        throw new BaasException("????????????????????????????????????????????????");
      }
    } else {
      throw new BaasException("?????????????????????????????????");
    }
  }

  private List<Date> getAllPublishDate(TaskPlan taskPlan) throws Exception {
    List<Date> dateList = new ArrayList<>();
    Date now = new Date();
    now = DateUtil.truncate(now, Calendar.MINUTE);
    if (CronType.weekly.name().equals(taskPlan.getCycle())) {
      Date weekStartDate = getWeekStartDate(taskPlan);
      Date weekEndDate = getEndDate(weekStartDate, taskPlan);
      do {
        if (weekEndDate.compareTo(taskPlan.getEndDate()) <= 0 && weekStartDate.compareTo(now) >= 0 && weekStartDate.compareTo(taskPlan.getStartDate()) >= 0) {
          dateList.add(weekStartDate);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(weekStartDate);
        calendar.add(Calendar.DATE, 7);
        weekStartDate = calendar.getTime();
        weekEndDate = getEndDate(weekStartDate, taskPlan);
      } while (weekEndDate.compareTo(taskPlan.getEndDate()) <= 0);
    } else if (CronType.monthly.name().equals(taskPlan.getCycle())) {
      Date monthStartDate = getMonthStartDate(taskPlan);
      Date monthEndDate = getEndDate(monthStartDate, taskPlan);
      do {
        if (monthEndDate.compareTo(taskPlan.getEndDate()) <= 0 && monthStartDate.compareTo(now) >= 0 && monthStartDate.compareTo(taskPlan.getStartDate()) >= 0) {
          dateList.add(monthStartDate);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(monthStartDate);
        calendar.add(Calendar.MONTH, 1);
        monthStartDate = calendar.getTime();
        monthEndDate = getEndDate(monthStartDate, taskPlan);
      } while (monthEndDate.compareTo(taskPlan.getEndDate()) <= 0);
    } else if (CronType.single.name().equals(taskPlan.getCycle())) {
      if (taskPlan.getStartDate() != null && now.compareTo(taskPlan.getEndDate()) <= 0 && taskPlan.getStartDate().compareTo(now) >= 0) {
        dateList.add(taskPlan.getStartDate());
      }
    }
    return dateList;
  }

  private void publishShopTask(String tenant, List<TaskPlanLine> taskPlanLine, TaskPlan taskPlan, Date publishDate) throws Exception {
    // ??????taskPlanLine???????????????,????????????????????????????????????????????????????????????????????????????????????
    Map<String, List<TaskPlanLine>> taskCollect = taskPlanLine.stream()
        .filter(item -> StringUtils.isNotEmpty(item.getShop()))
        .collect(Collectors.groupingBy(TaskPlanLine::getShop));
    for (Map.Entry<String, List<TaskPlanLine>> entry : taskCollect.entrySet()) {
      String shop = entry.getKey();
      List<TaskPlanLine> taskLines = entry.getValue();
      // ????????????????????????
      List<ShopTask> shopTasks = buildShopTask(tenant, taskLines, taskPlan, shop,
          CronType.valueOf(taskPlan.getCycle()), publishDate);
      // ????????????
      publishTask(tenant, taskPlan.getUuid(), shop, shopTasks);
      // ????????????
      sendMessage(tenant, taskPlan, shopTasks);
    }
  }

  /**
   * ??????????????????
   *
   * @return ????????????
   */
  private List<String> getTenants() {
    List<TaskPlan> taskPlans = taskPlanNewService.listTenant();
    return CollectionUtils.isNotEmpty(taskPlans) ? taskPlans.stream().map(TaskPlan::getTenant).collect(Collectors.toList()) : new ArrayList<>();
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

  private List<ShopTask> buildShopTask(String tenant, List<TaskPlanLine> taskLines, TaskPlan taskPlan, String ShopId,
                                       CronType type, Date publishDate) {
    List<ShopTask> shopTasks = new ArrayList<>();
    taskLines.forEach(line -> {
      ShopTask shopTask = new ShopTask();
      // ------- ???????????????????????? -----------
      shopTask.setDescription(taskPlan.getDescription());
      shopTask.setWordNeeded(taskPlan.isWordNeeded());
      shopTask.setImageNeeded(taskPlan.isImageNeeded());
      // shopTask.setVideoNeeded();
      shopTask.setTemplateCls(taskPlan.getTemplateCls());
      // --------- ???????????????????????? ---------

      // ---------???????????????????????? ----------
      shopTask.setShop(ShopId);
      shopTask.setShopCode(line.getShopCode());
      shopTask.setShopName(line.getShopName());
      // shopTask.setShopTaskGroup(); // ???????????????
      // ---------???????????????????????? ----------

      // ------- ???????????????????????? -----------
      shopTask.setPlan(taskPlan.getUuid());
      shopTask.setPlanCode(taskPlan.getCode());
      shopTask.setPlanName(taskPlan.getName());
      shopTask.setPlanType(taskPlan.getType());
      shopTask.setPlanPeriod(getPlanPeriod(publishDate, type));
      shopTask.setPlanPeriodCode(shopTask.getPlanPeriod());
      shopTask.setName(taskPlan.getName());
      // shopTask.setPlanTime();
      Date endDate = getEndDate(publishDate, taskPlan);
      try {
        shopTask.setRemindTime(getRemindDate(taskPlan, endDate));
      } catch (ParseException e) {
        log.error("????????????????????????????????????????????????????????????????????????id??????{}", taskPlan.getUuid(), e);
        e.printStackTrace();
      }
      // shopTask.setPlanPeriodCode();
      shopTask.setPlanStartTime(DateUtil.truncate(publishDate, Calendar.MINUTE));
      shopTask.setPlanEndTime(DateUtil.truncate(endDate, Calendar.MINUTE));
      // ------- ???????????????????????? -----------
      // ------- ?????????????????????????????? ---------
      shopTask.setTaskGroup(line.getTaskGroupId());
      shopTask.setGroupName(line.getTaskGroupName());
      // ------- ???????????????????????????????????? ---------

      // ------- ?????????????????????-----------
      shopTask.setOperatorId(line.getAssigneeId());
      shopTask.setOperatorName(line.getAssignee());
      // ------- ???????????????????????????-----------

      // -------- ????????????????????? ------------
      shopTask.setCreateInfo(taskPlan.getCreateInfo());
      shopTask.setLastModifyInfo(taskPlan.getLastModifyInfo());
      // -------- ??????????????????????????? ---------

      List<ShopTaskLog> logs = new ArrayList<>();
      if (StringUtils.isNotEmpty(line.getTaskGroupId())) {
        List<TaskTemplate> taskTemplates = taskGroupService.queryByOwner(tenant, line.getTaskGroupId());
        if (CollectionUtils.isNotEmpty(taskTemplates)) {
          // ??????
          shopTask.setPoint(taskTemplates.stream()
              .map(TaskTemplate::getScore)
              .filter(Objects::nonNull)
              .reduce(BigDecimal.ZERO, BigDecimal::add));
          taskTemplates.forEach(taskTemplate -> {
            ShopTaskLog shopTaskLog = new ShopTaskLog();
            shopTaskLog.setName(taskTemplate.getName());
            shopTaskLog.setPoint(taskTemplate.getScore());
            shopTaskLog.setPointDesc(taskTemplate.getContent());
            shopTaskLog.setOperatorId(line.getAssigneeId());
            shopTaskLog.setOperatorName(line.getAssignee());
            shopTaskLog.setImageNeeded(taskTemplate.isImageNeeded());
            shopTaskLog.setWordNeeded(taskTemplate.isWordNeeded());
            shopTaskLog.setVideoNeeded(taskTemplate.isVideoNeeded());
            shopTaskLog.setNote(taskTemplate.getNote());
            shopTaskLog.setState(ShopTaskState.NOT_STARTED.name());
            logs.add(shopTaskLog);
          });
        }
      }
      // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????job????????????????????????job??????
      if (taskPlan.getAdvancePubDay() == 0 && taskPlan.getAdvancePubHour() == 0) {
        Date currentDate = new Date();
        currentDate = DateUtil.truncate(currentDate, Calendar.MINUTE);
        if (currentDate.compareTo(shopTask.getPlanStartTime()) >= 0 && currentDate.compareTo(shopTask.getPlanEndTime()) < 0) {
          shopTask.setState(ShopTaskState.UNFINISHED);
          if (CollectionUtils.isNotEmpty(logs)) {
            logs.forEach(log -> log.setState(ShopTaskState.UNFINISHED.name()));
          }
        }
      } else {
        shopTask.setState(ShopTaskState.NOT_STARTED);
      }
      shopTask.setLogs(logs);
      shopTasks.add(shopTask);
    });
    return shopTasks;
  }

  private String getPlanPeriod(Date publishDate, CronType type) {
    String planPeriod = "??????";
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
   * ??????????????????????????????<br>
   *
   * @return ????????????????????????
   */
  private static int getWeekOfDate() {
    int[] weekDays = {
        7, 1, 2, 3, 4, 5, 6};
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

  /**
   * ??????????????????????????????????????????????????????????????????????????????
   *
   * @param taskPlan ??????
   * @return ????????????
   */
  private Date getRemindDate(TaskPlan taskPlan, Date endDate) throws ParseException {
    //????????????????????????????????????????????????????????????????????????????????????????????????
    if (taskPlan.getAdvanceEndDay() == 0 && taskPlan.getAdvanceEndHour() == 0) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(endDate);
    calendar.add(Calendar.DATE, taskPlan.getAdvanceEndDay() * (-1));
    calendar.add(Calendar.HOUR, taskPlan.getAdvanceEndHour() * (-1));
    return DateUtil.truncate(calendar.getTime(), Calendar.MINUTE);
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
      log.error("taskPlan???????????????????????????taskPlan Id??????{}", taskPlan.getUuid());
      throw new BaasException("taskPlan????????????????????????, taskPlan Id???:" + taskPlan.getUuid());
    }
  }

  private void publishTask(String tenant, String plan, String shop, List<ShopTask> tasks) throws Exception {
    shopTaskService.batchSaveNew(tenant, plan, shop, tasks, getSysOperateInfo());
  }

  public void sendMessage(String tenant, TaskPlan taskPlan, List<ShopTask> shopTasks) {
    if (taskPlan.getAdvancePubDay() == 0 && taskPlan.getAdvancePubHour() == 0) {
      sendTaskStartMessage(tenant, taskPlan.getUuid(), shopTasks);
    } else {
      sendAdvancePubMessage(tenant, taskPlan.getUuid(), shopTasks);
    }
  }

  private void sendTaskStartMessage(String tenant, String plan, List<ShopTask> shopTasks) {
    if (CollectionUtils.isEmpty(shopTasks)) {
      return;
    }
    try {
      TaskMessageConfig config = baasConfigClient.getConfig(tenant, TaskMessageConfig.class);
      List<Message> messageList = new ArrayList<>();
      for (ShopTask line : shopTasks) {
        Message message = buildShopTaskStartMessage(tenant, plan, line, config.getCheckTaskPage());
        messageList.add(message);
      }
      MessageConfig messageConfig = baasConfigClient.getConfig(tenant, MessageConfig.class);
      if (MessageConfig.FMS.equals(messageConfig.getAppMessageVendor())){
        List<AppMessageSaveNewReq> reqs = ConverterUtil.convert(messageList, MessageConvertUtil.MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ);
        reqs.forEach(req->{req.setOperateInfo(this.getSysOperateInfo());});
        BaasResponse<Void> response = fmsClient.batchSave(tenant, shopTasks.get(0).getOrgId(), reqs);
        if (!response.isSuccess()) {
          throw new BaasException("???????????????code???{}???msg???{}", response.getCode(), response.getMsg());
        }
      } else {
        messageService.batchCreate(tenant, messageList, getSysOperateInfo());
      }
    } catch (BaasException e) {
      log.error("???????????????????????????{}", JsonUtil.objectToJson(e));
    }
  }

  private Message buildShopTaskStartMessage(String tenant, String source, ShopTask shopTask, String checkTaskPage) {
    Message message = new Message();
    message.setTenant(tenant);
    String shop = "*";
    message.setShop(shop);
    message.setShopCode(shop);
    message.setShopName(shop);
    message.setAction(MessageAction.PAGE);
    message.setActionInfo(buildUrl(checkTaskPage, shopTask.getPlan(), shopTask.getPlanPeriod()));
    message.setTitle("????????????");
    message.setType(MessageType.ALERT);
    message.setTag(MessageTag.????????????);
    message.setSource(source);
    Map<MessageContentKey, String> content = new LinkedHashMap<>();
    content.put(MessageContentKey.TEXT, "???????????????????????????:" + shopTask.getName());
    message.setContent(content);
    message.setUserId(shopTask.getOperatorId());
    return message;
  }

  private void sendAdvancePubMessage(String tenant, String plan, List<ShopTask> shopTasks) {
    if (CollectionUtils.isEmpty(shopTasks)) {
      return;
    }
    try {
      TaskMessageConfig config = baasConfigClient.getConfig(tenant, TaskMessageConfig.class);
      List<Message> messageList = new ArrayList<>();
      for (ShopTask line : shopTasks) {
        Message message = buildCheckMessage(tenant, plan, line, config.getCheckTaskPage());
        messageList.add(message);
      }
      MessageConfig messageConfig = baasConfigClient.getConfig(tenant, MessageConfig.class);
      if (MessageConfig.FMS.equals(messageConfig.getAppMessageVendor())){
        List<AppMessageSaveNewReq> reqs = ConverterUtil.convert(messageList, MessageConvertUtil.MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ);
        reqs.forEach(req->{req.setOperateInfo(this.getSysOperateInfo());});
        BaasResponse<Void> response = fmsClient.batchSave(tenant, shopTasks.get(0).getOrgId(), reqs);
        if (!response.isSuccess()) {
          throw new BaasException("???????????????code???{}???msg???{}", response.getCode(), response.getMsg());
        }
      } else {
        messageService.batchCreate(tenant, messageList, getSysOperateInfo());
      }
    } catch (BaasException e) {
      log.error("???????????????????????????{}", JsonUtil.objectToJson(e));
    }
  }

  private Message buildCheckMessage(String tenant, String source, ShopTask shopTask, String checkTaskPage) {
    Message message = new Message();
    message.setTenant(tenant);
    String shop = "*";
    message.setShop(shop);
    message.setShopCode(shop);
    message.setShopName(shop);
    message.setAction(MessageAction.PAGE);
    message.setActionInfo(buildUrl(checkTaskPage, shopTask.getPlan(), shopTask.getPlanPeriod()));
    message.setTitle("????????????");
    message.setType(MessageType.ALERT);
    message.setTag(MessageTag.????????????);
    message.setSource(source);
    Map<MessageContentKey, String> content = new LinkedHashMap<>();
    content.put(MessageContentKey.TEXT, "???????????????????????????????????????:" + shopTask.getName());
    message.setContent(content);
    message.setUserId(shopTask.getOperatorId());
    return message;
  }

  // planPeriod ??? planPeriod??????
  private String buildUrl(String page, String plan, String planPeriodCode) {
    return page + "?plan=" + plan + "&periodCode=" + planPeriodCode;
  }

  private int getEffectiveDayOfMonth(int monthOfDay) {
    return Math.min(monthOfDay, getCurrentMonthDay());
  }

  /**
   * ??????????????? ??????
   */
  public static int getCurrentMonthDay() {
    Calendar a = Calendar.getInstance();
    a.set(Calendar.DATE, 1);
    a.roll(Calendar.DATE, -1);
    return a.get(Calendar.DATE);
  }

}
