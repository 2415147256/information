package com.hd123.baas.sop.service.impl.taskplan;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.taskplan.*;
import com.hd123.baas.sop.service.dao.task.ShopTaskDaoBof;
import com.hd123.baas.sop.service.dao.taskplan.TaskPlanDaoBof;
import com.hd123.baas.sop.service.dao.taskplan.TaskPlanItemDaoBof;
import com.hd123.baas.sop.service.dao.taskplan.TaskPlanLineDaoBof;
import com.hd123.baas.sop.service.impl.price.BillNumberMgr;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.assignable.AssignableTaskPlanMsg;
import com.hd123.baas.sop.evcall.exector.assignable.AssignableTaskPlanPublishEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.shoptask.PlanAction;
import com.hd123.baas.sop.evcall.exector.shoptask.PlanStateChangeEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.shoptask.PlanStateChangeMsg;
import com.hd123.baas.sop.job.bean.TaskPlanAssignableJob;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.entity.CronType;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.BaasStatus;
import com.qianfan123.baas.common.util.JSONUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author guyahui
 * @date 2021/5/6 19:47
 */
@Service
@Slf4j
public class TaskPlanNewServiceImpl implements TaskPlanNewService {

  public static final Integer ERROR_CODE = 50015;

  @Autowired
  private TaskPlanDaoBof taskPlanDao;
  @Autowired
  private TaskPlanLineDaoBof taskPlanLineDao;
  @Autowired
  private BillNumberMgr billNumberMgr;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private TaskPlanItemDaoBof taskPlanItemDao;
  @Autowired
  private TaskPlanAssignableJob taskPlanAssignableJob;
  @Autowired
  private ShopTaskDaoBof shopTaskDao;

  private static final SimpleDateFormat YYMMDD_SPACE = new SimpleDateFormat("yyyy-MM-dd ");
  private static final SimpleDateFormat YYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Override
  @Tx
  public String saveNew(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(taskPlan.getName(), "name");
    beforeCheck(taskPlan);
    if (StringUtils.isEmpty(taskPlan.getUuid())) {
      taskPlan.setUuid(IdGenUtils.buildRdUuid());
    }
    taskPlan.setCode(generateTaskPlanCode(tenant));
    taskPlanDao.insert(tenant, taskPlan, operateInfo);
    if (CollectionUtils.isNotEmpty(taskPlan.getLines())) {
      taskPlan.getLines().forEach(item -> item.setOwner(taskPlan.getUuid()));
      taskPlanLineDao.batchInsert(tenant, taskPlan.getLines());
    }
    taskPlan.setCreateInfo(operateInfo);
    return taskPlan.getUuid();
  }

  private void beforeCheck(TaskPlan taskPlan) throws BaasException {
    Assert.notNull(taskPlan, "taskPlan");
    Assert.hasText(taskPlan.getCycle(), "周期cycle");
    Assert.notNull(taskPlan.getEndDate());
    Calendar calendar = Calendar.getInstance();
    //对于单次任务，实际生效的临期提醒时间不能早于计划开始时间
    if (CronType.single.name().equals(taskPlan.getCycle())) {
      Date endDate = taskPlan.getEndDate();
      calendar.setTime(endDate);
      calendar.add(Calendar.DATE, taskPlan.getAdvanceEndDay() * (-1));
      calendar.add(Calendar.HOUR, taskPlan.getAdvanceEndHour() * (-1));
      if (calendar.getTime().compareTo(taskPlan.getStartDate()) < 0) {
        throw new BaasException("实际生效的临期提醒时间不能早于计划开始时间");
      }
      if (calendar.getTime().compareTo(taskPlan.getEndDate()) > 0) {
        throw new BaasException("实际生效的临期提醒时间不能晚于计划结束时间");
      }
    } else if (CronType.weekly.name().equals(taskPlan.getCycle())) {
      Date weekStartDate = getWeekStartDate(taskPlan);
      calendar.setTime(weekStartDate);
      calendar.add(Calendar.DATE, taskPlan.getValidityDays());
      Date weekEndDate = calendar.getTime();
      calendar.setTime(weekEndDate);
      calendar.add(Calendar.DATE, taskPlan.getAdvanceEndDay() * (-1));
      calendar.add(Calendar.HOUR, taskPlan.getAdvanceEndHour() * (-1));
      Date reminderDate = calendar.getTime();
      if (reminderDate.compareTo(weekStartDate) < 0) {
        throw new BaasException("实际生效的临期提醒时间不能早于计划开始时间");
      }
      if (reminderDate.compareTo(weekEndDate) > 0) {
        throw new BaasException("实际生效的临期提醒时间不能晚于计划结束时间");
      }
    } else if (CronType.monthly.name().equals(taskPlan.getCycle())) {
      Date monthStartDate = getMonthStartDate(taskPlan);
      calendar.setTime(monthStartDate);
      calendar.add(Calendar.DATE, taskPlan.getValidityDays());
      Date weekEndDate = calendar.getTime();
      calendar.setTime(weekEndDate);
      calendar.add(Calendar.DATE, taskPlan.getAdvanceEndDay() * (-1));
      calendar.add(Calendar.HOUR, taskPlan.getAdvanceEndHour() * (-1));
      Date reminderDate = calendar.getTime();
      if (reminderDate.compareTo(monthStartDate) < 0) {
        throw new BaasException("实际生效的临期提醒时间不能早于计划开始时间");
      }
      if (reminderDate.compareTo(weekEndDate) > 0) {
        throw new BaasException("实际生效的临期提醒时间不能晚于计划结束时间");
      }
    } else {
      throw new BaasException("不支持的周期类型");
    }
  }

  @Override
  public TaskPlan get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    return taskPlanDao.get(tenant, uuid);
  }

  @Override
  public QueryResult<TaskPlan> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(TaskPlan.Queries.TENANT, Cop.EQUALS, tenant);
    return taskPlanDao.query(tenant, qd);
  }

  @Override
  @Tx
  public void delete(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    // 删除任务计划
    taskPlanDao.deleteByUuid(tenant, uuid);
    // 删除任务计划下对应的巡检关系
    taskPlanLineDao.deleteByOwner(tenant, uuid);
  }

  @Override
  public List<TaskPlanLine> listTaskPlanLineByOwner(String tenant, String owner) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(owner, "owner");

    return taskPlanLineDao.list(tenant, owner);
  }

  @Override
  public List<TaskPlanLine> listTaskPlanLineByOwner(String tenant, List<String> owners) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(owners, "owners");

    return taskPlanLineDao.listByOwners(tenant, owners);
  }

  @Override
  @Tx
  public String saveModify(String tenant, TaskPlan taskPlanModifyReq, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(taskPlanModifyReq.getUuid(), "uuid");
    // 已发布和未发布执行的修改逻辑分开
    String uuid = taskPlanModifyReq.getUuid();
    TaskPlan taskPlanNeedModify = taskPlanDao.get(tenant, uuid);
    if (taskPlanNeedModify == null) {
      throw new BaasException("要修改的计划任务不存在,uuid为：" + uuid);
    }
    beforeCheck(taskPlanModifyReq);
    // 已发布,已发布的内容只允许修改超时提醒时间
    if (TaskPlanState.EFFECTIVE.equals(taskPlanNeedModify.getState())) {
      // 直接修改提醒时间即可
      taskPlanDao.updateAdvanceEndDate(tenant, uuid, taskPlanModifyReq.getAdvanceEndDay(), taskPlanModifyReq.getAdvanceEndHour(), operateInfo);
    } else if (TaskPlanState.UN_EFFECTIVE.equals(taskPlanNeedModify.getState())) {

      // 未发布的内容允许修改除code、状态之外的所有内容
      taskPlanModifyReq.setCode(taskPlanNeedModify.getCode());
      taskPlanModifyReq.setState(TaskPlanState.UN_EFFECTIVE);
      taskPlanModifyReq.setCreateInfo(taskPlanNeedModify.getCreateInfo());
      taskPlanDao.updateTaskPlan(tenant, taskPlanModifyReq, operateInfo);
      // 首先删除uuid对应的taskPlanLine的记录
      taskPlanLineDao.deleteByOwner(tenant, uuid);
      if (CollectionUtils.isNotEmpty(taskPlanModifyReq.getLines())) {
        taskPlanModifyReq.getLines().forEach(item -> item.setOwner(uuid));
        taskPlanLineDao.batchInsert(tenant, taskPlanModifyReq.getLines());
      }
    }
    return uuid;
  }

  @Override
  public QueryResult<TaskPlanLine> queryTaskPlanLine(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(TaskPlanLine.Queries.TENANT, Cop.EQUALS, tenant);
    return taskPlanLineDao.query(tenant, qd);
  }

  @Override
  @Tx
  public void terminate(String tenant, String uuid, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    taskPlanDao.updateState(tenant, uuid, TaskPlanState.TERMINATED.name(), operateInfo);
    // 终止后通知终止
    PlanStateChangeMsg msg = new PlanStateChangeMsg();
    msg.setTenant(tenant);
    msg.setOperateInfo(operateInfo);
    msg.setAction(PlanAction.TERMINATE);
    msg.setPlan(uuid);
    msg.setTraceId(IdGenUtils.buildRdUuid());
    publisher.publishForNormal(PlanStateChangeEvCallExecutor.PLAN_STATE_CHANGE_EXECUTOR_ID, msg);
  }

  @Override
  @Tx
  public void updateState(String tenant, String uuid, String state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.hasText(state, "state");
    taskPlanDao.updateState(tenant, uuid, state, operateInfo);
  }

  @Override
  public void updatePublishTaskDateCollect(String tenant, String uuid, String publishTaskDateCollect) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.hasText(publishTaskDateCollect, "publishTaskDateCollect");

    taskPlanDao.updatePublishTaskDateCollect(tenant, uuid, publishTaskDateCollect);
  }

  @Override
  public List<TaskPlan> listTenant() {
    return taskPlanDao.listTenant();
  }

  @Override
  @Tx
  public String saveAssignableTaskPlan(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    if (StringUtils.isEmpty(taskPlan.getUuid())) {
      taskPlan.setUuid(IdGenUtils.buildRdUuid());
    }
    taskPlan.setCode(generateAssignableTaskPlanCode(tenant));
    taskPlan.setState(TaskPlanState.UN_EFFECTIVE);
    taskPlanDao.insert(tenant, taskPlan, operateInfo);
    // 保存明细item、line
    List<TaskPlanLine> taskPlanLines = new ArrayList<>();
    List<TaskPlanItem> taskPlanItems = new ArrayList<>();
    buildItemAndLine(tenant, taskPlan, taskPlanItems, taskPlanLines);
    if (CollectionUtils.isNotEmpty(taskPlanItems)) {
      taskPlanItemDao.batchInsert(tenant, taskPlanItems);
    }
    if (CollectionUtils.isNotEmpty(taskPlanLines)) {
      taskPlanLineDao.batchInsert(tenant, taskPlanLines);
    }
    return taskPlan.getUuid();
  }

  @Override
  @Tx
  public String saveModifyAssignableTaskPlan(String tenant, TaskPlan taskPlanModifyReq, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(taskPlanModifyReq.getUuid(), "uuid");
    // 未发布不能进行编辑
    String uuid = taskPlanModifyReq.getUuid();
    TaskPlan taskPlanNeedModify = taskPlanDao.get(tenant, uuid);
    if (taskPlanNeedModify == null) {
      throw new BaasException("要修改的计划任务不存在,uuid为：" + uuid);
    }
    if (TaskPlanState.UN_EFFECTIVE != taskPlanNeedModify.getState()) {
      throw new BaasException("该任务计划没有处于未发布状态，不能修改" + uuid);
    }
    // 不允许修改的内容
    taskPlanModifyReq.setCode(taskPlanNeedModify.getCode());
    taskPlanModifyReq.setState(TaskPlanState.UN_EFFECTIVE);
    taskPlanModifyReq.setCreateInfo(taskPlanNeedModify.getCreateInfo());
    taskPlanDao.updateTaskPlan(tenant, taskPlanModifyReq, operateInfo);
    // 首先删除uuid对应的taskPlanItem的记录和taskPlanLine的记录
    taskPlanItemDao.deleteByOwner(tenant, uuid);
    taskPlanLineDao.deleteByOwner(tenant, uuid);
    List<TaskPlanLine> lines = new ArrayList<>();
    List<TaskPlanItem> items = new ArrayList<>();
    buildItemAndLine(tenant, taskPlanModifyReq, items, lines);
    if (CollectionUtils.isNotEmpty(items)) {
      taskPlanItemDao.batchInsert(tenant, items);
    }
    if (CollectionUtils.isNotEmpty(lines)) {
      taskPlanLineDao.batchInsert(tenant, lines);
    }
    return uuid;
  }

  @Override
  public List<TaskPlanItem> listTaskPlanItemByTaskPlanIds(String tenant, List<String> taskPlanIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(taskPlanIds, "taskPlanIds");

    return taskPlanItemDao.listByOwners(tenant, taskPlanIds);
  }

  @Tx
  @Override
  public void deleteAssignableTaskPlan(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    // 删除普通任务计划
    taskPlanDao.deleteByUuid(tenant, uuid);
    // 删除普通任务计划item明细
    taskPlanItemDao.deleteByOwner(tenant, uuid);
    // 删除普通任务计划line明细
    taskPlanLineDao.deleteByOwner(tenant, uuid);
  }

  @Override
  public void publishAssignableTaskPlan(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText("uuid", uuid);
    TaskPlan taskPlan = get(tenant, uuid);
    List<Date> allPublishDate = taskPlanAssignableJob.getAllPublishDate(tenant, taskPlan);
    if (CollectionUtils.isEmpty(allPublishDate) || allPublishDate.stream()
        .noneMatch(item -> taskPlanAssignableJob.getPublishEffectiveEndDate(tenant).compareTo(item) >= 0
            && new Date().compareTo(item) <= 0 && taskPlan.getStartDate() != null
            && taskPlan.getStartDate().compareTo(item) <= 0)) {
      throw new BaasException(new BaasStatus(ERROR_CODE, "立刻发布产生的任务为空，不能执行发布！"));
    }
    taskPlan.setLastModifyInfo(operateInfo);
    publishAssignableTaskPlan(tenant, taskPlan);
  }

  @Override
  @Tx
  public void terminateAssignableTaskPlan(String tenant, String uuid, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText("uuid", uuid);
    taskPlanDao.updateState(tenant, uuid, TaskPlanState.TERMINATED.name(), operateInfo);
    // 将未完成的任务置为终止
    shopTaskDao.terminateByPlan(tenant, uuid, operateInfo);
  }

  @Override
  @Tx
  public String saveModifyAndPublish(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException {
    String uuid = saveModifyAssignableTaskPlan(tenant, taskPlan, operateInfo);
    publishAssignableTaskPlan(tenant, uuid, operateInfo);
    return uuid;
  }

  @Override
  @Tx
  public String saveNewAndPublish(String tenant, TaskPlan taskPlan, OperateInfo operateInfo) throws BaasException {
    String uuid = saveAssignableTaskPlan(tenant, taskPlan, operateInfo);
    publishAssignableTaskPlan(tenant, uuid, operateInfo);
    return uuid;
  }

  @Override
  public List<TaskPlan> listByUuids(String tenant, List<String> planIds) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(planIds, "planIds");

    return taskPlanDao.listByUuids(tenant, planIds);
  }

  /**
   * 生成计划代码
   *
   * @return
   */
  @SneakyThrows
  private String generateTaskPlanCode(String tenant) {
    String code = billNumberMgr.generateTaskPlanCode(tenant);
    if (StringUtils.isEmpty(code) || code.length() < 8) {
      throw new BaasException("任务计划code生成失败");
    }
    return code.substring(1);
  }

  /**
   * 生成普通任务计划代码
   *
   * @return
   */
  @SneakyThrows
  private String generateAssignableTaskPlanCode(String tenant) {
    String code = billNumberMgr.generateAssignableTaskPlanCode(tenant);
    if (StringUtils.isEmpty(code) || code.length() < 8) {
      throw new BaasException("普通任务计划code生成失败");
    }
    return code.substring(1);
  }

  /**
   * 发布普通任务计划
   *
   * @param tenant   租户
   * @param taskPlan 计划
   */
  private void publishAssignableTaskPlan(String tenant, TaskPlan taskPlan) {
    AssignableTaskPlanMsg assignableTaskPlanMsg = new AssignableTaskPlanMsg();
    assignableTaskPlanMsg.setTaskPlan(taskPlan);
    assignableTaskPlanMsg.setTenant(tenant);
    assignableTaskPlanMsg.setTraceId(IdGenUtils.buildRdUuid());
    assignableTaskPlanMsg.setCreateDate(new Date());
    publisher.publishForNormal(AssignableTaskPlanPublishEvCallExecutor.ASSIGNABLE_TASK_PLAN_PUBLISH_EXECUTOR_ID,
        assignableTaskPlanMsg);
  }

  private void buildItemAndLine(String tenant, TaskPlan taskPlan, List<TaskPlanItem> targetItem,
                                List<TaskPlanLine> targetLine) {
    // 保存明细item
    if (CollectionUtils.isNotEmpty(taskPlan.getItems())) {
      List<TaskPlanItem> items = taskPlan.getItems();
      List<TaskPlanLine> lines = taskPlan.getLines();
      // 保存item、line
      for (TaskPlanItem taskPlanItem : items) {
        taskPlanItem.setUuid(IdGenUtils.buildRdUuid());
        taskPlanItem.setOwner(taskPlan.getUuid());
        taskPlanItem.setTenant(tenant);
        targetItem.add(taskPlanItem);
        for (TaskPlanLine taskPlanLine : lines) {
          taskPlanLine.setUuid(IdGenUtils.buildRdUuid());
          taskPlanLine.setOwner(taskPlan.getUuid());
          taskPlanLine.setTenant(tenant);
          taskPlanLine.setTaskPlanItemId(taskPlanItem.getUuid());
          targetLine.add(taskPlanLine);
        }
      }
    }
  }

  private Date getWeekStartDate(TaskPlan taskPlan) throws BaasException {
    int weekOfDate = getWeekOfDate();
    String startDateTmp = YYMMDD_SPACE.format(new Date()) + taskPlan.getPublishDate() + ":00";
    Date startDate = null;
    try {
      startDate = YYMMDD_HHMMSS.parse(startDateTmp);
    } catch (ParseException e) {
      log.error("taskPlan时间转换失败，taskPlan:{}", JSONUtil.safeToJson(taskPlan));
      throw new BaasException("taskPlan开始时间转换失败");
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    calendar.add(Calendar.DATE, taskPlan.getDayOfWeek() - weekOfDate);
    return calendar.getTime();
  }

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

  private Date getMonthStartDate(TaskPlan taskPlan) throws BaasException {
    int monthOfDay = getMonthOfDay();
    String startDateTmp = YYMMDD_SPACE.format(new Date()) + taskPlan.getPublishDate() + ":00";
    Date startDate = null;
    try {
      startDate = YYMMDD_HHMMSS.parse(startDateTmp);
    } catch (ParseException e) {
      log.error("taskPlan时间转换失败，taskPlan:{}", JSONUtil.safeToJson(taskPlan));
      throw new BaasException("taskPlan开始时间转换失败");
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    calendar.add(Calendar.DATE, getEffectiveDayOfMonth(taskPlan.getDayOfMonth()) - monthOfDay);
    return calendar.getTime();
  }

  private static int getMonthOfDay() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
    String format = simpleDateFormat.format(new Date());
    return Integer.parseInt(format);
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
