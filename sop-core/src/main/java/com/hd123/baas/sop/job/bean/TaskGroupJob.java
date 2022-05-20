package com.hd123.baas.sop.job.bean;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.config.DailyTaskConfig;
import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.baas.sop.service.api.task.ShopTaskGroup;
import com.hd123.baas.sop.service.api.task.ShopTaskGroupService;
import com.hd123.baas.sop.service.api.task.ShopTaskGroupState;
import com.hd123.baas.sop.service.api.task.ShopTaskService;
import com.hd123.baas.sop.service.api.task.ShopTaskState;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroup;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupService;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupType;
import com.hd123.baas.sop.service.api.taskplan.TaskPlan;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanService;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanState;
import com.hd123.baas.sop.job.entity.TimedJob;
import com.hd123.baas.sop.job.timed.ShopTaskGroupRemindJobParam;
import com.hd123.baas.sop.job.timed.ShopTaskRemindJobParam;
import com.hd123.baas.sop.job.timed.TimedJobService;
import com.hd123.baas.sop.job.timed.executor.ShopTaskGroupRemindJobExecutor;
import com.hd123.baas.sop.job.timed.executor.ShopTaskRemindJobExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.baas.sop.utils.entity.CronType;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.CronExpression;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author zhengzewang on 2020/11/5.
 */
@DisallowConcurrentExecution
@Slf4j
public class TaskGroupJob implements Job {

  public static final String TENANT = "tenant";
  public static final String GROUP_ID = "groupId";

  @Autowired
  private TaskGroupService taskGroupService;

  @Autowired
  private TaskPlanService taskPlanService;

  @Autowired
  private ShopTaskGroupService shopTaskGroupService;

  @Autowired
  private StoreService storeService;

  @Autowired
  private ShopTaskService shopTaskService;

  @Autowired
  private TimedJobService timedJobService;

  @Autowired
  private BaasConfigClient configClient;

  @SneakyThrows
  @Override
  @Tx
  public void execute(JobExecutionContext context) throws JobExecutionException {
    String tenant = context.getJobDetail().getJobDataMap().getString(TENANT);
    String groupId = context.getJobDetail().getJobDataMap().getString(GROUP_ID);
    log.info("门店任务执行:{} {}", tenant, groupId);
    boolean effective = false;
    List<String> shops = taskGroupService.getRelateShops(tenant, groupId);
    if (CollectionUtils.isNotEmpty(shops)) {
      for (String shop : shops) {
        try {
          ShopTaskGroup shopTaskGroup = shopTaskGroupService.getByShopAndGroupIdAndPlanDate(tenant, shop, groupId,
              initDateByDay());
          if (shopTaskGroup == null) {
            log.info("开始创建新门店任务组,groupId:{},shop:{}", groupId, shop);
            createNewShopTaskGroup(tenant, groupId, shop);
          } else {
            log.info("开始更新新门店任务组,shopTaskGroupId:{},shop:{}", shopTaskGroup.getUuid(), shop);
            updateShopTaskGroup(tenant, groupId, shopTaskGroup, shop);
          }
          effective = true;
        } catch (Exception e) {
          log.error("门店{}生成日结任务清单失败，group_id:{}", shop, groupId, e);
        }
      }
      if (effective) {
        setTaskPlanEffectiveByGroupId(tenant, groupId);
        deleteSingleTaskPlan(tenant, groupId);
      }
    }
  }

  private void deleteSingleTaskPlan(String tenant, String groupId) throws BaasException {
    List<TaskPlan> taskPlans = taskPlanService.list(tenant, groupId);
    for (TaskPlan taskPlan : taskPlans) {
      if (CronType.single.name().equals(taskPlan.getPlanTime())) {
        log.info("该任务为单次任务，任务详情:{}", BaasJSONUtil.safeToJson(taskPlan));
        taskPlanService.makeOverdue(taskPlan.getTenant(), taskPlan.getTaskGroup(), taskPlan.getUuid());
      }
    }
  }

  private void setTaskPlanEffectiveByGroupId(String tenant, String groupId) {
    taskPlanService.effectiveByGroupId(tenant, groupId);
  }

  private void updateShopTaskGroup(String tenant, String groupId, ShopTaskGroup shopTaskGroup, String shop)
      throws BaasException, ParseException {
    DailyTaskConfig config = configClient.getConfig(tenant, DailyTaskConfig.class);

    if (config == null || !config.ignoreShopTaskGroupState) {
      if (!ShopTaskGroupState.UNFINISHED.name().equals(shopTaskGroup.getState().name())) {
        log.info("门店任务组以完成或者以过期，忽略，uuid:{}", shopTaskGroup.getUuid());
        return;
      }
    }

    List<ShopTask> shopTasks = shopTaskService.getByShopTaskGroupId(tenant, shopTaskGroup.getUuid());
    for (ShopTask shopTask : shopTasks) {
      if (!ShopTaskState.FINISHED.name().equals(shopTask.getState().name())) {
        TaskPlan taskPlan = taskPlanService.get(tenant, shopTask.getPlan());
        if (taskPlan == null) {
          log.info("任务计划不存在，且任尚未完成，删除该任务，shopTaskUUID:{}", shopTask.getUuid());
          shopTaskService.delete(tenant, shopTask.getUuid());
        }
      }
    }

    // 判断是否需要更新任务组最后完成时间
    TaskGroup taskGroup = taskGroupService.get(tenant, shopTaskGroup.getTaskGroup());
    if (taskGroup.getType().equals(TaskGroupType.DAILY)
        && !decodeTime(config.getEarliestFinishTime()).equals(shopTaskGroup.getEarliestFinishTime())) {
      log.info("日结任务组最早完成时间变化");
      shopTaskGroupService.modifyEarliestFinishTime(tenant, shopTaskGroup.getUuid(),
          decodeTime(config.getEarliestFinishTime()));
    }

    List<TaskPlan> taskPlans = taskPlanService.list(tenant, shopTaskGroup.getTaskGroup());
    taskPlans = taskPlans.stream()
        .filter(i -> !i.getState().equals(TaskPlanState.OVERDUE))
        .collect(Collectors.toList());
    for (TaskPlan taskPlan : taskPlans) {
      if (!judgeDateRange(taskPlan)) {
        continue;
      }
      if (!judgePlanTime(taskPlan.getPlanTime())) {
        continue;
      }

      Store store = getStore(tenant, shop);
      ShopTask newShopTask = buildShopTask(taskPlan, store, shopTaskGroup);
      if (TaskPlanState.UN_EFFECTIVE.name().equals(taskPlan.getState().name())) {
        log.info("任务计划未生效，重新生成任务");
        ShopTask oldShopTask = shopTaskService.getByShopTaskGroupIdAndTaskPlanId(tenant, shopTaskGroup.getUuid(),
            taskPlan.getUuid());
        if (oldShopTask == null) {
          log.info("插入新任务");
          shopTaskService.batchSaveNew(tenant, newShopTask);
        }
        if (oldShopTask != null) {
          if (!ShopTaskState.FINISHED.name().equals(oldShopTask.getState().name())) {
            log.info("删除旧任务");
            shopTaskService.delete(tenant, oldShopTask.getUuid());
          }
          log.info("插入新任务");
          shopTaskService.batchSaveNew(tenant, newShopTask);
        }
        if (taskGroup.getType().name().equals(TaskGroupType.USUAL.name())) {
          log.info("提交日常任务提醒");
          submitShopTaskRemindJob(tenant, newShopTask);
        }

      }
    }

  }

  private void createNewShopTaskGroup(String tenant, String groupId, String shop) throws BaasException, ParseException {
    Store store = getStore(tenant, shop);
    TaskGroup taskGroup = taskGroupService.get(tenant, groupId);
    ShopTaskGroup shopTaskGroup = buildShopTaskGroup(taskGroup, store);
    List<TaskPlan> taskPlans = taskPlanService.list(tenant, groupId);
    log.info("taskPlans1 : {}", taskPlans);
    taskPlans = taskPlans.stream()
        .filter(i -> !i.getState().equals(TaskPlanState.OVERDUE))
        .collect(Collectors.toList());
    log.info("taskPlans2 : {}", taskPlans);
    List<ShopTask> shopTasks = buildShopTasks(taskPlans, store, shopTaskGroup);
    log.info("shopTasks :{}", shopTasks);
    if (CollectionUtils.isEmpty(shopTasks)) {
      return;
    }
    String lastShopTaskGroupId = shopTaskGroupService.checkLast(tenant, shop, groupId);
    shopTaskGroupService.saveNew(tenant, shopTaskGroup);
    if (lastShopTaskGroupId != null) {
      shopTaskService.batchCheckLastOne(tenant, shop, groupId, lastShopTaskGroupId);
    }
    shopTaskService.batchInsert(tenant, shopTasks);
    if (TaskGroupType.DAILY.name().equals(taskGroup.getType().name())) {
      log.info("提交日结门店任务组提醒job");
      submitShopTaskGroupRemindJob(tenant, shopTaskGroup);
    } else {
      for (ShopTask shopTask : shopTasks) {
        log.info("提交日常门店任务提醒job");
        submitShopTaskRemindJob(tenant, shopTask);
      }
    }
  }

  private void submitShopTaskRemindJob(String tenant, ShopTask shopTask) throws BaasException {
    Date remindTime = shopTask.getRemindTime();
    if (remindTime == null) {
      return;
    }
    String uuid = shopTask.getUuid();
    TimedJob job = new TimedJob();
    ShopTaskRemindJobParam param = new ShopTaskRemindJobParam();
    param.setTenant(tenant);
    param.setShopTaskId(uuid);
    job.setParams(BaasJSONUtil.safeToJson(param));
    job.setExpectedRunTime(remindTime);
    job.setCallbackBeanName(ShopTaskRemindJobExecutor.BEAN_NAME);
    job.setTranId(ShopTaskRemindJobExecutor.buildTranId());
    job.setInterval("60");
    job.setUuid(UUID.randomUUID().toString());
    timedJobService.submit(job);
  }

  private void submitShopTaskGroupRemindJob(String tenant, ShopTaskGroup shopTaskGroup) throws BaasException {
    Date remindTime = shopTaskGroup.getRemindTime();
    if (remindTime == null) {
      return;
    }
    ShopTaskGroupRemindJobParam param = new ShopTaskGroupRemindJobParam();
    param.setTenant(tenant);
    param.setShopTaskGroupId(shopTaskGroup.getUuid());
    TimedJob job = new TimedJob();
    job.setTranId(ShopTaskGroupRemindJobExecutor.buildTranId());
    job.setInterval("60");
    job.setCallbackBeanName(ShopTaskGroupRemindJobExecutor.BEAN_NAME);
    job.setExpectedRunTime(remindTime);
    job.setParams(BaasJSONUtil.safeToJson(param));
    job.setUuid(UUID.randomUUID().toString());
    timedJobService.submit(job);
  }

  private Store getStore(String tenant, String shop) throws BaasException {
    StoreFilter storeFilter = new StoreFilter();
    List<String> list = new ArrayList<>();
    list.add(shop);
    storeFilter.setIdIn(list);
    QueryResult<Store> query = storeService.query(tenant, storeFilter);
    if (CollectionUtils.isEmpty(query.getRecords())) {
      throw new BaasException("门店异常");
    }
    Store store = query.getRecords().get(0);
    return store;
  }

  private List<ShopTask> buildShopTasks(List<TaskPlan> taskPlans, Store store, ShopTaskGroup shopTaskGroup)
      throws ParseException, BaasException {
    if (CollectionUtils.isEmpty(taskPlans)) {
      return Collections.emptyList();
    }
    List<ShopTask> shopTasks = new ArrayList<>();
    for (TaskPlan taskPlan : taskPlans) {
      if (!judgeDateRange(taskPlan)) {
        log.info("任务计划未开始或已结束 忽略该 {} 任务计划", taskPlan);
        continue;
      }
      if (!judgePlanTime(taskPlan.getPlanTime())) {
        log.info("不在计划时间列表中 忽略该 {} 任务计划", taskPlan);
        continue;
      }
      ShopTask item = buildShopTask(taskPlan, store, shopTaskGroup);
      shopTasks.add(item);
    }
    return shopTasks;
  }

  private boolean judgeDateRange(TaskPlan taskPlan) throws BaasException {
    Date now = new Date();
    Date startDate = taskPlan.getStartDate();
    Date endDate = taskPlan.getEndDate();
    boolean flag = true;
    if (startDate != null) {
      flag = now.after(startDate);
    }
    if (flag && endDate != null) {
      flag = now.before(endDate);
      if (!flag) {
        taskPlanService.makeOverdue(taskPlan.getTenant(), taskPlan.getTaskGroup(), taskPlan.getUuid());
      }
    }
    return flag;
  }

  public boolean judgePlanTime(String planTime) throws ParseException {
    if (planTime == null) {
      return true;
    }
    if (planTime.equals(CronType.single.name()) || planTime.equals("0 0 0 * * ?")) {
      return true;
    }
    CronExpression cronExpression = new CronExpression(planTime);
    boolean resCron = cronExpression.isSatisfiedBy(initDateByDay());
    return resCron;
  }

  private ShopTask buildShopTask(TaskPlan taskPlan, Store store, ShopTaskGroup shopTaskGroup) {
    ShopTask shopTask = new ShopTask();
    shopTask.setTenant(taskPlan.getTenant());
    shopTask.setTemplateCls(taskPlan.getTemplateCls());
    shopTask.setState(ShopTaskState.UNFINISHED);
    shopTask.setPlanTime(initDateByDay());
    shopTask.setPlan(taskPlan.getUuid());
    shopTask.setShopTaskGroup(shopTaskGroup.getUuid());
    shopTask.setTaskGroup(taskPlan.getTaskGroup());
    shopTask.setGroupName(shopTaskGroup.getGroupName());
    shopTask.setShop(shopTaskGroup.getShop());
    shopTask.setShopCode(shopTaskGroup.getShopCode());
    shopTask.setShopName(shopTaskGroup.getShopName());
    shopTask.setOrgId(shopTaskGroup.getOrgId());
    shopTask.setRemindTime(decodeRemindTime(taskPlan.getRemindTime()));
    shopTask.setName(taskPlan.getName());
    shopTask.setWordNeeded(taskPlan.isWordNeeded());
    shopTask.setImageNeeded(taskPlan.isImageNeeded());
    shopTask.setDescription(taskPlan.getDescription());
    shopTask.setVersion(1);
    shopTask.setUuid(UUID.randomUUID().toString());
    shopTask.setCreateInfo(SopUtils.getSysOperateInfo());
    shopTask.setLastModifyInfo(SopUtils.getSysOperateInfo());
    shopTask.setSort(taskPlan.getSort());
    return shopTask;
  }

  private Date decodeRemindTime(String time) {
    if (time == null) {
      return initDateByDay();
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    String[] split = time.split(":");

    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));
    calendar.set(Calendar.MINUTE, Integer.parseInt(split[1]));
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  private ShopTaskGroup buildShopTaskGroup(TaskGroup taskGroup, Store store) {
    DailyTaskConfig config = configClient.getConfig(taskGroup.getTenant(), DailyTaskConfig.class);
    ShopTaskGroup result = new ShopTaskGroup();
    result.setTenant(taskGroup.getTenant());
    result.setType(taskGroup.getType());
    result.setState(ShopTaskGroupState.UNFINISHED);
    result.setShop(store.getId());
    result.setShopCode(store.getCode());
    result.setShopName(store.getName());
    result.setPlanTime(initDateByDay());
    result.setRemindTime(decodeRemindTime(taskGroup.getRemindTime()));
    result.setTaskGroup(taskGroup.getUuid());
    result.setGroupName(taskGroup.getName());
    result.setVersion(1L);
    result.setUuid(UUID.randomUUID().toString());
    result.setCreateInfo(SopUtils.getSysOperateInfo());
    result.setLastModifyInfo(SopUtils.getSysOperateInfo());
    result.setOrgId(taskGroup.getOrgId());
    if (result.getType().name().equals(TaskGroupType.DAILY.name())) {
      result.setEarliestFinishTime(decodeTime(config.getEarliestFinishTime()));
    }
    return result;
  }

  private Date decodeTime(String time) {
    if (time == null) {
      return initDateByDay();
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    String[] split = time.split(":");

    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));
    calendar.set(Calendar.MINUTE, Integer.parseInt(split[1]));
    if (split.length > 2) {
      calendar.set(Calendar.SECOND, Integer.parseInt(split[2]));
    } else {
      calendar.set(Calendar.SECOND, 0);
    }
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  public Date initDateByDay() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

}
